FROM europe-north1-docker.pkg.dev/cgr-nav/pull-through/nav.no/jre:openjdk-25
LABEL maintainer="Team Arbeidsforhold"

ENV LANG='nb_NO.UTF-8' LANGUAGE='nb_NO:nb' LC_ALL='nb:NO.UTF-8' TZ="Europe/Oslo"
ENV JAVA_OPTS="-XX:MinRAMPercentage=25.0 -XX:MaxRAMPercentage=75.0 -XX:+HeapDumpOnOutOfMemoryError"

ARG JAR_PATH

COPY $JAR_PATH /app/app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]