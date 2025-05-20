1. docker run --name postgres -e POSTGRES_PASSWORD=mysecretpassword -p 5432:5432 -d postgres

2. docker cp dump.sql postgres:/dump.sql

3. docker exec -i postgres psql -U postgres -f /dump.sql
