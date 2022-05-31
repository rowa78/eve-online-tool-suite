FROM alpine:latest AS builder
ADD https://www.fuzzwork.co.uk/dump/mysql-latest.tar.bz2 /tmp/
# run the postgres process in the container?
# i saw this as the CMD for the postgres image
# also tried running /usr/local/bin/docker-entrypoint.sh

RUN ls -l /tmp && \    
    tar xfvj /tmp/mysql-latest.tar.bz2 && \
    cp sde*/*.sql /tmp/sde.sql


# final build stage
FROM mysql:8.0.29-oracle
COPY --from=builder /tmp/sde.sql /docker-entrypoint-initdb.d
ENV MYSQL_ROOT_PASSWORD=eve
ENV MYSQL_DATABASE=eve-sde
ENV MYSQL_USER=eve
ENV MYSQL_PASSWORD=eve