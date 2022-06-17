FROM hub.kuangheng.com/jdk/mini-jdk8:1
#VOLUME /tmp
ADD cloud.gateway.jar app.jar
ENV LANG C.UTF-8
#RUN bash -c 'touch /app.jar'
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]