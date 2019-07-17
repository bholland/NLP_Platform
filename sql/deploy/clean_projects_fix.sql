-- Deploy nlp_schema:clean_projects_fix to pg

BEGIN;

CREATE OR REPLACE FUNCTION public.clean_projects(
	in_user_id integer)
    RETURNS void
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE 
AS $BODY$
declare new_id integer;
begin

perform log_message(in_user_id, 'clean_projects', null);

truncate table projects;
truncate table multiuser_document_classification_status_labels cascade;

end;
$BODY$;

COMMIT;
