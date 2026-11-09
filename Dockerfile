FROM eclipse-temurin:21-jre-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 비관리자 계정 생성
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

#  파일 복사 및 권한 설정
COPY --chown=appuser:appgroup build/libs/app.jar app.jar

# 비관리자 계정으로 전환
USER appuser

#  포트 설정
EXPOSE 8080

# 실행
ENTRYPOINT ["java", "-Xms256m", "-Xmx512m", "-XX:+UseG1GC", "-jar", "app.jar"]
