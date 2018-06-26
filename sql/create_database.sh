#dump nlp_template:
#pg_dump -s nlp_template > nlp_schema.sql

#create the database
createdb nlp_platform

#populate it with the nlp_schema.sql
psql -d nlp_platform -f nlp_schema.sql
