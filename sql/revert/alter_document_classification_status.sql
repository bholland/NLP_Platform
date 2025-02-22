-- Revert nlp_schema:alter_document_classification_status from pg

BEGIN;

DROP FUNCTION IF EXISTS public.insert_document_classification_status(integer, text, boolean);

CREATE OR REPLACE FUNCTION public.insert_document_classification_status(
	in_user_id integer,
	in_status text,
	in_is_ready_for_review bit,
	in_is_requires_admin bit)
    RETURNS TABLE(out_id integer) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE 
    ROWS 1000
AS $BODY$
declare new_id integer;
begin

perform log_message(in_user_id, 'insert_document_classification_status', null);

insert into document_classification_status
(status_text, is_ready_for_review, is_requires_admin)
values
(in_status, in_is_ready_for_review, in_is_requires_admin)
returning id into new_id;

return query
select new_id;

end;
$BODY$;

ALTER FUNCTION public.insert_document_classification_status(integer, text, bit, bit)
    OWNER TO ben;


DROP FUNCTION public.insert_document_classification_status_next(integer, integer, integer);

CREATE OR REPLACE FUNCTION public.insert_document_classification_status_next(
	in_user_id integer,
	in_document_classification_status_id integer,
	in_next_id integer)
    RETURNS void
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE 
AS $BODY$
declare new_id integer;
begin

perform log_message(in_user_id, 'insert_document_classification_status_next', null);

update document_classification_status
set next_id = in_next_id
where document_classification_status.id = in_document_classification_status_id;

end;
$BODY$;

ALTER FUNCTION public.insert_document_classification_status_next(integer, integer, integer)
    OWNER TO ben;

COMMIT;
