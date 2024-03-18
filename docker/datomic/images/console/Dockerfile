FROM clojure:temurin-21-lein-alpine

ENV DATOMIC_HOME /opt/datomic-pro-1.0.7021

RUN apk add --no-cache unzip curl

RUN curl https://datomic-pro-downloads.s3.amazonaws.com/1.0.7021/datomic-pro-1.0.7021.zip -o /tmp/datomic.zip \
    && unzip /tmp/datomic.zip -d /opt \
    && rm -f /tmp/datomic.zip

WORKDIR $DATOMIC_HOME

ENTRYPOINT ["bin/console", "-p", "9000"]

EXPOSE 9000