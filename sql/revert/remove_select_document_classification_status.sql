-- Revert nlp_schema:remove_select_document_classification_status from pg

BEGIN;

DROP FUNCTION public.select_document_classification_status(integer)

CREATE OR REPLACE FUNCTION public.select_document_classification_status(
	in_user_id integer)
    RETURNS TABLE(out_id integer, out_status_text text, out_next_id integer, out_is_ready_for_review bit, out_is_requires_admin bit) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE 
    ROWS 1000
AS $BODY$
declare new_id integer;
begin

perform log_message(in_user_id, 'select_document_classification_status', null);

return query
select dcs.id, dcs.status_text, dcs.next_id, dcs.is_ready_for_review, dcs.is_requires_admin
from document_classification_status dcs;

end;
$BODY$;

ALTER FUNCTION public.select_document_classification_status(integer)
    OWNER TO ben;

COMMIT;
