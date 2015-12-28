# Pre-built JDK 8 image
FROM kurron/docker-oracle-jdk-8:latest

MAINTAINER Ron Kurr <kurr@jvmguy.com>

LABEL org.kurron.built-on="${generated}" org.kurron.branch="${branch}"

COPY ${projectName}.jar /opt/example/${projectName}.jar

EXPOSE ${port}

WORKDIR /opt/example

ENTRYPOINT ["java", "-Dserver.port=${port}", "-jar", "${projectName}.jar"]
