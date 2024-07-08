#!/bin/bash

IS_APP1=$(docker ps | grep container1)
IS_REDIS=$(docker ps | grep redis)
MAX_RETRIES=30

#옵션 정리글 https://lxstitch.tistory.com/65
#return: 함수 종료하는 키워드. main 함수에서 사용하면, 프로그램 바로 종료됨.
#exit: main함수가 아닌 곳에서 호출되어도, 바로 프로그램 종료됨.

#exit 0: 정상 작동후 종료
#exit 1: 에러후 강제 종료

#if문
# -z string: 문자열의 길이가 0이면 참
# -n string: 문자열의 길이가 0아 아니면 참

#산술 비교
#[A -lt B]: A Less Then B 이면 참.
#[A -le B]: A Less Equal B
#-ne: Not equal
#-gt: Grater Then
#-ge: Grater Equal

# 헬스 체크 함수 (Using HTTP Request)
check_service() {
  local RETRIES=0
  local URL=$1
  while [ $RETRIES -lt $MAX_RETRIES ]; do
    echo "Checking service at $URL... (attempt: $((RETRIES+1)))"
    sleep 3

    REQUEST=$(curl $URL)
    if [ -n "$REQUEST" ]; then
      echo "health check success"
      return 0
    fi

    RETRIES=$((RETRIES+1))
  done;

  echo "Failed to check service after $MAX_RETRIES attempts."
  return 1
}

# Redis 헬스 체크 함수 (Using redis-cli)
# Redis 서버는 HTTP Request 허용 X -> redis-cli ping 사용
check_redis() {
  local RETRIES=0
  while [ $RETRIES -lt $MAX_RETRIES ]; do
    echo "Checking Redis... (attempt: $((RETRIES+1)))"
    sleep 3

    REQUEST=$(sudo docker exec redis redis-cli ping)
    if [ "$REQUEST" == "PONG" ]; then
      echo "Redis health check success"
      return 0
    fi

    RETRIES=$((RETRIES+1))
  done;

  echo "Failed to check Redis after $MAX_RETRIES attempts."
  return 1
}

# Check if redis is running, if not, start it and perform a health check
if [ -z "$IS_REDIS" ]; then
  echo "Redis 컨테이너가 실행되고 있지 않습니다. Redis 컨테이너를 시작합니다."
  docker-compose up -d redis

  echo "Redis health check"
  if ! check_redis; then
    echo "Redis health check가 실패했습니다."
    exit 1
  fi
else
  echo "Redis 컨테이너가 이미 실행 중입니다."
fi

if [ -z "$IS_APP1" ]; then
  echo "### App3 App4 => APP1 App2 ###"

  echo "1. App1 이미지 받기"
  docker-compose pull app1

  echo "2. App1 App2 컨테이너 실행"
  docker-compose up -d app1 app2

  echo "3. health check"
  if ! check_service "http://127.0.0.1:8081" || ! check_service "http://127.0.0.1:8082"; then
    echo "APP1 또는 APP2 health check 가 실패했습니다."
    exit 1
  fi

  echo "4. APP3 APP4 컨테이너 내리기"
  docker-compose stop app3 app4
  docker-compose rm -f app3 app4

else
  echo "### App1 App2 => App3 App4 ###"

  echo "1. App3 이미지 받기"
  docker-compose pull app3

  echo "2. App3 App4 컨테이너 실행"
  docker-compose up -d app3 app4

  echo "3. health check"
  if ! check_service "http://127.0.0.1:8083" || ! check_service "http://127.0.0.1:8084"; then
    echo "APP3 또는 APP4 health check 가 실패했습니다."
    exit 1
  fi

  echo "4. APP1 APP2 컨테이너 내리기"
  docker-compose stop app1 app2
  docker-compose rm -f app1 app2
fi