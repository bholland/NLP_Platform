pg_dump -s my_database > nlp_schema.sql
dropdb nlp_template
createdb nlp_template
psql -d nlp_template -f nlp_schema.sql
