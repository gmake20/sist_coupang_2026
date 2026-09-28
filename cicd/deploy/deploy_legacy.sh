#!/bin/bash
# GoodPangSpringLegacy 배포 스크립트
#   git fetch/reset → db.properties 주입 → mvn package → 기존 war 백업
#   → Tomcat 9 중지 → ROOT.war 교체 → Tomcat 시작 → 헬스체크 → 실패 시 롤백
set -euo pipefail

SRC_REPO_URL="https://github.com/gmake20/sist_coupang_2026.git"
SRC_REPO_DIR="/root/git/sist_coupang_2026"
DEPLOY_BRANCH="main"
PROJECT_DIR="$SRC_REPO_DIR/project/GoodPangSpringLegacy"

CONFIG_DIR="/root/goodpang-config"            # git 밖에 두는 시크릿 (db.properties)
TOMCAT_HOME="/opt/tomcat9"                    # javax.servlet 기반이라 Tomcat 9 필수
APP_PORT="8080"
HEALTH_URL="http://localhost:${APP_PORT}/"
HEALTH_TIMEOUT_SEC=120
BACKUP_WAR="/root/goodpang-backup/ROOT.war"

export JAVA_HOME="${JAVA_HOME:-/usr/lib/jvm/java-11-openjdk-amd64}"
export PATH="$JAVA_HOME/bin:$PATH"

# 동시 실행 방지
exec 9>/tmp/deploy_legacy.lock
if ! flock -n 9; then
    echo "이미 다른 배포가 진행 중입니다."
    exit 1
fi

log() { echo "[$(date '+%H:%M:%S')] $*"; }

tomcat_running() {
    pgrep -f "catalina.base=${TOMCAT_HOME}" > /dev/null
}

# systemd(tomcat9.service)로 등록돼 있으면 systemctl을, 아니면 bin 스크립트를 사용
use_systemd() {
    systemctl is-enabled tomcat9 > /dev/null 2>&1
}

start_tomcat() {
    log "Tomcat 시작"
    if use_systemd; then
        systemctl start tomcat9
    else
        # 9>&- : 잠금 fd를 닫고 실행. 안 닫으면 Tomcat JVM이 잠금을 물려받아 다음 배포가 막힘
        "$TOMCAT_HOME/bin/startup.sh" > /dev/null 9>&-
    fi
}

stop_tomcat() {
    log "Tomcat 중지"
    if use_systemd; then
        systemctl stop tomcat9 || true
    else
        "$TOMCAT_HOME/bin/shutdown.sh" > /dev/null 2>&1 || true
    fi
    for _ in $(seq 1 30); do
        tomcat_running || return 0
        sleep 1
    done
    log "30초 내 종료되지 않아 강제 종료"
    pkill -9 -f "catalina.base=${TOMCAT_HOME}" || true
    sleep 2
}

install_war() {
    rm -rf "$TOMCAT_HOME/webapps/ROOT" "$TOMCAT_HOME/work/Catalina/localhost/ROOT"
    cp "$1" "$TOMCAT_HOME/webapps/ROOT.war"
}

health_check() {
    for _ in $(seq 1 "$HEALTH_TIMEOUT_SEC"); do
        code=$(curl -s -o /dev/null -w '%{http_code}' "$HEALTH_URL" || true)
        case "$code" in
            2??|3??) log "헬스체크 통과 (HTTP $code)"; return 0 ;;
        esac
        sleep 1
    done
    log "헬스체크 실패 (마지막 HTTP ${code:-none})"
    return 1
}

# 1. 소스 동기화 (서버 로컬 변경은 버리고 원격 브랜치와 동일하게 맞춤)
if [ -d "$SRC_REPO_DIR/.git" ]; then
    cd "$SRC_REPO_DIR"
    git fetch origin "$DEPLOY_BRANCH"
    git reset --hard "origin/$DEPLOY_BRANCH"
else
    git clone -b "$DEPLOY_BRANCH" "$SRC_REPO_URL" "$SRC_REPO_DIR"
    cd "$SRC_REPO_DIR"
fi
git log -1 --format='배포 커밋: %h %s (%an)'

# 2. 시크릿 주입 (.gitignore 대상이라 저장소에 없음)
cp "$CONFIG_DIR/db.properties" "$PROJECT_DIR/src/main/resources/db.properties"

# 3. 빌드 — 실패하면 set -e로 여기서 중단되고 기존 서비스는 그대로 유지
cd "$PROJECT_DIR"
log "Maven 빌드 시작"
mvn -B -q clean package -DskipTests -Dproject.build.sourceEncoding=UTF-8
WAR_OUT=$(ls target/*.war | head -1)
log "빌드 완료: $WAR_OUT"

# 4. 현재 배포본 백업
mkdir -p "$(dirname "$BACKUP_WAR")"
if [ -f "$TOMCAT_HOME/webapps/ROOT.war" ]; then
    cp "$TOMCAT_HOME/webapps/ROOT.war" "$BACKUP_WAR"
fi

# 5. 교체 및 기동
stop_tomcat
install_war "$WAR_OUT"
start_tomcat

# 6. 헬스체크 실패 시 롤백
if ! health_check; then
    tail -n 60 "$TOMCAT_HOME/logs/catalina.out" || true
    if [ -f "$BACKUP_WAR" ]; then
        log "이전 버전으로 롤백"
        stop_tomcat
        install_war "$BACKUP_WAR"
        start_tomcat
        health_check || log "롤백 후에도 기동 실패 — 서버 확인 필요"
    fi
    exit 1
fi

log "배포 완료"
