Il faut : `docker`, `docker-buildx`, `qemu-user-static`, `qemu-user-binfmt`

`sudo docker buildx create --name mybuilder`
`sudo docker buildx use mybuilder`
`sudo docker buildx inspect --bootstrap`
`sudo docker run --rm --privileged multiarch/qemu-user-static --reset -p yes`
Pour check : `docker buildx ls`
`sudo docker buildx build -t arm --output type=docker --platform linux/arm/v7 .`

`sudo docker run --platform linux/arm/v7 --rm -it arm /bin/sh`
OU 
`sudo docker run --platform linux/arm/v7 --rm -it arm /prgm`