FROM clojure:temurin-21-lein-alpine as BUILD

RUN apk add --no-cache unzip curl

RUN curl https://datomic-pro-downloads.s3.amazonaws.com/1.0.7021/datomic-pro-1.0.7021.zip -o /tmp/datomic.zip \
    && unzip /tmp/datomic.zip -d /opt \
    && rm -f /tmp/datomic.zip

FROM clojure:temurin-21-lein-alpine as TRANSACTOR

ARG STORAGE_ADMIN_PASSWORD
ARG STORAGE_DATOMIC_PASSWORD

ENV DATOMIC_HOME    /opt/datomic-pro-1.0.7021
ENV DATOMIC_DATA    /var/datomic/data
ENV DATOMIC_LOG     /var/datomic/log

VOLUME $DATOMIC_DATA
VOLUME $DATOMIC_LOG

WORKDIR $DATOMIC_HOME

COPY --from=BUILD /opt/datomic-pro-1.0.7021 /opt/datomic-pro-1.0.7021

ADD ./datomic/images/transactor/config/transactor.properties config/dev-transactor-template.properties

RUN echo -e "\n storage-admin-password=$STORAGE_ADMIN_PASSWORD" >> config/dev-transactor-template.properties
RUN echo -e "\n storage-datomic-password=$STORAGE_DATOMIC_PASSWORD" >> config/dev-transactor-template.properties

ENTRYPOINT ["bin/transactor", "-Ddatomic.printConnectionInfo=true", "config/dev-transactor-template.properties"]

EXPOSE 4334 4335 4336 9999

FROM clojure:temurin-21-lein-alpine as CONSOLE

ENV DATOMIC_HOME /opt/datomic-pro-1.0.7021

COPY --from=BUILD /opt/datomic-pro-1.0.7021 /opt/datomic-pro-1.0.7021

WORKDIR $DATOMIC_HOME

ENTRYPOINT ["bin/console", "-p", "9000"]

EXPOSE 9000