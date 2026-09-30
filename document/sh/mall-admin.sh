#!/usr/bin/env bash
app_name='mall-admin'
network_name='mall-net'
docker stop ${app_name}
echo '----stop container----'
docker rm ${app_name}
echo '----rm container----'
docker rmi `docker images | grep none | awk '{print $3}'`
echo '----rm none images----'
docker run -p 8092:8092 --name ${app_name} \
--add-host=host.docker.internal:host-gateway \
--network ${network_name} \
--link redis:redis \
-e TZ="Asia/Shanghai" \
-v /etc/localtime:/etc/localtime \
-v /mydata/app/${app_name}/logs:/var/logs \
-d mall/${app_name}:1.0-SNAPSHOT
echo '----start container----'
