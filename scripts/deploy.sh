#!/bin/bash
set -euo pipefail

# === 필수 환경변수 ===
AWS_REGION="${AWS_REGION:-ap-southeast-2}"
ECR_REGISTRY="${ECR_REGISTRY:?ERROR: ECR_REGISTRY must be set}"
IMAGE_TAG="${IMAGE_TAG:?ERROR: IMAGE_TAG must be set}"
GITHUB_REPO="${GITHUB_REPO:?ERROR: GITHUB_REPO must be set}"
COMMIT_SHA="${COMMIT_SHA:-main}"

APP_DIR="/home/ubuntu/app"
mkdir -p "$APP_DIR/nginx/conf.d"
mkdir -p "$APP_DIR/nginx/certbot/conf" "$APP_DIR/nginx/certbot/www"
cd "$APP_DIR"

echo "=== 같이가개 배포 ==="
echo "Image: ${ECR_REGISTRY}:${IMAGE_TAG}"

# === 최신 설정 파일 fetch (GitHub raw) ===
echo "[0/6] 설정 파일 가져오기"
RAW_BASE="https://raw.githubusercontent.com/${GITHUB_REPO}/${COMMIT_SHA}"

curl -fsSL "${RAW_BASE}/docker-compose.prod.yaml" -o docker-compose.prod.yaml
curl -fsSL "${RAW_BASE}/nginx/nginx.conf" -o nginx/nginx.conf
curl -fsSL "${RAW_BASE}/nginx/conf.d/default.conf" -o nginx/conf.d/default.conf

# === ECR 로그인 ===
echo "[1/6] ECR 로그인"
REGISTRY_HOST="${ECR_REGISTRY%%/*}"
aws ecr get-login-password --region "${AWS_REGION}" \
  | docker login --username AWS --password-stdin "${REGISTRY_HOST}"

# === 새 이미지 pull ===
echo "[2/6] 이미지 pull"
docker pull "${ECR_REGISTRY}:${IMAGE_TAG}"

# === Parameter Store에서 환경변수 fetch ===
echo "[3/6] 환경변수 로드"
: > .env
chmod 600 .env

cat >> .env << EOF
SPRING_PROFILES_ACTIVE=prod
ECR_REGISTRY=${ECR_REGISTRY}
IMAGE_TAG=${IMAGE_TAG}
EOF

aws ssm get-parameters-by-path \
  --path "/gatigagae/prod/" \
  --with-decryption \
  --region "${AWS_REGION}" \
  --query "Parameters[*].[Name,Value]" \
  --output text \
  | while IFS=$'\t' read -r name value; do
      key="${name##*/}"
      echo "${key}=${value}" >> .env
    done

PARAM_COUNT=$(grep -c '=' .env || true)
echo "    로드된 환경변수: ${PARAM_COUNT}개"

# === 필수 환경변수 검증 ===
REQUIRED_VARS=(
  # DB
  "POSTGRES_HOST"
  "POSTGRES_PORT"
  "POSTGRES_DB"
  "POSTGRES_USER"
  "POSTGRES_PASSWORD"
  # SERVER URL
  "SERVER_URL"
  # S3
  "S3_REGION"
  "S3_BUCKET"
)

MISSING=()
for var in "${REQUIRED_VARS[@]}"; do
  if ! grep -qE "^${var}=.+" .env; then
    MISSING+=("$var")
  fi
done

if [ ${#MISSING[@]} -gt 0 ]; then
  echo "❌ 필수 환경변수 누락 또는 값 비어있음:"
  for var in "${MISSING[@]}"; do
    echo "    - ${var}"
  done
  exit 1
fi

echo "    필수 환경변수 ${#REQUIRED_VARS[@]}개 모두 존재 확인"

# === 새 컨테이너 기동 ===
echo "[4/6] 컨테이너 가동"
docker compose -f docker-compose.prod.yaml up -d

# === Health Check ===
echo "[5/6] Health Check"
HEALTH_OK=false
for i in $(seq 1 60); do
  if curl -fs http://localhost:8080/actuator/health > /dev/null 2>&1; then
    echo "    [$i/60]  Health Check 성공"
    HEALTH_OK=true
    break
  fi
  echo "    [$i/60] 대기중..."
  sleep 3
done

if [ "$HEALTH_OK" = false ]; then
  echo "❌  Health Check 실패. 컨테이너 로그:"
  docker logs gatigagae-app --tail 50
  exit 1
fi

# === Nginx reload ===
echo "[6/6] Nginx reload"
docker exec gatigagae-nginx nginx -t
docker exec gatigagae-nginx nginx -s reload

docker image prune -af > /dev/null

echo "✅ 배포 완료 (${IMAGE_TAG})"