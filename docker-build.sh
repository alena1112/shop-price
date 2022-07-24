#!/bin/sh

docker_image=alena1112/shop-price-service

build_image() {
  echo build $1
  docker build -t $1 .
}

push_image() {
  echo push $1
  docker push $1
}

# $1 - command
# $2 - version

case "$1" in
    build)
        build_image $docker_image:$2
      ;;
    push)
        push_image $docker_image:$2
      ;;
    *)
      echo "An error occurred. Build or push command expected"
      exit 1
      ;;
esac