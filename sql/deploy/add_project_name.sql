-- Deploy nlp_schema:add_project_name to pg

BEGIN;

ALTER TABLE public.projects
    ADD COLUMN "name" character varying(100);


DROP FUNCTION IF EXISTS public.insert_project(integer, integer, integer, integer);

CREATE OR REPLACE FUNCTION public.insert_project(
	in_name character varying,
	in_user_id integer,
	in_owner_user_id integer,
	in_ready_for_review_id integer,
	in_checkout_timeout integer DEFAULT 600)
    RETURNS TABLE(out_id integer) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE 
    ROWS 1000
AS $BODY$
declare new_id integer;
begin

perform log_message(in_user_id, 'insert_project', null);

--only have a single project setting.
truncate table projects;

insert into projects
("name", owner_user_id, ready_for_review_id, checkout_timeout)
values
(in_name, in_owner_user_id, in_ready_for_review_id, in_checkout_timeout)
returning id into new_id;

return query
select new_id;

end;
$BODY$;

ALTER FUNCTION public.insert_project(character varying, integer, integer, integer, integer)
    OWNER TO ben;

COMMIT;
