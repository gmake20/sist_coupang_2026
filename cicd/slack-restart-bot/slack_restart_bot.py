import os
import subprocess
import logging

from slack_bolt import App
from slack_bolt.adapter.socket_mode import SocketModeHandler

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger("slack-restart-bot")

SLACK_BOT_TOKEN = os.environ["SLACK_BOT_TOKEN"]
SLACK_APP_TOKEN = os.environ["SLACK_APP_TOKEN"]
RESTART_SCRIPT_PATH = os.environ.get("RESTART_SCRIPT_PATH", "/root/restart.sh")
RESTART_TIMEOUT_SEC = int(os.environ.get("RESTART_TIMEOUT_SEC", "180"))
LEGACY_SCRIPT_PATH = os.environ.get("LEGACY_SCRIPT_PATH", "/root/deploy_legacy.sh")
LEGACY_TIMEOUT_SEC = int(os.environ.get("LEGACY_TIMEOUT_SEC", "600"))

# "/restart <target>" 의 target → (표시 이름, 스크립트, 타임아웃). target 생략 시 "" (Spring Legacy)
TARGETS = {
    "": ("GoodPangSpringLegacy (Tomcat 9)", LEGACY_SCRIPT_PATH, LEGACY_TIMEOUT_SEC),
    "old": ("GoodPang (Tomcat 10)", RESTART_SCRIPT_PATH, RESTART_TIMEOUT_SEC),
}

ALLOWED_USER_IDS = {
    uid.strip()
    for uid in os.environ.get("ALLOWED_SLACK_USER_IDS", "").split(",")
    if uid.strip()
}
ALLOWED_CHANNEL_ID = os.environ.get("ALLOWED_SLACK_CHANNEL_ID", "").strip()

app = App(token=SLACK_BOT_TOKEN)


def is_authorized(user_id: str, channel_id: str) -> bool:
    if ALLOWED_USER_IDS and user_id not in ALLOWED_USER_IDS:
        return False
    if ALLOWED_CHANNEL_ID and channel_id != ALLOWED_CHANNEL_ID:
        return False
    return True


def truncate(text: str, limit: int = 3500) -> str:
    # 에러 원인은 보통 로그 끝에 있으므로 뒷부분을 남긴다
    if len(text) <= limit:
        return text
    return "(truncated)...\n" + text[-limit:]


@app.command("/restart")
def handle_restart(ack, respond, command, say):
    ack()

    user_id = command["user_id"]
    channel_id = command["channel_id"]

    if not is_authorized(user_id, channel_id):
        respond("이 명령을 실행할 권한이 없습니다.")
        logger.warning("Unauthorized /restart attempt by user=%s channel=%s", user_id, channel_id)
        return

    target = (command.get("text") or "").strip().lower()
    if target not in TARGETS:
        respond("사용법: `/restart` (GoodPangSpringLegacy) 또는 `/restart old` (기존 GoodPang)")
        return
    name, script_path, timeout_sec = TARGETS[target]

    say(f"<@{user_id}> {name} 배포를 시작합니다...")
    logger.info("Restart triggered by user=%s target=%s", user_id, target or "legacy")

    try:
        result = subprocess.run(
            ["bash", script_path],
            capture_output=True,
            text=True,
            timeout=timeout_sec,
        )
    except subprocess.TimeoutExpired:
        say(f"{name} 배포 스크립트가 {timeout_sec}초 내에 끝나지 않았습니다. 서버에서 직접 확인해주세요.")
        logger.exception("Restart script timed out")
        return
    except Exception as exc:
        say(f"재시작 스크립트 실행 중 오류가 발생했습니다: {exc}")
        logger.exception("Restart script failed to run")
        return

    if result.returncode == 0:
        say(f":white_check_mark: {name} 배포가 완료되었습니다.\n```" + truncate(result.stdout or "(출력 없음)") + "```")
    else:
        say(
            f":x: {name} 배포 스크립트가 종료 코드 {result.returncode}로 실패했습니다.\n"
            "```" + truncate((result.stdout or "") + "\n" + (result.stderr or "")) + "```"
        )
    logger.info("Restart finished with returncode=%s", result.returncode)


if __name__ == "__main__":
    handler = SocketModeHandler(app, SLACK_APP_TOKEN)
    handler.start()
