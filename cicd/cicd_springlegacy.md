# GoodPangSpringLegacy 배포 (Tomcat 9 + Maven + Slack)

`project/GoodPangSpringLegacy`(Spring 5 MVC)를 Ubuntu 서버(`scym3`, 계정 `root`)에 배포하는 구성과 절차.
기존 `project/GoodPang`(서블릿/JSP) 배포는 [README.md](README.md) 참고.

## 1. 개요

```
로컬 수정 → feature 브랜치 push → PR → main 병합
        ↓
Slack: /restart            → GoodPangSpringLegacy 배포 (Tomcat 9, 8080)
       /restart old        → 기존 GoodPang 배포 (Tomcat 10, 8081)
        ↓
slack_restart_bot.py → /root/deploy_legacy.sh
        ↓
git fetch/reset → db.properties 주입 → mvn package → 기존 war 백업
→ Tomcat 9 중지 → ROOT.war 교체 → 시작 → 헬스체크 → (실패 시 자동 롤백)
        ↓
결과(성공/실패/롤백 로그)를 Slack 채널에 응답
```

| 구분 | GoodPang (기존) | GoodPangSpringLegacy |
|---|---|---|
| Slack 명령 | `/restart old` | `/restart` |
| 배포 스크립트 | `/root/restart.sh` | `/root/deploy_legacy.sh` |
| 빌드 방식 | `javac` + `jar` | `mvn clean package` |
| 서블릿 API | `jakarta.*` | `javax.servlet` |
| Tomcat | `/opt/tomcat` (10.x), **8081** | `/opt/tomcat9` (9.0.x), **8080** |
| shutdown 포트 | 8005 | 8006 |
| JDK | 서버 기본 | 11 |

### 왜 Tomcat 9를 따로 쓰나

Spring 5.0.7은 `javax.servlet` 기반이라 `jakarta.*`를 쓰는 Tomcat 10 이상에서는 기동되지 않는다.
그래서 Tomcat 9를 별도 설치해 두 버전을 병행 운영한다.

## 2. 관련 파일

| 저장소 경로 | 서버 배치 경로 | 역할 |
|---|---|---|
| `cicd/deploy/deploy_legacy.sh` | `/root/deploy_legacy.sh` | Spring Legacy 빌드/배포 스크립트 |
| `cicd/slack-restart-bot/slack_restart_bot.py` | `/root/slack-restart-bot/slack_restart_bot.py` | `/restart`, `/restart old` 처리 봇 |
| (git 밖) | `/root/goodpang-config/db.properties` | DB 접속 정보 (시크릿) |
| (git 밖) | `/opt/tomcat9/bin/setenv.sh` | JDK, 업로드 경로, JVM 옵션 |
| (git 밖) | `/opt/tomcat9/conf/Catalina/localhost/ROOT.xml` | `/upload` URL → 업로드 디렉토리 매핑 |
| (git 밖) | `/var/goodpang/upload` | 업로드 파일 저장소 (재배포해도 유지) |
| (git 밖) | `/root/goodpang-backup/ROOT.war` | 직전 배포본 (롤백용) |

> 배포 스크립트는 저장소에서 `/root`로 **복사해서** 실행한다. 스크립트가 실행 도중 `git reset`으로
> 저장소 파일을 바꾸기 때문에, 저장소 안의 파일을 직접 실행하면 bash가 읽던 파일이 바뀌어 동작이 꼬일 수 있다.

## 3. 서버 최초 설정 (1회)

### 3.1 JDK 11, Maven

```bash
apt update
apt install -y openjdk-11-jdk maven curl
ls /usr/lib/jvm/          # java-11-openjdk-amd64 확인
```

`mvn -v`에 다른 Java 버전이 보여도 된다. 배포 스크립트가 `JAVA_HOME`을 JDK 11로 지정한다.

### 3.2 Tomcat 9 설치

```bash
cd /tmp
VER=$(curl -s https://dlcdn.apache.org/tomcat/tomcat-9/ | grep -oP 'v9\.0\.\d+' | sort -V | tail -1 | tr -d v)
curl -O https://dlcdn.apache.org/tomcat/tomcat-9/v$VER/bin/apache-tomcat-$VER.tar.gz
curl -O https://dlcdn.apache.org/tomcat/tomcat-9/v$VER/bin/apache-tomcat-$VER.tar.gz.sha512
sha512sum -c apache-tomcat-$VER.tar.gz.sha512      # OK 확인

tar xzf apache-tomcat-$VER.tar.gz -C /opt
mv /opt/apache-tomcat-$VER /opt/tomcat9
rm -rf /opt/tomcat9/webapps/{ROOT,docs,examples,manager,host-manager}
chmod +x /opt/tomcat9/bin/*.sh
```

`manager`와 `host-manager`는 쓰지 않으므로 보안상 삭제한다.

### 3.3 포트 설정 (`/opt/tomcat9/conf/server.xml`)

Tomcat 10과 겹치지 않도록 shutdown 포트와 redirect 포트를 바꾼다.

```bash
cp /opt/tomcat9/conf/server.xml /opt/tomcat9/conf/server.xml.orig
sed -i 's/<Server port="8005"/<Server port="8006"/' /opt/tomcat9/conf/server.xml
sed -i 's/redirectPort="8443"/redirectPort="8444"/' /opt/tomcat9/conf/server.xml
```

현재 HTTP Connector 설정은 다음과 같다(9.0.122 기준).

```xml
<Connector port="8080" protocol="HTTP/1.1"
           connectionTimeout="20000"
           redirectPort="8444"
           maxParameterCount="1000" />
```

### 3.4 실행 환경 (`/opt/tomcat9/bin/setenv.sh`)

```bash
cat > /opt/tomcat9/bin/setenv.sh <<'EOF'
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export UPLOAD_BASE_DIR=/var/goodpang/upload
export CATALINA_OPTS="-Dfile.encoding=UTF-8 -Duser.timezone=Asia/Seoul -Xms256m -Xmx512m"
EOF
chmod +x /opt/tomcat9/bin/setenv.sh
mkdir -p /var/goodpang/upload
```

- `UPLOAD_BASE_DIR`: `UploadPaths.resolveBaseDir()`가 읽는 값. 지정하지 않으면 업로드 파일이
  `webapps/ROOT/upload`에 저장돼 **재배포할 때마다 삭제된다**.
- `-Xmx`는 서버 여유 메모리(`free -h`)를 보고 조정한다.

### 3.5 업로드 URL 매핑 (`/opt/tomcat9/conf/Catalina/localhost/ROOT.xml`)

```bash
mkdir -p /opt/tomcat9/conf/Catalina/localhost
cat > /opt/tomcat9/conf/Catalina/localhost/ROOT.xml <<'EOF'
<Context>
  <Resources>
    <PostResources className="org.apache.catalina.webresources.DirResourceSet"
                   base="/var/goodpang/upload" webAppMount="/upload"/>
  </Resources>
</Context>
EOF
```

심볼릭 링크와 달리 war를 다시 풀어도 설정이 유지된다.
첫 배포 전에는 ROOT 앱이 없어서 `The main resource set specified [/opt/tomcat9/webapps/ROOT] is not a directory`
에러가 로그에 남는데, 첫 배포 후 사라지므로 무시해도 된다.

### 3.6 DB 설정 (`/root/goodpang-config/db.properties`)

`db.properties`는 `.gitignore` 대상이라 저장소에 없다. 서버에 직접 만든다.

```bash
mkdir -p /root/goodpang-config && chmod 700 /root/goodpang-config
cat > /root/goodpang-config/db.properties <<'EOF'
jdbc.driver=net.sf.log4jdbc.sql.jdbcapi.DriverSpy
jdbc.url=jdbc:log4jdbc:oracle:thin:@scym3.cafe24.com:1521/XEPDB1
jdbc.username=<계정>
jdbc.password=<비밀번호>
EOF
chmod 600 /root/goodpang-config/db.properties
```

- 주소는 기존 GoodPang(`META-INF/context.xml`)과 같은 **공용 DB**를 쓴다. `localhost`는 각자 PC의 로컬 DB용이다.
- 반드시 원본(`/root/goodpang-config/db.properties`)을 수정한다. `src/main/resources/db.properties`는
  배포 때마다 원본으로 덮어써지는 복사본이다.

적용 흐름:

```
/root/goodpang-config/db.properties
   → (스크립트가 빌드 직전 복사) src/main/resources/db.properties
   → (mvn package) war 안의 WEB-INF/classes/db.properties
   → root-context.xml 의 <context:property-placeholder location="classpath:db.properties"/>
```

### 3.7 (선택) systemd 등록

재부팅 시 자동 시작이 필요하면 등록한다. 등록돼 있으면 배포 스크립트가 자동으로 `systemctl`을 사용한다.

```bash
cat > /etc/systemd/system/tomcat9.service <<'EOF'
[Unit]
Description=Apache Tomcat 9 (GoodPangSpringLegacy)
After=network.target

[Service]
Type=forking
User=root
Environment=CATALINA_HOME=/opt/tomcat9
Environment=CATALINA_BASE=/opt/tomcat9
Environment=CATALINA_PID=/opt/tomcat9/temp/tomcat.pid
ExecStart=/opt/tomcat9/bin/startup.sh
ExecStop=/opt/tomcat9/bin/shutdown.sh
PIDFile=/opt/tomcat9/temp/tomcat.pid

[Install]
WantedBy=multi-user.target
EOF
systemctl daemon-reload
systemctl enable --now tomcat9
```

### 3.8 배포 스크립트, 봇 배치

```bash
cd /root/git/sist_coupang_2026 && git pull
cp cicd/deploy/deploy_legacy.sh /root/deploy_legacy.sh
chmod +x /root/deploy_legacy.sh
cp cicd/slack-restart-bot/slack_restart_bot.py /root/slack-restart-bot/
systemctl restart slack-restart-bot
```

### 3.9 첫 배포는 서버에서 직접

Maven 의존성을 처음 내려받느라 수 분이 걸리므로 Slack이 아니라 서버에서 실행한다.

```bash
bash /root/deploy_legacy.sh
```

`헬스체크 통과` → `배포 완료`가 출력되면 성공. 헬스체크(최대 120초) 중에 Ctrl+C로 중단하지 않는다.
중단하면 자동 롤백이 실행되지 않는다.

## 4. `deploy_legacy.sh` 동작

| 단계 | 내용 | 실패 시 |
|---|---|---|
| 잠금 | `flock /tmp/deploy_legacy.lock`으로 동시 실행 방지 | "이미 다른 배포가 진행 중입니다." 후 종료 |
| 소스 동기화 | `git fetch origin main` + `git reset --hard origin/main` | 중단 (서비스 영향 없음) |
| 시크릿 주입 | `/root/goodpang-config/db.properties` → `src/main/resources/` | 중단 (서비스 영향 없음) |
| 빌드 | `mvn -B -q clean package -DskipTests` | 중단 (서비스 영향 없음) |
| 백업 | 현재 `webapps/ROOT.war` → `/root/goodpang-backup/ROOT.war` | - |
| 교체 | Tomcat 중지(30초 초과 시 강제 종료) → `ROOT`, `work` 캐시 삭제 → 새 war 복사 → 시작 | - |
| 헬스체크 | `curl http://localhost:8080/`이 2xx/3xx가 될 때까지 최대 120초 | catalina.out 마지막 60줄 출력 → 백업 war로 **자동 롤백** → 종료 코드 1 |

주요 변수 (스크립트 상단):

| 변수 | 값 |
|---|---|
| `DEPLOY_BRANCH` | `main` (테스트 시 잠시 다른 브랜치로 변경 가능, 원격에 push된 브랜치여야 함) |
| `TOMCAT_HOME` | `/opt/tomcat9` |
| `APP_PORT` | `8080` |
| `HEALTH_TIMEOUT_SEC` | `120` |
| `CONFIG_DIR` | `/root/goodpang-config` |
| `BACKUP_WAR` | `/root/goodpang-backup/ROOT.war` |

> 서버의 `/root/git/sist_coupang_2026`는 기존 GoodPang(`restart.sh`)과 함께 쓰는 폴더다.
> 배포할 때마다 `origin/main`과 똑같이 맞춰지므로 서버에서 이 폴더의 파일을 직접 고치지 않는다.

## 5. Slack 봇

| 입력 | 동작 |
|---|---|
| `/restart` | GoodPangSpringLegacy 배포 (`LEGACY_SCRIPT_PATH`, 타임아웃 `LEGACY_TIMEOUT_SEC`) |
| `/restart old` | 기존 GoodPang 배포 (`RESTART_SCRIPT_PATH`, 타임아웃 `RESTART_TIMEOUT_SEC`) |
| 그 외 | 사용법 안내만 응답 |

`/root/slack-restart-bot/.env` 관련 변수:

| 변수 | 기본값 | 설명 |
|---|---|---|
| `LEGACY_SCRIPT_PATH` | `/root/deploy_legacy.sh` | `/restart`가 실행할 스크립트 |
| `LEGACY_TIMEOUT_SEC` | `600` | Maven 빌드 + 헬스체크 + 롤백까지 고려한 타임아웃 |
| `RESTART_SCRIPT_PATH` | `/root/restart.sh` | `/restart old`가 실행할 스크립트 |
| `RESTART_TIMEOUT_SEC` | `180` | 기존 GoodPang용 |

실패 시 Slack에는 로그의 **뒷부분**(최대 3500자)이 표시된다. 에러 원인은 보통 로그 끝에 있기 때문이다.

## 6. 운영

| 작업 | 방법 |
|---|---|
| 코드 배포 | main 병합 후 Slack `/restart` |
| DB 비밀번호 변경 | `/root/goodpang-config/db.properties` 수정 → `/restart` (war에 포함되므로 재빌드 필요) |
| 배포 스크립트 수정 | 저장소 수정 → main 병합 → 서버에서 `git pull` 후 `cp cicd/deploy/deploy_legacy.sh /root/` |
| 봇 수정 | 저장소 수정 → main 병합 → 서버에서 `cp` 후 `systemctl restart slack-restart-bot` |
| 수동 롤백 | Tomcat 중지 → `rm -rf /opt/tomcat9/webapps/ROOT` → `cp /root/goodpang-backup/ROOT.war /opt/tomcat9/webapps/` → 시작 |
| 로그 확인 | `/opt/tomcat9/logs/catalina.out`, Spring 초기화 에러는 `/opt/tomcat9/logs/localhost.YYYY-MM-DD.log` |

## 7. 파일 업로드 제한

Spring Legacy는 `CommonsMultipartResolver`(commons-fileupload 1.5)가 요청을 직접 파싱한다.
따라서 **Tomcat Connector의 `maxPartCount` 등은 적용되지 않고**, 제한은
`servlet-context.xml`의 `multipartResolver`에서 설정한다.

현재는 `maxUploadSize="-1"`(무제한)이다. 기존 GoodPang(`@MultipartConfig`)과 같은 기준으로 맞추는 권장 설정:

```xml
<beans:bean id="multipartResolver"
  class="org.springframework.web.multipart.commons.CommonsMultipartResolver">
  <beans:property name="maxUploadSize" value="157286400"/>        <!-- 요청 전체 150MB -->
  <beans:property name="maxUploadSizePerFile" value="10485760"/>  <!-- 파일 1개 10MB -->
  <beans:property name="maxInMemorySize" value="10240"/>
  <beans:property name="defaultEncoding" value="UTF-8"/>
</beans:bean>
```

참고로 기존 GoodPang(Tomcat 10)은 Tomcat이 multipart를 파싱하므로 `server.xml` Connector의
`maxPartCount`(10.1.42 이상 기본값 10, 일반 입력 필드도 1개로 셈)에 걸릴 수 있다.

## 8. 트러블슈팅 기록

### `FileNotFoundException: class path resource [org/doit/goodpang/mapper/mybatis-config.xml]`

- 원인: 파일이 `src/main/java`에 있었다. Maven은 `src/main/java`에서 `.java`만 컴파일하고 다른 파일은
  war에 넣지 않는다. Eclipse 빌드는 복사해 주기 때문에 로컬에서는 정상이었다.
- 조치: `src/main/resources/org/doit/goodpang/mapper/`로 이동.
- 교훈: MyBatis 설정·매퍼 XML 등 비-Java 파일은 반드시 `src/main/resources`에 둔다.
  확인: `unzip -l target/*.war | grep mybatis-config`

### "이미 다른 배포가 진행 중입니다."가 계속 나옴

- 원인: 스크립트가 잡은 flock 잠금 파일(fd 9)을 `startup.sh`로 띄운 Tomcat JVM이 상속받아
  스크립트 종료 후에도 잠금이 유지됐다. 확인: `fuser -v /tmp/deploy_legacy.lock` → java 프로세스.
- 조치: `startup.sh ... 9>&-`로 fd를 닫고 실행하도록 수정. 이미 잠겨 있으면 Tomcat을 한 번 내렸다 올린다.

### 앱 기동 실패 후 `curl`이 404 (`Context [] startup failed due to previous errors`)

- Tomcat은 떴지만 ROOT 앱이 실패한 상태. 원인은 `localhost.YYYY-MM-DD.log`의 **마지막 `Caused by:`** 에 있다.

```bash
grep -n -A3 "SEVERE\|Caused by" /opt/tomcat9/logs/localhost.$(date +%F).log | tail -60
```

| 로그 | 원인 |
|---|---|
| `ORA-12541`, `Connection refused` | DB 주소/포트 문제 (`localhost` 대신 공용 DB 주소 사용) |
| `ORA-12514` | 서비스명(XEPDB1) 불일치 |
| `ORA-01017` | 계정/비밀번호 오류 |
| `Could not resolve placeholder 'jdbc.url'` | `db.properties` 키 누락 |
| `FileNotFoundException ... .xml` | 파일 위치 또는 **대소문자 불일치** (Linux는 구분함) |

`HikariPool ... is shutting down`은 다른 에러로 기동이 취소되며 함께 나오는 로그이므로 DB 문제로 단정하지 않는다.

### 첫 기동 시 `The main resource set specified [/opt/tomcat9/webapps/ROOT] is not a directory`

- 원인: `ROOT.xml`은 있는데 아직 배포된 `ROOT.war`가 없음.
- 조치: 첫 배포 후 자동 해결. 거슬리면 `mkdir -p /opt/tomcat9/webapps/ROOT`.

## 9. 남은 개선 과제

- `multipartResolver`의 `maxUploadSize`가 `-1`(무제한)이다. 7장의 권장 값으로 제한한다.
- `db.properties`가 war 안에 포함된다. `root-context.xml`에서
  `location="classpath:db.properties, file:/root/goodpang-config/db.properties"`,
  `ignore-resource-not-found="true"`로 바꾸면 war에서 비밀번호를 빼고, 비밀번호 변경 시 재빌드 없이
  Tomcat 재시작만으로 적용할 수 있다.
- `GoodPang/src/main/webapp/META-INF/context.xml`에 공용 DB 계정·비밀번호가 커밋돼 있다.
  비밀번호 변경과 1521 포트 외부 접근 제한을 검토한다.
- `customLoginSuccessHandler`가 컴포넌트 스캔과 `security-context.xml`에 이중 정의돼 있다(동작에는 영향 없음).
- 봇과 배포 스크립트가 모두 `root`로 실행된다. 전용 계정 분리를 검토한다.
