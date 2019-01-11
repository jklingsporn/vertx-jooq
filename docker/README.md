# postgres
- Build postgres image: `docker build -t vertx-jooq-pg -f DockerPostgres .`
- Run postgres image: `docker run -p 5432:5432 vertx-jooq-pg`

# mysql
- Run MySQL image: `docker run -p 127.0.0.1:3306:3306 -e MYSQL_ROOT_PASSWORD=vertx -e MYSQL_ROOT_HOST=% mysql:5.7 --max_connections=500`

