#!/usr/bin/env bash

script_name=$0
script_path=$(dirname "$0")

set -ex

docker-compose up -d

until docker-compose exec mysql mysqladmin ping -uroot -proot; do
    echo "Waiting for MySQL to become available..."
    sleep 1;
done

docker-compose exec mysql mysql -u root -proot -e "create user if not exists app;
grant all privileges on *.* to 'app'@'%' identified by 'app_password';
create database if not exists hexagon_auth;"

