-- Deploy nlp_schema:remove_select_document_classification_status to pg

BEGIN;

DROP FUNCTION public.select_document_classification_status(integer);

CREATE OR REPLACE FUNCTION public.select_multiuser_document_classification_status_labels(
	in_user_id integer)
    RETURNS TABLE(out_id integer, out_status_text text, out_next_id integer, out_is_ready_for_review boolean, out_is_initialize boolean) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE 
    ROWS 1000
AS $BODY$
declare new_id integer;
begin

perform log_message(in_user_id, 'select_multiuser_document_classification_status_labels', null);

return query
select mdcsl.id, mdcsl.status_text, mdcsl.next_id, mdcsl.is_ready_for_review, mdcsl.is_initialize
from select_multiuser_document_classification_status_labels mdcsl;

end;
$BODY$;

ALTER FUNCTION public.select_multiuser_document_classification_status_labels(integer)
    OWNER TO ben;

COMMIT;
