#dump nlp_template:
pg_dump -s my_database > nlp_schema.sql

dropdb database

#create the database
createdb my_database

#populate it with the nlp_schema.sql
psql -d my_database -f nlp_schema.sql
