FROM openjdk:11
ENV PORT 8080
COPY build/libs/news.searcher-0.0.1-SNAPSHOT.jar /opt/app.jar
WORKDIR /opt
ENTRYPOINT exec java $JAVA_OPTS -jar app.jar