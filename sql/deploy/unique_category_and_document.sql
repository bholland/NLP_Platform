-- Deploy nlp_schema:unique_category_and_document to pg

BEGIN;

ALTER TABLE public.category_text
    ADD UNIQUE (category_document_id, completed_processed_files_id);

ALTER TABLE public.document_text
    ADD UNIQUE (original_document_id, completed_processed_files_id);

CREATE OR REPLACE FUNCTION public.insert_category_text(
	in_user_id integer,
	in_category_document_id character varying,
	in_category_document_text text,
	in_category_document_path character varying)
    RETURNS TABLE(id integer) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE 
    ROWS 1000
AS $BODY$
declare new_id integer;
declare new_path_id integer;
begin

perform log_message(in_user_id, 'insert_category_text', null);

new_id := null;

insert into completed_processed_files
(file_path, is_complete)
values
(in_category_document_path, FALSE)
on conflict on constraint completed_processed_files_file_path_key do nothing;

select completed_processed_files.id into new_path_id 
from completed_processed_files
where file_path = in_category_document_path;

insert into category_text
(category_document_id, category_document_text, completed_processed_files_id) VALUES
(in_category_document_id, in_category_document_text, new_path_id)
on conflict on constraint category_text_category_document_id_completed_processed_file_key do nothing
returning category_text.id into new_id;

if new_id is null then
	select category_text.id into new_id
	from category_text
	where category_document_id = in_category_document_id and
	completed_processed_files_id = new_path_id;
end if;
	
return QUERY
select new_id;

end;
$BODY$;


CREATE OR REPLACE FUNCTION public.insert_document_text(
	in_user_id integer,
	in_original_document_id character varying,
	in_original_document_text text,
	in_original_document_path character varying)
    RETURNS TABLE(document_id integer) 
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE 
    ROWS 1000
AS $BODY$
declare new_id integer;
declare new_path_id integer;
begin
perform log_message(in_user_id, 'insert_document_text', null);

new_id := null;

insert into completed_processed_files
(file_path, is_complete)
values
(in_original_document_path, FALSE)
on conflict on constraint completed_processed_files_file_path_key do nothing;

select completed_processed_files.id into new_path_id 
from completed_processed_files
where file_path = in_original_document_path;

insert into document_text
(original_document_id, original_document_text, completed_processed_files_id) VALUES
(in_original_document_id, in_original_document_text, new_path_id)
on conflict on constraint document_text_original_document_id_completed_processed_file_key do nothing
returning document_text.id into new_id;

if new_id is null then
	select category_text.id into new_id
	from category_text
	where category_document_id = in_category_document_id and
	completed_processed_files_id = new_path_id;
end if;

return query
select new_id;

end;
$BODY$;



CREATE OR REPLACE FUNCTION public.insert_training_data(
	in_user_id integer,
	in_category_text_id integer,
	in_category_id integer,
	in_is_in_category boolean)
    RETURNS void
    LANGUAGE 'plpgsql'
    COST 100
    VOLATILE 
AS $BODY$
begin
perform log_message(in_user_id, 'insert_training_data', null);

insert into category_training_data 
(category_text_id, category_id, is_in_category) 
values 
(in_category_text_id, in_category_id, in_is_in_category)
on conflict on constraint category_training_data_text_id_category_id_key do nothing;

end;
$BODY$;


COMMIT;
