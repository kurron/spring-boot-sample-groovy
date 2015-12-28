# Pre-built JDK 8 image
FROM kurron/docker-oracle-jdk-8:latest

MAINTAINER Ron Kurr <rkurr@transparent.com>

LABEL com.transparent.built-on="${generated}" com.transparent.branch="${branch}"

COPY ${projectName}.jar /opt/example/${projectName}.jar

EXPOSE ${port}

WORKDIR /opt/example

ENTRYPOINT ["java", "-Dserver.port=${port}", "-jar", "${projectName}.jar"]
