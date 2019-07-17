-- Revert nlp_schema:clean_projects_fix from pg

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

end;
$BODY$;

COMMIT;
