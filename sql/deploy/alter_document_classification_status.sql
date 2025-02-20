-- Deploy nlp_schema:alter_document_classification_status to pg

BEGIN;

DROP FUNCTION IF EXISTS public.insert_document_classification_status(integer, text, bit, bit);

CREATE OR REPLACE FUNCTION public.insert_multiuser_document_classification_status_labels(
	in_user_id integer,
	in_status text,
	in_is_ready_for_review boolean, 
	in_is_initialize boolean)
    RETURNS TABLE(out_id integer) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE 
    ROWS 1000
AS $BODY$
declare new_id integer;
begin

perform log_message(in_user_id, 'insert_multiuser_document_classification_status_labels', null);

insert into multiuser_document_classification_status_labels
(status_text, is_ready_for_review, is_initialize)
values
(in_status, in_is_ready_for_review, in_is_initialize)
returning id into new_id;

return query
select new_id;

end;
$BODY$;

ALTER FUNCTION public.insert_multiuser_document_classification_status_labels(integer, text, boolean, boolean)
    OWNER TO ben;


DROP FUNCTION public.insert_document_classification_status_next(integer, integer, integer);

CREATE OR REPLACE FUNCTION public.insert_multiuser_document_classification_status_next(
	in_user_id integer,
	in_multiuser_document_classification_status_labels_id integer,
	in_next_id integer)
    RETURNS void
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE 
AS $BODY$
declare new_id integer;
begin

perform log_message(in_user_id, 'insert_document_classification_status_next', null);

update multiuser_document_classification_status_labels
set next_id = in_next_id
where multiuser_document_classification_status_labels.id = in_multiuser_document_classification_status_labels_id;

end;
$BODY$;

ALTER FUNCTION public.insert_multiuser_document_classification_status_next(integer, integer, integer)
    OWNER TO ben;

COMMIT;
