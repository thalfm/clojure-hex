FROM clojure:temurin-21-lein-alpine

ARG STORAGE_ADMIN_PASSWORD
ARG STORAGE_DATOMIC_PASSWORD

ENV DATOMIC_HOME    /opt/datomic-pro-1.0.7021
ENV DATOMIC_DATA    /var/datomic/data
ENV DATOMIC_LOG     /var/datomic/log

VOLUME $DATOMIC_DATA
VOLUME $DATOMIC_LOG

RUN apk add --no-cache unzip curl

RUN curl https://datomic-pro-downloads.s3.amazonaws.com/1.0.7021/datomic-pro-1.0.7021.zip -o /tmp/datomic.zip \
    && unzip /tmp/datomic.zip -d /opt \
    && rm -f /tmp/datomic.zip

WORKDIR $DATOMIC_HOME

ADD ./datomic/images/transactor/config/transactor.properties config/dev-transactor-template.properties

RUN echo -e "\n storage-admin-password=$STORAGE_ADMIN_PASSWORD" >> config/dev-transactor-template.properties
RUN echo -e "\n storage-datomic-password=$STORAGE_DATOMIC_PASSWORD" >> config/dev-transactor-template.properties

ENTRYPOINT ["bin/transactor", "-Ddatomic.printConnectionInfo=true", "config/dev-transactor-template.properties"]

EXPOSE 4334 4335 4336 9999