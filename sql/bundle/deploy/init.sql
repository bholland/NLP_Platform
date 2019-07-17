-- Deploy nlp_schema:init to pg

BEGIN;

--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.14
-- Dumped by pg_dump version 9.5.14

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: nlp_template; Type: SCHEMA; Schema: -; Owner: ben
--

CREATE SCHEMA nlp_template;


ALTER SCHEMA nlp_template OWNER TO ben;

--
-- Name: SCHEMA nlp_template; Type: COMMENT; Schema: -; Owner: ben
--

COMMENT ON SCHEMA nlp_template IS 'This is the NLP database schema.';


--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


--
-- Name: CleanSortedIndex(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public."CleanSortedIndex"(in_user_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
perform log_message(in_user_id, 'CleanSortedIndex', null);

truncate sorted_index RESTART IDENTITY;
end;
$$;


ALTER FUNCTION public."CleanSortedIndex"(in_user_id integer) OWNER TO ben;

--
-- Name: clean_all_text(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.clean_all_text(in_user_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$

begin
perform log_message(in_user_id, 'clean_all_text', null);
truncate document_text cascade;
truncate category_text cascade;
end;

$$;


ALTER FUNCTION public.clean_all_text(in_user_id integer) OWNER TO ben;

--
-- Name: clean_category_sentences(integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.clean_category_sentences(in_user_id integer, in_category_text_id integer DEFAULT NULL::integer) RETURNS void
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$
begin

perform log_message(in_user_id, 'clean_category_sentences', null);

delete from category_sentences
where category_text_id = in_category_text_id;

end;
$$;


ALTER FUNCTION public.clean_category_sentences(in_user_id integer, in_category_text_id integer) OWNER TO ben;

--
-- Name: clean_document_classification_status(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.clean_document_classification_status(in_user_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin

perform log_message(in_user_id, 'clean_document_classification_status', null);

truncate table document_classification_status;

end;
$$;


ALTER FUNCTION public.clean_document_classification_status(in_user_id integer) OWNER TO ben;

--
-- Name: clean_document_sentences(integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.clean_document_sentences(in_user_id integer, in_document_text_id integer DEFAULT NULL::integer) RETURNS void
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$
begin
perform log_message(in_user_id, 'clean_document_sentences', null);

delete from document_sentences
where document_text_id = in_document_text_id;
end;
$$;


ALTER FUNCTION public.clean_document_sentences(in_user_id integer, in_document_text_id integer) OWNER TO ben;

--
-- Name: clean_projects(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.clean_projects(in_user_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin

perform log_message(in_user_id, 'clean_projects', null);

truncate table projects;

end;
$$;


ALTER FUNCTION public.clean_projects(in_user_id integer) OWNER TO ben;

--
-- Name: clean_sorted_index(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.clean_sorted_index(in_user_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
perform log_message(in_user_id, 'clean_sorted_index', null);
truncate sorted_index RESTART IDENTITY;
end;
$$;


ALTER FUNCTION public.clean_sorted_index(in_user_id integer) OWNER TO ben;

--
-- Name: clean_spelling_corrections(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.clean_spelling_corrections(in_user_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
perform log_message(in_user_id, 'clean_spelling_corrections', null);

truncate spelling_corrections restart identity cascade;
end;
$$;


ALTER FUNCTION public.clean_spelling_corrections(in_user_id integer) OWNER TO ben;

--
-- Name: copy_document_to_category_text(integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.copy_document_to_category_text(in_user_id integer, in_document_text_id integer) RETURNS TABLE(id integer)
    LANGUAGE plpgsql
    AS $$

DECLARE
    new_category_id integer;
	category_count bigint;
begin
perform log_message(in_user_id, 'copy_document_to_category_text', null);

select into category_count count(*)
from document_text dt
where dt.id = in_document_text_id
and dt.category_text_id is not null;

if category_count > 0 then

return QUERY
select category_text_id
from document_text dt
where dt.id = in_document_text_id;

else

insert into category_text
(category_document_id, category_document_text, completed_processed_files_id, processed_text, processed, num_tokens)
select original_document_id, original_document_text, completed_processed_files_id, processed_text, processed, num_tokens
from document_text dt
where dt.id = in_document_text_id
returning category_text.id into new_category_id;

update document_text
set category_text_id = new_category_id
where document_text.id = in_document_text_id;

return QUERY
select new_category_id;

end if;

end;

$$;


ALTER FUNCTION public.copy_document_to_category_text(in_user_id integer, in_document_text_id integer) OWNER TO ben;

--
-- Name: delete_application_user(integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.delete_application_user(in_user_id integer, in_role_user_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin

perform log_message(in_user_id, 'delete_application_user', null);

delete from users
where users.id = in_role_user_id;

end;
$$;


ALTER FUNCTION public.delete_application_user(in_user_id integer, in_role_user_id integer) OWNER TO ben;

--
-- Name: delete_category_texts_at_path(integer, character varying); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.delete_category_texts_at_path(in_user_id integer, in_file_path character varying) RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
perform log_message(in_user_id, 'delete_category_texts_at_path', null);

delete from category_text 
where completed_processed_files_id in (
	select cpf.id
	from completed_processed_files cpf
	join category_text ct on ct.completed_processed_files_id = cpf.id
	where file_path = in_file_path
);


end;
$$;


ALTER FUNCTION public.delete_category_texts_at_path(in_user_id integer, in_file_path character varying) OWNER TO ben;

--
-- Name: delete_document_texts_at_path(integer, character varying); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.delete_document_texts_at_path(in_user_id integer, in_file_path character varying) RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
perform log_message(in_user_id, 'delete_document_texts_at_path', null);

delete from document_text 
where completed_processed_files_id in (
	select cpf.id
	from completed_processed_files cpf
	join document_text dt on dt.completed_processed_files_id = cpf.id
	where file_path = in_file_path
);


end;
$$;


ALTER FUNCTION public.delete_document_texts_at_path(in_user_id integer, in_file_path character varying) OWNER TO ben;

--
-- Name: delete_job_from_queue(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.delete_job_from_queue(in_user_id integer, in_job_queue_id integer, in_job_status_finished_id integer) RETURNS TABLE(job_status bigint)
    LANGUAGE plpgsql
    AS $$
declare is_fininshed integer;
declare js_next_id integer;
begin
perform log_message(in_user_id, 'delete_job_from_queue', null);

select count(*) into is_fininshed
from job_queue jq
where jq.id = in_job_queue_id
and jq.job_status_id = in_job_status_finished_id;

if is_fininshed = 1 then
	delete from job_queue
	where job_queue.id = in_job_queue_id;
end if;

return query
select count(*)
from job_queue
where job_queue.id = in_job_queue_id;

end;
$$;


ALTER FUNCTION public.delete_job_from_queue(in_user_id integer, in_job_queue_id integer, in_job_status_finished_id integer) OWNER TO ben;

--
-- Name: delete_job_params_for_job_id(integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.delete_job_params_for_job_id(in_user_id integer, in_job_queue_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin
perform log_message(in_user_id, 'delete_job_params_for_job_id', null);

delete from job_additional_parameters
where job_queue_id = in_job_queue_id;

end;
$$;


ALTER FUNCTION public.delete_job_params_for_job_id(in_user_id integer, in_job_queue_id integer) OWNER TO ben;

--
-- Name: delete_job_status(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.delete_job_status(in_user_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin
perform log_message(in_user_id, 'delete_job_status', null);

delete from job_status where job_status.id > 0;

end;
$$;


ALTER FUNCTION public.delete_job_status(in_user_id integer) OWNER TO ben;

--
-- Name: delete_job_types(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.delete_job_types(in_user_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin
perform log_message(in_user_id, 'delete_job_types', null);

delete from job_types where job_types.id > 0;

end;
$$;


ALTER FUNCTION public.delete_job_types(in_user_id integer) OWNER TO ben;

--
-- Name: delete_user_roles(integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.delete_user_roles(in_user_id integer, in_role_user_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
begin

perform log_message(in_user_id, 'delete_user_roles', null);

delete from user_role_assignments
where user_role_assignments.user_id = in_role_user_id;

end;
$$;


ALTER FUNCTION public.delete_user_roles(in_user_id integer, in_role_user_id integer) OWNER TO ben;

--
-- Name: increment_batch_number(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.increment_batch_number(in_user_id integer) RETURNS TABLE(batch_seq bigint)
    LANGUAGE plpgsql
    AS $$
begin
perform log_message(in_user_id, 'increment_batch_number', null);

return query
select nextval('text_category_batch_number_seq');
end;
$$;


ALTER FUNCTION public.increment_batch_number(in_user_id integer) OWNER TO ben;

--
-- Name: increment_next_job_status(integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.increment_next_job_status(in_user_id integer, in_job_queue_id integer) RETURNS TABLE(job_status_id integer)
    LANGUAGE plpgsql
    AS $$
declare js_id integer;
declare js_next_id integer;
begin
perform log_message(in_user_id, 'increment_next_job_status', null);

select js.next_status_id into js_next_id
from job_queue jq
join job_status js on js.id = jq.job_status_id
where jq.id = in_job_queue_id;

if js_next_id is not null then
	update job_queue
	set job_status_id = js_next_id
	where job_queue.id = in_job_queue_id;
end if;

return query
select jq.job_status_id
from job_queue jq
where jq.id = in_job_queue_id;

end;
$$;


ALTER FUNCTION public.increment_next_job_status(in_user_id integer, in_job_queue_id integer) OWNER TO ben;

--
-- Name: insert_category(integer, character varying); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_category(in_user_id integer, in_category character varying) RETURNS TABLE(category_id integer)
    LANGUAGE plpgsql
    AS $$
declare
	returning_id integer;
begin
perform log_message(in_user_id, 'insert_category', null);


insert into categories 
(category) 
values 
(in_category)
returning "id" into returning_id;

return query
select returning_id;

end;
$$;


ALTER FUNCTION public.insert_category(in_user_id integer, in_category character varying) OWNER TO ben;

--
-- Name: insert_category_model_file(integer, integer, bytea); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_category_model_file(in_user_id integer, in_category_id integer, in_category_model_file bytea) RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
perform log_message(in_user_id, 'insert_category_model_file', null);

update categories
set category_model_file = in_category_model_file
where id = in_category_id;
end;
$$;


ALTER FUNCTION public.insert_category_model_file(in_user_id integer, in_category_id integer, in_category_model_file bytea) OWNER TO ben;

--
-- Name: insert_category_model_file(integer, integer, oid); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_category_model_file(in_user_id integer, in_category_id integer, in_category_model_file_oid oid) RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
perform log_message(in_user_id, 'insert_category_model_file', null);

--save the old category id inside of a history. 
insert into category_model_history
(category_id, category_model_file_oid)
select category_id, category_model_file_oid
from categories
where category_id = in_category_id and category_model_file_oid is not null;

update categories
set category_model_file_oid = in_category_model_file_oid
where id = in_category_id;
end;
$$;


ALTER FUNCTION public.insert_category_model_file(in_user_id integer, in_category_id integer, in_category_model_file_oid oid) OWNER TO ben;

--
-- Name: insert_category_sentence(integer, text, integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_category_sentence(in_user_id integer, in_sentence_text text, in_category_text_id integer, in_sentence_number integer) RETURNS TABLE(category_sentence_id integer)
    LANGUAGE plpgsql
    AS $$
declare new_category_sentence_id integer;
begin

perform log_message(in_user_id, 'insert_category_sentence', null);

insert into category_sentences (category_text_id, sentence, sentence_number)
values
(in_category_text_id, in_sentence_text, in_sentence_number)
returning id into new_category_sentence_id;

return query
select new_category_sentence_id;

end $$;


ALTER FUNCTION public.insert_category_sentence(in_user_id integer, in_sentence_text text, in_category_text_id integer, in_sentence_number integer) OWNER TO ben;

--
-- Name: insert_category_sentence_metadata(integer, integer, integer, character varying, character varying, character varying, character varying, boolean); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_category_sentence_metadata(in_user_id integer, in_sentence_id integer, in_sentence_index_id integer, in_token character varying, in_pos_tag character varying, in_chunk character varying, in_stemmed_token character varying, in_is_raw_text boolean) RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
perform log_message(in_user_id, 'insert_category_sentence_metadata', null);

insert into category_sentences_metadata
(category_sentence_id, sentence_index, "token", pos_tag, chunk, stemmed_token, is_raw_text)
values
(in_sentence_id, in_sentence_index_id, in_token, in_pos_tag, in_chunk, in_stemmed_token, in_is_raw_text);

end $$;


ALTER FUNCTION public.insert_category_sentence_metadata(in_user_id integer, in_sentence_id integer, in_sentence_index_id integer, in_token character varying, in_pos_tag character varying, in_chunk character varying, in_stemmed_token character varying, in_is_raw_text boolean) OWNER TO ben;

--
-- Name: insert_category_text(integer, character varying, text, character varying); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_category_text(in_user_id integer, in_category_document_id character varying, in_category_document_text text, in_category_document_path character varying) RETURNS TABLE(id integer)
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
declare new_path_id integer;
begin

perform log_message(in_user_id, 'insert_category_text', null);

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
returning category_text.id into new_id;

return QUERY
select new_id;

end;
$$;


ALTER FUNCTION public.insert_category_text(in_user_id integer, in_category_document_id character varying, in_category_document_text text, in_category_document_path character varying) OWNER TO ben;

--
-- Name: insert_category_token_count(integer, integer, text, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_category_token_count(in_user_id integer, in_category_text_id integer, in_processed_text text, in_num_tokens integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
perform log_message(in_user_id, 'insert_category_token_count', null);

update category_text
set processed_text=in_processed_text,
processed=TRUE,
num_tokens=in_num_tokens
where category_text.id = in_category_text_id;
end;
$$;


ALTER FUNCTION public.insert_category_token_count(in_user_id integer, in_category_text_id integer, in_processed_text text, in_num_tokens integer) OWNER TO ben;

--
-- Name: insert_document_classification_status(integer, text, bit, bit); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_document_classification_status(in_user_id integer, in_status text, in_is_ready_for_review bit, in_is_requires_admin bit) RETURNS TABLE(out_id integer)
    LANGUAGE plpgsql
    AS $$
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
$$;


ALTER FUNCTION public.insert_document_classification_status(in_user_id integer, in_status text, in_is_ready_for_review bit, in_is_requires_admin bit) OWNER TO ben;

--
-- Name: insert_document_classification_status_next(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_document_classification_status_next(in_user_id integer, in_document_classification_status_id integer, in_next_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin

perform log_message(in_user_id, 'insert_document_classification_status_next', null);

update document_classification_status
set next_id = in_next_id
where document_classification_status.id = in_document_classification_status_id;

end;
$$;


ALTER FUNCTION public.insert_document_classification_status_next(in_user_id integer, in_document_classification_status_id integer, in_next_id integer) OWNER TO ben;

--
-- Name: insert_document_sentence(integer, text, integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_document_sentence(in_user_id integer, in_sentence_text text, in_document_text_id integer, in_sentence_number integer) RETURNS TABLE(document_sentence_id integer)
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin
perform log_message(in_user_id, 'insert_document_sentence', null);

insert into document_sentences (document_text_id, sentence, sentence_number)
values
(in_document_text_id, in_sentence_text, in_sentence_number)
returning id into new_id;

return query
select new_id;
end;
$$;


ALTER FUNCTION public.insert_document_sentence(in_user_id integer, in_sentence_text text, in_document_text_id integer, in_sentence_number integer) OWNER TO ben;

--
-- Name: insert_document_sentence_metadata(integer, integer, integer, character varying, character varying, character varying, character varying, boolean); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_document_sentence_metadata(in_user_id integer, in_document_sentence_id integer, in_sentence_index_id integer, in_token character varying, in_pos_tag character varying, in_chunk character varying, in_stemmed_token character varying, in_is_raw_text boolean) RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
perform log_message(in_user_id, 'insert_document_sentence_metadata', null);

insert into document_sentences_metadata
(document_sentence_id, sentence_index, "token", pos_tag, chunk, stemmed_token, is_raw_text)
values
(in_document_sentence_id, in_sentence_index_id, in_token, in_pos_tag, in_chunk, in_stemmed_token, in_is_raw_text);
end;
$$;


ALTER FUNCTION public.insert_document_sentence_metadata(in_user_id integer, in_document_sentence_id integer, in_sentence_index_id integer, in_token character varying, in_pos_tag character varying, in_chunk character varying, in_stemmed_token character varying, in_is_raw_text boolean) OWNER TO ben;

--
-- Name: insert_document_text(integer, character varying, text, character varying); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_document_text(in_user_id integer, in_original_document_id character varying, in_original_document_text text, in_original_document_path character varying) RETURNS TABLE(document_id integer)
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
declare new_path_id integer;
begin
perform log_message(in_user_id, 'insert_document_text', null);

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
returning document_text.id into new_id;

return query
select new_id;

end;
$$;


ALTER FUNCTION public.insert_document_text(in_user_id integer, in_original_document_id character varying, in_original_document_text text, in_original_document_path character varying) OWNER TO ben;

--
-- Name: insert_document_token_count(integer, integer, text, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_document_token_count(in_user_id integer, in_document_text_id integer, in_processed_text text, in_num_tokens integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
perform log_message(in_user_id, 'insert_document_token_count', null);

update document_text
set processed_text=in_processed_text,
processed=TRUE,
num_tokens=in_num_tokens
where document_text.id = in_document_text_id;
end;
$$;


ALTER FUNCTION public.insert_document_token_count(in_user_id integer, in_document_text_id integer, in_processed_text text, in_num_tokens integer) OWNER TO ben;

--
-- Name: insert_job_param(integer, integer, character varying, character varying, oid); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_job_param(in_user_id integer, in_job_queue_id integer, in_param_name character varying, in_param_value character varying DEFAULT NULL::character varying, in_param_object_oid oid DEFAULT NULL::oid) RETURNS TABLE(job_additional_param_id integer)
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin
perform log_message(in_user_id, 'insert_job_param', null);

insert into job_additional_parameters
(job_queue_id, param_name, param_value, param_object_oid)
values
(in_job_queue_id, in_param_name, in_param_value, in_param_object_oid)
returning job_additional_parameters.id into new_id;

return query
select new_id;

end;
$$;


ALTER FUNCTION public.insert_job_param(in_user_id integer, in_job_queue_id integer, in_param_name character varying, in_param_value character varying, in_param_object_oid oid) OWNER TO ben;

--
-- Name: insert_job_status(integer, character varying); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_job_status(in_user_id integer, in_job_status_label character varying) RETURNS TABLE(job_status_id integer)
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin
perform log_message(in_user_id, 'insert_job_status_next', null);

insert into job_status
(job_status_label)
values
(in_job_status_label)
returning job_status.id into new_id;

return query
select new_id;

end;
$$;


ALTER FUNCTION public.insert_job_status(in_user_id integer, in_job_status_label character varying) OWNER TO ben;

--
-- Name: insert_job_status_next(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_job_status_next(in_user_id integer, in_job_status_id integer, in_next_status_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin
perform log_message(in_user_id, 'insert_job_status_next', null);

update job_status
set next_status_id = in_next_status_id
where job_status.id = in_job_status_id;

end;
$$;


ALTER FUNCTION public.insert_job_status_next(in_user_id integer, in_job_status_id integer, in_next_status_id integer) OWNER TO ben;

--
-- Name: insert_job_type(integer, character varying); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_job_type(in_user_id integer, in_job_type_label character varying) RETURNS TABLE(job_type_id integer)
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin
perform log_message(in_user_id, 'insert_job_type', null);

insert into job_types
(job_label)
values
(in_job_type_label)
returning job_types.id into new_id;

return query
select new_id;

end;
$$;


ALTER FUNCTION public.insert_job_type(in_user_id integer, in_job_type_label character varying) OWNER TO ben;

--
-- Name: insert_new_job(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_new_job(in_user_id integer, in_job_type_id integer, in_job_status_not_started_id integer) RETURNS TABLE(job_type_id integer)
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin
perform log_message(in_user_id, 'insert_job_type', null);

insert into job_queue
(user_id, job_type_id, job_status_id)
values
(in_user_id, in_job_type_id, in_job_status_not_started_id)
returning job_queue.id into new_id;

return query
select new_id;

end;
$$;


ALTER FUNCTION public.insert_new_job(in_user_id integer, in_job_type_id integer, in_job_status_not_started_id integer) OWNER TO ben;

--
-- Name: insert_new_role(integer, character varying, text, text); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_new_role(in_user_id integer, in_name character varying, in_display_name text DEFAULT NULL::text, in_description text DEFAULT NULL::text) RETURNS TABLE(id integer)
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin

perform log_message(in_user_id, 'insert_new_role', null);

insert into roles
("name", display_name, description) VALUES
(in_name, in_display_name, in_description)
returning roles.id into new_id;

return QUERY
select new_id;

end;
$$;


ALTER FUNCTION public.insert_new_role(in_user_id integer, in_name character varying, in_display_name text, in_description text) OWNER TO ben;

--
-- Name: insert_new_user(text, text, text, text); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_new_user(in_user_name text, in_email text, in_first_name text DEFAULT NULL::text, in_last_name text DEFAULT NULL::text) RETURNS TABLE(id integer)
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin

insert into users
(username, email, first_name, last_name) 
VALUES
(in_user_name, in_email, in_first_name, in_last_name)
returning users.id into new_id;

return QUERY
select new_id;

end;
$$;


ALTER FUNCTION public.insert_new_user(in_user_name text, in_email text, in_first_name text, in_last_name text) OWNER TO ben;

--
-- Name: insert_project(integer, integer, integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_project(in_user_id integer, in_owner_user_id integer, in_ready_for_review_id integer, in_checkout_timeout integer DEFAULT 600) RETURNS TABLE(out_id integer)
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin

perform log_message(in_user_id, 'insert_project', null);

--only have a single project setting.
truncate table projects;

insert into projects
(owner_user_id, ready_for_review_id, checkout_timeout)
values
(in_owner_user_id, in_ready_for_review_id, in_checkout_timeout)
returning id into new_id;

return query
select new_id;

end;
$$;


ALTER FUNCTION public.insert_project(in_user_id integer, in_owner_user_id integer, in_ready_for_review_id integer, in_checkout_timeout integer) OWNER TO ben;

--
-- Name: insert_sorted_index(integer, integer, integer[], integer[], double precision[]); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_sorted_index(in_user_id integer, in_document_text_id integer, category_ids integer[], ranks integer[], cos_thetas double precision[]) RETURNS void
    LANGUAGE plpgsql
    AS $$

BEGIN
perform log_message(in_user_id, 'insert_sorted_index', null);
	
Insert into sorted_index (document_text_id, category_text_id, "index", cos_theta) (
	select in_document_text_id, r.*
	from unnest(category_ids, ranks, cos_thetas) as r
	);
END;

$$;


ALTER FUNCTION public.insert_sorted_index(in_user_id integer, in_document_text_id integer, category_ids integer[], ranks integer[], cos_thetas double precision[]) OWNER TO ben;

--
-- Name: FUNCTION insert_sorted_index(in_user_id integer, in_document_text_id integer, category_ids integer[], ranks integer[], cos_thetas double precision[]); Type: COMMENT; Schema: public; Owner: ben
--

COMMENT ON FUNCTION public.insert_sorted_index(in_user_id integer, in_document_text_id integer, category_ids integer[], ranks integer[], cos_thetas double precision[]) IS 'This will insert the rankings of each category. This should probably be limited on the client side to this many to many table doesn''t blow up.  ';


--
-- Name: insert_source_sentence_metadata(integer, integer, integer, character varying, character varying, character varying, character varying, boolean); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_source_sentence_metadata(in_user_id integer, in_sentence_id integer, in_sentence_index_id integer, in_token character varying, in_pos_tag character varying, in_chunk character varying, in_stemmed_token character varying, in_is_raw_text boolean) RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
perform log_message(in_user_id, 'insert_source_sentence_metadata', null);

insert into document_sentences_metadata
(document_sentence_id, sentence_index, "token", pos_tag, chunk, stemmed_token, is_raw_text)
values
(in_sentence_id, in_sentence_index_id, in_token, in_pos_tag, in_chunk, in_stemmed_token, in_is_raw_text);
end;
$$;


ALTER FUNCTION public.insert_source_sentence_metadata(in_user_id integer, in_sentence_id integer, in_sentence_index_id integer, in_token character varying, in_pos_tag character varying, in_chunk character varying, in_stemmed_token character varying, in_is_raw_text boolean) OWNER TO ben;

--
-- Name: insert_spelling_correction(integer, text, text); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_spelling_correction(in_user_id integer, in_orginal_word text, in_recomended_correction text) RETURNS void
    LANGUAGE plpgsql
    AS $$

declare word_exists integer;
begin
PERFORM log_message(in_user_id, 'insert_spelling_correction', null);

word_exists := 0;
select count(sc.id) into word_exists 
from spelling_corrections sc
where sc.orginal_word = in_orginal_word
limit 1;

if word_exists < 1 then
insert into spelling_corrections (orginal_word, recomended_correction, num) values
(in_orginal_word, in_recomended_correction, 1);
else
update spelling_corrections set num = num + 1
where orginal_word = in_orginal_word;
end if;

exception when unique_violation then
update spelling_corrections set num = num + 1
where orginal_word = in_orginal_word;
end;

$$;


ALTER FUNCTION public.insert_spelling_correction(in_user_id integer, in_orginal_word text, in_recomended_correction text) OWNER TO ben;

--
-- Name: insert_text_category_probability(integer, integer, integer, integer, double precision, double precision, character varying, character varying); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_text_category_probability(in_user_id integer, in_document_text_id integer, in_category_id integer, in_batch_number integer, in_probability_cat1 double precision, in_probability_cat2 double precision, in_cat1_label character varying, in_cat2_label character varying) RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
perform log_message(in_user_id, 'insert_text_category_probability', null);

insert into text_category_probabilities
(document_text_id, category_id, batch_number, probability_cat1, probability_cat2, cat1_label, cat2_label)
values
(in_document_text_id, in_category_id, in_batch_number, in_probability_cat1, in_probability_cat2, in_cat1_label, in_cat2_label);
end;
$$;


ALTER FUNCTION public.insert_text_category_probability(in_user_id integer, in_document_text_id integer, in_category_id integer, in_batch_number integer, in_probability_cat1 double precision, in_probability_cat2 double precision, in_cat1_label character varying, in_cat2_label character varying) OWNER TO ben;

--
-- Name: insert_training_data(integer, integer, integer, boolean); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_training_data(in_user_id integer, in_category_text_id integer, in_category_id integer, in_is_in_category boolean) RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
perform log_message(in_user_id, 'insert_training_data', null);

insert into category_training_data 
(category_text_id, category_id, is_in_category) 
values 
(in_category_text_id, in_category_id, in_is_in_category);

end;
$$;


ALTER FUNCTION public.insert_training_data(in_user_id integer, in_category_text_id integer, in_category_id integer, in_is_in_category boolean) OWNER TO ben;

--
-- Name: insert_user_role(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_user_role(in_user_id integer, in_role_user_id integer, in_role_id integer) RETURNS TABLE(id integer)
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin

perform log_message(in_user_id, 'insert_user_role', null);

insert into user_role_assignments
(user_id, role_id) VALUES
(in_role_user_id, in_role_id)
returning user_role_assignments.id into new_id;

return QUERY
select new_id;

end;
$$;


ALTER FUNCTION public.insert_user_role(in_user_id integer, in_role_user_id integer, in_role_id integer) OWNER TO ben;

--
-- Name: log_message(integer, text, text); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.log_message(in_user_id integer, in_called_procedure text, in_previous_state text) RETURNS void
    LANGUAGE plpgsql
    AS $$
begin

insert into logging
(user_id, called_procedure, previous_state)
values (in_user_id, in_called_procedure, in_previous_state);

end;
$$;


ALTER FUNCTION public.log_message(in_user_id integer, in_called_procedure text, in_previous_state text) OWNER TO ben;

--
-- Name: multiuser_cancel(integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.multiuser_cancel(in_user_id integer, in_checkout_id integer) RETURNS TABLE(status integer)
    LANGUAGE plpgsql
    AS $$
declare 
	return_value integer;
	row_counter integer;
begin

perform log_message(in_user_id, 'multiuser_cancel', null);

select count(*) into row_counter
from checked_out_documents cod
where cod.user_id = in_user_id
and cod.id = in_checkout_id;

if row_counter = 0 then
	return query
	select -1;
else
	delete from checked_out_documents
	where "id" = in_checkout_id;

	return query
	select 0;
end if;

end;
$$;


ALTER FUNCTION public.multiuser_cancel(in_user_id integer, in_checkout_id integer) OWNER TO ben;

--
-- Name: multiuser_checkout_document(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.multiuser_checkout_document(in_user_id integer) RETURNS TABLE(checkout_document_id integer, document_id integer)
    LANGUAGE plpgsql
    AS $$
declare 
	return_value integer;
	row_counter integer;
	row_id integer;
	new_row_id integer;
	timeout integer;
	now_time timestamp with time zone;
	init_status integer;
	next_status_id integer;
begin

perform log_message(in_user_id, 'multiuser_checkout_document', null);

timeout := null;
select checkout_timeout into timeout
from projects
limit 1;

if timeout is null then
	RAISE EXCEPTION 'checkout_timeout is not set for the project. Please make sure that the project is initialized correctly.';
end if;

init_status := null;
select labels.id into init_status
from multiuser_document_classification_status_labels labels
where labels.is_initialize = true;

if init_status is null then
	RAISE EXCEPTION 'There is not a status with the is_initialize flag set to true. Please configure your project such that there is a status with is_initialize set to true.';
end if;

--if they are already assigned a checked out document, give it back to them. 
select count(*) into row_counter
from multiuser_checked_out_documents
where user_id = in_user_id;

if row_counter != 0 then
	raise notice 'Row found with in_user_id %', in_user_id;
	return query
	select mcod.id, mcod.document_id
	from multiuser_checked_out_documents mcod
	where user_id = in_user_id
	for update skip locked; 
	
	update multiuser_checked_out_documents
	set createdate = now()
	where user_id = in_user_id;
	
	return;
end if;

--look for checked out documents first.
--if the checked out document was checked out
--outside of the timeout window, set that to the
--current user, update the createdate and return

now_time := now();

row_id := null;
select mcod.id into row_id
from multiuser_checked_out_documents mcod
where timeout < (select extract(epoch from(now_time - mcod.createdate)))
limit 1
for update skip locked;

if row_id is not null then
	raise notice 'found document with row_id % with timeout: %, now: %', row_id, timeout, now_time;
	update multiuser_checked_out_documents
	set createdate = now(), user_id = in_user_id
	where multiuser_checked_out_documents.id = row_id;
	
	return query
	select mcod.id, mcod.document_id
	from multiuser_checked_out_documents mcod
	where mcod.id = row_id;
	
	return;
end if;

row_id := null;
--see if there is a document that isn't checked out
--but doesn't have a "fininshed" status_id
--also, the user cannot have seen it before. 
select mcods.document_id into row_id
from multiuser_document_classification_status mcods
join multiuser_document_classification_status_labels mcods_labels on mcods.status_id = mcods_labels.id
where mcods_labels.is_ready_for_review = false
and not exists ( --it is not checked out
	select 1 
	from multiuser_checked_out_documents mcod 
	where mcods.document_id = mcod.document_id
) and not exists ( --and the current user hasn't seen it yet
	select 1
	from multiuser_checked_out_documents_history hist
	where hist.document_id = mcods.document_id
	and hist.user_id = in_user_id
)
limit 1
for update skip locked;

if row_id is not null then
	raise notice 'found document_id with %', row_id;

	insert into multiuser_checked_out_documents
	(user_id, document_id)
	values
	(in_user_id, row_id)
	returning id into new_row_id;
	
	return query
	select new_row_id, row_id;
	
	return;
end if;

--if we got to here, we have to create a new 
--multiuser_checked_out_documents record. 

select dt.id into row_id
from document_text dt
where dt.category_text_id is null
and not exists (
	select 1 
	from multiuser_checked_out_documents mcod
	where mcod.document_id = dt.id
)
limit 1
for update skip locked;

--briefly set this to inialized. 
insert into multiuser_document_classification_status
(document_id, status_id)
values
(row_id, init_status);

insert into multiuser_checked_out_documents
(user_id, document_id)
values
(in_user_id, row_id)
returning id into new_row_id;

--update the status to the next status after initalized
select labels.next_id into next_status_id
from multiuser_document_classification_status_labels labels
join multiuser_document_classification_status mdcs on mdcs.status_id = labels.id
where mdcs.document_id = row_id;

update multiuser_document_classification_status
set status_id = next_status_id
where multiuser_document_classification_status.document_id = row_id;

return query
select new_row_id, row_id;

return;

end;
$$;


ALTER FUNCTION public.multiuser_checkout_document(in_user_id integer) OWNER TO ben;

--
-- Name: multiuser_select_document_for_review(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.multiuser_select_document_for_review(in_user_id integer) RETURNS TABLE(document_id integer, user_id integer, category_id integer)
    LANGUAGE plpgsql
    AS $$
declare
	row_id integer;
	review_status integer;
begin

perform log_message(in_user_id, 'multiuser_checkout_document', null);

select labels.id into review_status
from multiuser_document_classification_status_labels labels
where labels.is_ready_for_review = true;

select mdcs.document_id into row_id
from multiuser_document_classification_status mdcs
where status_id = review_status
and not exists(
	select 1
	from multiuser_checked_out_documents mcod
	where mcod.document_id = mdcs.document_id
)
limit 1
for update skip locked; 

--is there isn't anything to review, don't review anything. 
if row_id is null then
	return query
	select null, null, null;
	
	return;
end if;

insert into multiuser_checked_out_documents
(user_id, document_id)
values
(in_user_id, row_id);

return query
select mcodh.document_id, mcodh.user_id, mcodh.category_id
from multiuser_checked_out_documents_history mcodh
where mcodh.document_id = row_id;

return;
end;
$$;


ALTER FUNCTION public.multiuser_select_document_for_review(in_user_id integer) OWNER TO ben;

--
-- Name: multiuser_set_category_admin(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.multiuser_set_category_admin(in_user_id integer, in_document_id integer, in_category_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
declare
	ct_id integer;
begin

perform log_message(in_user_id, 'multiuser_set_category_admin', null);

insert into multiuser_checked_out_documents_history
(user_id, document_id, category_id, reviewing_user_id, review_timestamp)
values
(in_user_id, in_document_id, in_category_id, in_user_id, now());

delete from multiuser_document_classification_status
where document_id = in_document_id;

select copy_document_to_category_text(in_user_id, in_document_id) into ct_id;

insert into category_training_data
(category_text_id, category_id, is_in_category)
values
(ct_id, in_category_id, 1);
				 
end;
$$;


ALTER FUNCTION public.multiuser_set_category_admin(in_user_id integer, in_document_id integer, in_category_id integer) OWNER TO ben;

--
-- Name: multiuser_set_category_user(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.multiuser_set_category_user(in_user_id integer, in_document_id integer, in_category_id integer) RETURNS void
    LANGUAGE plpgsql
    AS $$
declare 
	next_status_id integer;
begin

perform log_message(in_user_id, 'multiuser_set_category_user', null);

--check to see if the user currenlty owns the row
if not exists(
	select 1 
	from multiuser_checked_out_documents 
	where user_id = in_user_id 
	and document_id = in_document_id
) then
	RAISE EXCEPTION 'User with id: % does not own document with id: %', in_user_id, in_document_id;
	return;
end if;


insert into multiuser_checked_out_documents_history
(user_id, document_id, category_id)
values
(in_user_id, in_document_id, in_category_id);

delete from multiuser_checked_out_documents
where user_id = in_user_id
and document_id = in_document_id;

select next_id into next_status_id
from multiuser_document_classification_status_labels labels
join multiuser_document_classification_status mdcs on mdcs.status_id = labels.id
where mdcs.document_id = in_document_id;

update multiuser_document_classification_status
set status_id = next_status_id
where document_id = in_document_id;

				 
end;
$$;


ALTER FUNCTION public.multiuser_set_category_user(in_user_id integer, in_document_id integer, in_category_id integer) OWNER TO ben;

--
-- Name: select_all_training_text_from_category_id(integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_all_training_text_from_category_id(in_user_id integer, in_category_id integer) RETURNS TABLE(processed_text text)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin
perform log_message(in_user_id, 'select_all_training_text_from_category_id', null);

return QUERY 
select ct.processed_text
from category_training_data ctd
join category_text ct on ctd.category_text_id = ct.id
where ctd.category_id = in_category_id
order by ctd.category_text_id;

END;

$$;


ALTER FUNCTION public.select_all_training_text_from_category_id(in_user_id integer, in_category_id integer) OWNER TO ben;

--
-- Name: select_all_training_text_from_category_id(integer, integer, integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_all_training_text_from_category_id(in_user_id integer, in_category_id integer, in_start integer, in_limit integer) RETURNS TABLE(processed_text text)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin
perform log_message(in_user_id, 'select_all_training_text_from_category_id', null);

return QUERY
select ct.processed_text
from category_training_data ctd
join category_text ct on ctd.category_text_id = ct.id
where ctd.category_id = in_category_id
order by ctd.category_text_id
offset in_start
limit in_limit;

END;

$$;


ALTER FUNCTION public.select_all_training_text_from_category_id(in_user_id integer, in_category_id integer, in_start integer, in_limit integer) OWNER TO ben;

--
-- Name: select_batch_number(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_batch_number(in_user_id integer) RETURNS TABLE(category_batch bigint)
    LANGUAGE plpgsql
    AS $$
begin
perform log_message(in_user_id, 'select_batch_number', null);

return query
select last_value 
from text_category_batch_number_seq;

end;
$$;


ALTER FUNCTION public.select_batch_number(in_user_id integer) OWNER TO ben;

--
-- Name: select_categories(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_categories(in_user_id integer) RETURNS TABLE(id integer, category character varying)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin
perform log_message(in_user_id, 'select_categories', null);

return QUERY
select categories.id, categories.category 
from categories
order by categories.category;

END;

$$;


ALTER FUNCTION public.select_categories(in_user_id integer) OWNER TO ben;

--
-- Name: select_categories_with_probabilities(integer, double precision); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_categories_with_probabilities(in_user_id integer, in_probability_cat double precision) RETURNS TABLE(category_id integer, counts integer)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin 
perform log_message(in_user_id, 'select_categories_with_probabilities', null);

return QUERY
select tcp.category_id, count(*)
from text_category_probabilities tcp
join document_text dt on tcp.document_text_id = dt.id
where tcp.probability_cat2 > in_probability_cat
and st.category_text_id is null
group by category_id
order by category_id;

END;

$$;


ALTER FUNCTION public.select_categories_with_probabilities(in_user_id integer, in_probability_cat double precision) OWNER TO ben;

--
-- Name: select_category(integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_category(in_user_id integer, in_category_id integer) RETURNS TABLE(category character varying)
    LANGUAGE plpgsql
    AS $$
begin
perform log_message(in_user_id, 'select_category', null);

return query
select cat.category 
from categories cat
where cat.id = in_category_id;

end;
$$;


ALTER FUNCTION public.select_category(in_user_id integer, in_category_id integer) OWNER TO ben;

--
-- Name: select_category(integer, character varying); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_category(in_user_id integer, in_category_text character varying) RETURNS TABLE(category_id integer)
    LANGUAGE plpgsql
    AS $$
begin
perform log_message(in_user_id, 'select_category', null);

return query
select cat.id 
from categories cat
where cat.category = in_category_text;

end;
$$;


ALTER FUNCTION public.select_category(in_user_id integer, in_category_text character varying) OWNER TO ben;

--
-- Name: select_category_text(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_category_text(in_user_id integer) RETURNS TABLE(id integer, category_text text)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin
perform log_message(in_user_id, 'select_category_text', null);

return QUERY
select category_text.id, category_text.category_document_text from category_text where processed=FALSE order by category_text.id;

return QUERY
select category_text.id, category_text.category_document_text from category_text where processed=TRUE order by category_text.id;

END;

$$;


ALTER FUNCTION public.select_category_text(in_user_id integer) OWNER TO ben;

--
-- Name: select_category_text_from_id(integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_category_text_from_id(in_user_id integer, in_category_text_id integer) RETURNS TABLE(category_text text)
    LANGUAGE plpgsql
    AS $$

begin
perform log_message(in_user_id, 'select_category_text_from_id', null);

return QUERY
select category_text.category_document_text 
from category_text 
where processed=FALSE and 
id=in_category_text_id

union 

--return QUERY
select category_text.processed_text 
from category_text 
where processed=TRUE and
id=in_category_text_id;
end;

$$;


ALTER FUNCTION public.select_category_text_from_id(in_user_id integer, in_category_text_id integer) OWNER TO ben;

--
-- Name: select_category_text_tokens(integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_category_text_tokens(in_user_id integer, in_category_text_id integer) RETURNS TABLE(sentence_index integer, tok character varying, pos_tag character varying)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin
perform log_message(in_user_id, 'select_category_text_tokens', null);

--prefered stemmed tokens but if not avaialble, use the normal ones. 
return QUERY
select meta.sentence_index, meta.stemmed_token, meta.pos_tag
from category_sentences s
join category_sentences_metadata meta ON meta.category_sentence_id = s.id
where s.category_text_id = in_category_text_id
and meta.stemmed_token != 'O'

union 

select meta.sentence_index, meta.token, meta.pos_tag
from category_sentences s
join category_sentences_metadata meta ON meta.category_sentence_id = s.id
where s.category_text_id = in_category_text_id
and meta.stemmed_token = 'O'
order by sentence_index;

END;

$$;


ALTER FUNCTION public.select_category_text_tokens(in_user_id integer, in_category_text_id integer) OWNER TO ben;

--
-- Name: select_document_classification_status(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_document_classification_status(in_user_id integer) RETURNS TABLE(out_id integer, out_status_text text, out_next_id integer, out_is_ready_for_review bit, out_is_requires_admin bit)
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin

perform log_message(in_user_id, 'select_document_classification_status', null);

return query
select dcs.id, dcs.status_text, dcs.next_id, dcs.is_ready_for_review, dcs.is_requires_admin
from document_classification_status dcs;

end;
$$;


ALTER FUNCTION public.select_document_classification_status(in_user_id integer) OWNER TO ben;

--
-- Name: select_document_text(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_document_text(in_user_id integer) RETURNS TABLE(id integer, source_text text)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin
perform log_message(in_user_id, 'select_document_text', null);

return QUERY
select document_text.id, document_text.orginal_document_text from document_text where processed=FALSE order by document_text.id;

return QUERY
select document_text.id, document_text.processed_text from document_text where processed=TRUE order by document_text.id;

END;

$$;


ALTER FUNCTION public.select_document_text(in_user_id integer) OWNER TO ben;

--
-- Name: select_document_text_from_id(integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_document_text_from_id(in_user_id integer, in_document_text_id integer) RETURNS TABLE(document_text text)
    LANGUAGE plpgsql
    AS $$

begin
perform log_message(in_user_id, 'select_document_text_from_id', null);

return QUERY
select document_text.original_document_text 
from document_text 
where processed=FALSE and 
id=in_document_text_id;

return QUERY
select document_text.processed_text 
from document_text 
where processed=TRUE and
id=in_document_text_id;
end;

$$;


ALTER FUNCTION public.select_document_text_from_id(in_user_id integer, in_document_text_id integer) OWNER TO ben;

--
-- Name: select_document_text_ids(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_document_text_ids(in_user_id integer) RETURNS TABLE(id integer)
    LANGUAGE plpgsql
    AS $$
begin
perform log_message(in_user_id, 'select_document_text_ids', null);

select "id"
from document_text;

end;
$$;


ALTER FUNCTION public.select_document_text_ids(in_user_id integer) OWNER TO ben;

--
-- Name: select_document_tokens(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_document_tokens(in_user_id integer) RETURNS TABLE(source_id integer, sentence_index integer, source_token character varying, pos_tag character varying)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin 
perform log_message(in_user_id, 'select_document_tokens', null);

return QUERY
select dt.id, meta.sentence_index, meta.token, meta.pos_tag
from document_sentences_metadata meta
join document_sentences sent on meta.document_sentence_id = sent.id
join document_text dt on sent.document_text_id = dt.id;

END;

$$;


ALTER FUNCTION public.select_document_tokens(in_user_id integer) OWNER TO ben;

--
-- Name: select_job_from_id(integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_job_from_id(in_user_id integer, in_job_queue_id integer) RETURNS TABLE(job_queue_id integer, user_id integer, job_type_id integer, job_label character varying, job_status_id integer, job_staus_label character varying, next_status_id integer)
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin
perform log_message(in_user_id, 'select_job_from_id', null);

return query
select jq.id as "job_queue_id", jq.user_id, jt.id as "job_type_id", jt.job_label, js.id as "job_status_id", js.job_status_label, js.next_status_id
from job_queue jq
join job_types jt on jq.job_type_id = jt.id
join job_status js on jq.job_status_id = js.id
where jq.id = in_job_queue_id;

end;
$$;


ALTER FUNCTION public.select_job_from_id(in_user_id integer, in_job_queue_id integer) OWNER TO ben;

--
-- Name: select_job_params_from_job_id(integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_job_params_from_job_id(in_user_id integer, in_job_queue_id integer) RETURNS TABLE(job_queue_id integer, param_name character varying, param_value text, param_object_oid oid)
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin
perform log_message(in_user_id, 'select_job_params_from_job_id', null);

return query
select params.job_queue_id, params.param_name, params.param_value, params.param_object_oid
from job_additional_parameters params
where params.job_queue_id = in_job_queue_id;

end;
$$;


ALTER FUNCTION public.select_job_params_from_job_id(in_user_id integer, in_job_queue_id integer) OWNER TO ben;

--
-- Name: select_job_queue_ids(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_job_queue_ids(in_user_id integer) RETURNS TABLE(job_queue_id integer)
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin
perform log_message(in_user_id, 'select_job_queue_ids', null);

return query

select jq.id
from job_queue jq
order by jq.id;

end;
$$;


ALTER FUNCTION public.select_job_queue_ids(in_user_id integer) OWNER TO ben;

--
-- Name: select_job_status_list(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_job_status_list(in_user_id integer) RETURNS TABLE(job_status_id integer, job_status_label character varying, next_status_id integer)
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin
perform log_message(in_user_id, 'select_job_status_list', null);

return query
select js.id, js.job_status_label, js.next_status_id
from job_status js
order by js.id;

end;
$$;


ALTER FUNCTION public.select_job_status_list(in_user_id integer) OWNER TO ben;

--
-- Name: select_job_types_list(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_job_types_list(in_user_id integer) RETURNS TABLE(job_type_id integer, job_type_label character varying)
    LANGUAGE plpgsql
    AS $$
declare new_id integer;
begin
perform log_message(in_user_id, 'select_job_types_list', null);

return query

select jt.id, jt.job_label
from job_types jt
order by jt.id;

end;
$$;


ALTER FUNCTION public.select_job_types_list(in_user_id integer) OWNER TO ben;

--
-- Name: select_next_job(integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_next_job(in_user_id integer, in_not_started_status_id integer) RETURNS TABLE(job_queue_id integer)
    LANGUAGE plpgsql
    AS $$
declare job_queue_id integer;
begin
perform log_message(in_user_id, 'select_next_job', null);

select job_queue.id into job_queue_id
from job_queue
where job_status_id = in_not_started_status_id
limit 1
FOR UPDATE SKIP LOCKED;

update job_queue
set job_status_id = (select next_status_id from job_status where id = in_not_started_status_id)
where job_queue.id = job_queue_id;

return query
select job_queue_id;

end;
$$;


ALTER FUNCTION public.select_next_job(in_user_id integer, in_not_started_status_id integer) OWNER TO ben;

--
-- Name: select_next_job(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_next_job(in_user_id integer, in_not_started_status_id integer, in_job_type integer) RETURNS TABLE(job_queue_id integer)
    LANGUAGE plpgsql
    AS $$
declare job_queue_id integer;
begin
perform log_message(in_user_id, 'select_next_job', null);

select job_queue.id into job_queue_id
from job_queue
where job_status_id = in_not_started_status_id
and job_type_id = in_job_type
limit 1
FOR UPDATE SKIP LOCKED;

update job_queue
set job_status_id = (select next_status_id from job_status where id = in_not_started_status_id)
where job_queue.id = job_queue_id;

return query
select job_queue_id;

end;
$$;


ALTER FUNCTION public.select_next_job(in_user_id integer, in_not_started_status_id integer, in_job_type integer) OWNER TO ben;

--
-- Name: select_nlp_model(integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_nlp_model(in_user_id integer, in_category_id integer) RETURNS TABLE(category_model_oid oid)
    LANGUAGE plpgsql
    AS $$
begin
perform log_message(in_user_id, 'select_nlp_model', null);

return query
select category_model_file_oid
from categories
where id = in_category_id;

end;
$$;


ALTER FUNCTION public.select_nlp_model(in_user_id integer, in_category_id integer) OWNER TO ben;

--
-- Name: select_processed_files(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_processed_files(in_user_id integer) RETURNS TABLE(file_path character varying)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin
perform log_message(in_user_id, 'select_processed_files', null);

return QUERY
select completed_processed_files.file_path
from completed_processed_files
where is_complete = TRUE;

END;

$$;


ALTER FUNCTION public.select_processed_files(in_user_id integer) OWNER TO ben;

--
-- Name: select_roles_for_user(integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_roles_for_user(in_user_id integer, in_role_user_id integer) RETURNS TABLE(id integer, name character varying, display_name text, description text)
    LANGUAGE plpgsql
    AS $$
begin

perform log_message(in_user_id, 'select_roles_for_user', null);

return query
select r.id, r.name, r.display_name, r.description 
from user_role_assignments ura
join roles r on ura.role_id = r.id
where r.user_id = in_user_id;
	
end;
$$;


ALTER FUNCTION public.select_roles_for_user(in_user_id integer, in_role_user_id integer) OWNER TO ben;

--
-- Name: select_training_data(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_training_data(in_user_id integer) RETURNS TABLE(text_id integer, category_id integer, category_name character varying, is_in_category boolean)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin
perform log_message(in_user_id, 'select_training_data', null);

return QUERY
select train.category_text_id, train.category_id, cat.category, train.is_in_category
from category_training_data train
join categories cat on train.category_id = cat.id;

END;

$$;


ALTER FUNCTION public.select_training_data(in_user_id integer) OWNER TO ben;

--
-- Name: select_training_text(integer, integer, double precision); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_training_text(in_user_id integer, in_document_text_id integer, in_probability_cat double precision) RETURNS TABLE(processed_text text, category_id integer, category_name character varying)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin 
perform log_message(in_user_id, 'select_training_text', null);

return QUERY
select dt.processed_text, tcp.category_id, cat.category
from text_category_probabilities tcp
join document_text dt on tcp.document_text_id = dt.id
join categories cat on tcp.category_id = cat.id
where tcp.probability_cat2 > in_probability_cat
and dt.category_text_id is null
and tcp.document_text_id = in_document_text_id
order by tcp.category_id;

END;

$$;


ALTER FUNCTION public.select_training_text(in_user_id integer, in_document_text_id integer, in_probability_cat double precision) OWNER TO ben;

--
-- Name: select_training_text_all(integer, integer, double precision); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_training_text_all(in_user_id integer, in_document_text_id integer, in_probability_cat double precision) RETURNS TABLE(processed_text text, category_id integer, category_name character varying, category_text_id integer)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin 
perform log_message(in_user_id, 'select_training_text_all', null);

return QUERY
select dt.processed_text, tcp.category_id, cat.category, dt.category_text_id
from text_category_probabilities tcp
join document_text dt on tcp.document_text_id = dt.id
join categories cat on tcp.category_id = cat.id
where tcp.probability_cat2 > in_probability_cat
and tcp.document_text_id = in_document_text_id
order by tcp.category_id;

END;

$$;


ALTER FUNCTION public.select_training_text_all(in_user_id integer, in_document_text_id integer, in_probability_cat double precision) OWNER TO ben;

--
-- Name: select_training_text_counts(integer, double precision); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_training_text_counts(in_user_id integer, in_probability_cat double precision) RETURNS TABLE(text_id integer, counts bigint)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin 
perform log_message(in_user_id, 'select_training_text_counts', null);

return QUERY
select tcp.document_text_id, count(*)
from text_category_probabilities tcp
join document_text dt on tcp.document_text_id = dt.id
where tcp.probability_cat2 > in_probability_cat
and dt.category_text_id is null
group by tcp.document_text_id
order by tcp.document_text_id;

END;

$$;


ALTER FUNCTION public.select_training_text_counts(in_user_id integer, in_probability_cat double precision) OWNER TO ben;

--
-- Name: select_training_text_counts_all(integer, double precision); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_training_text_counts_all(in_user_id integer, in_probability_cat double precision) RETURNS TABLE(text_id integer, counts bigint)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin 
perform log_message(in_user_id, 'select_training_text_counts_all', null);

return QUERY
select tcp.document_text_id, count(*)
from text_category_probabilities tcp
join document_text dt on tcp.document_text_id = dt.id
where tcp.probability_cat2 > in_probability_cat
group by tcp.document_text_id
order by tcp.document_text_id;

END;

$$;


ALTER FUNCTION public.select_training_text_counts_all(in_user_id integer, in_probability_cat double precision) OWNER TO ben;

--
-- Name: select_training_text_for_category(integer, integer, double precision); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_training_text_for_category(in_user_id integer, in_category_id integer, in_probability_cat double precision) RETURNS TABLE(source_text_id integer, souce_text text)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin 
perform log_message(in_user_id, 'select_training_text_for_category', null);

return QUERY
select tcp.document_text_id, dt.processed_text
from text_category_probabilities tcp
join document_text dt on tcp.document_text_id = dt.id
where tcp.probability_cat2 > in_probability_cat
and dt.category_text_id is null
and tcp.category_id = in_category_id

union all

select tcp.document_text_id, dt.processed_text
from text_category_probabilities tcp
join document_text dt on tcp.document_text_id = dt.id
join category_text ct on dt.category_text_id = ct.id
where tcp.probability_cat2 > in_probability_cat
and 0 = (select count(*) from category_training_data ctd where ctd.category_text_id = ct.id and ctd.category_id = in_category_id)

order by document_text_id;

																   
END;

$$;


ALTER FUNCTION public.select_training_text_for_category(in_user_id integer, in_category_id integer, in_probability_cat double precision) OWNER TO ben;

--
-- Name: select_training_text_for_category(integer, integer, double precision, integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_training_text_for_category(in_user_id integer, in_category_id integer, in_probability_cat double precision, in_start integer, in_limit integer) RETURNS TABLE(source_text_id integer, souce_text text)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin 
perform log_message(in_user_id, 'select_training_text_for_category', null);

return QUERY
select tcp.document_text_id, dt.processed_text
from text_category_probabilities tcp
join document_text dt on tcp.document_text_id = dt.id
where tcp.probability_cat2 > in_probability_cat
and dt.category_text_id is null
and tcp.category_id = in_category_id

union 

select tcp.document_text_id, dt.processed_text
from text_category_probabilities tcp
join document_text dt on tcp.document_text_id = dt.id
join category_text ct on st.category_text_id = ct.id
where tcp.probability_cat2 > in_probability_cat
and 0 = (select count(*) from category_training_data ctd where ctd.category_text_id = ct.id and ctd.category_id = in_category_id)

--each of the selects has it's own table alias. Do not include an alias in the order by clause.
--https://stackoverflow.com/questions/24107125/error-missing-from-clause-entry-in-query-with-union-and-order-by
order by text_id
offset in_start
limit in_limit;
																   
END;

$$;


ALTER FUNCTION public.select_training_text_for_category(in_user_id integer, in_category_id integer, in_probability_cat double precision, in_start integer, in_limit integer) OWNER TO ben;

--
-- Name: select_training_text_next(integer, integer, double precision); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_training_text_next(in_user_id integer, in_start_text_id integer, in_probability_cat double precision) RETURNS TABLE(source_text_id integer, category_id integer, category character varying)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

DECLARE
	dt_id integer;
	batch_num bigint;
begin 
perform log_message(in_user_id, 'select_training_text_next', null);

select into dt_id dt.id
from document_text dt
where dt.category_text_id is null
and dt.id > in_start_text_id
order by dt.id
limit 1;

select into batch_num select_batch_number();

return QUERY
select tcp.document_text_id, tcp.category_id, cat.category
from text_category_probabilities tcp
join document_text dt on tcp.document_text_id = dt.id
join categories cat on tcp.category_id = cat.id
where tcp.probability_cat2 > in_probability_cat
and tcp.document_text_id = dt_id
and tcp.batch_number = batch_num
order by tcp.probability_cat2 desc;

END;

$$;


ALTER FUNCTION public.select_training_text_next(in_user_id integer, in_start_text_id integer, in_probability_cat double precision) OWNER TO ben;

--
-- Name: select_training_text_next_top_n(integer, integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_training_text_next_top_n(in_user_id integer, in_start_text_id integer, in_limit integer) RETURNS TABLE(document_text_id integer, category_id integer, category character varying, probability double precision)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

DECLARE
	dt_id integer;
	batch_num bigint;
begin 
perform log_message(in_user_id, 'select_training_text_next_top_n', null);

select into dt_id st.id
from document_text dt
where st.category_text_id is null
and dt.id > in_start_text_id
order by dt.id
limit 1;

select into batch_num select_batch_number();

return QUERY
select tcp.docment_text_id, tcp.category_id, cats.category, tcp.probability_cat2
from text_category_probabilities tcp
join document_text dt on tcp.document_text_id = dt.id
join categories cats on tcp.category_id = cats.id
where tcp.document_text_id = dt_id
and tcp.batch_number = batch_num
order by tcp.probability_cat2 desc
limit in_limit;

END;

$$;


ALTER FUNCTION public.select_training_text_next_top_n(in_user_id integer, in_start_text_id integer, in_limit integer) OWNER TO ben;

--
-- Name: select_training_text_next_top_n(integer, integer, integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_training_text_next_top_n(in_user_id integer, in_start_text_id integer, in_offset integer, in_limit integer) RETURNS TABLE(source_text_id integer, category_id integer, category character varying, probability double precision)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

DECLARE
	dt_id integer;
	batch_num bigint;
begin 
perform log_message(in_user_id, 'select_training_text_next_top_n', null);

select into dt_id dt.id
from document_text dt
where dt.category_text_id is null
and dt.id > in_start_text_id
order by dt.id
limit 1;

select into batch_num select_batch_number();

return QUERY
select tcp.document_text_id, tcp.category_id, cats.category, tcp.probability_cat2
from text_category_probabilities tcp
join document_text dt on tcp.document_text_id = dt.id
join categories cats on tcp.category_id = cats.id
where tcp.document_text_id = dt_id
and tcp.batch_number = batch_num
order by tcp.probability_cat2 desc
offset in_offset
limit in_limit;

END;

$$;


ALTER FUNCTION public.select_training_text_next_top_n(in_user_id integer, in_start_text_id integer, in_offset integer, in_limit integer) OWNER TO ben;

--
-- Name: select_unprocessed_files(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_unprocessed_files(in_user_id integer) RETURNS TABLE(file_path character varying)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin
perform log_message(in_user_id, 'select_unprocessed_files', null);

return QUERY
select completed_processed_files.file_path
from completed_processed_files
where is_complete = FALSE;

END;

$$;


ALTER FUNCTION public.select_unprocessed_files(in_user_id integer) OWNER TO ben;

--
-- Name: select_username_id(text); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_username_id(in_user_name text) RETURNS TABLE(user_id integer)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin
--no logging here because we don't have the in_user_id yet. 
--select log_message(in_user_id, 'select_username_id', null);

return QUERY
select u.id
from users u
where u.username = in_user_name;


END;
$$;


ALTER FUNCTION public.select_username_id(in_user_name text) OWNER TO ben;

--
-- Name: update_processed_files_set_finished(integer, character varying); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.update_processed_files_set_finished(in_user_id integer, in_file_path character varying) RETURNS void
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin
perform log_message(in_user_id, 'update_processed_files_set_finished', null);

update completed_processed_files
set is_complete = TRUE
where file_path = in_file_path;

END;

$$;


ALTER FUNCTION public.update_processed_files_set_finished(in_user_id integer, in_file_path character varying) OWNER TO ben;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: auth_group; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.auth_group (
    id integer NOT NULL,
    name character varying(80) NOT NULL
);


ALTER TABLE public.auth_group OWNER TO ben;

--
-- Name: auth_group_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.auth_group_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.auth_group_id_seq OWNER TO ben;

--
-- Name: auth_group_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.auth_group_id_seq OWNED BY public.auth_group.id;


--
-- Name: auth_group_permissions; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.auth_group_permissions (
    id integer NOT NULL,
    group_id integer NOT NULL,
    permission_id integer NOT NULL
);


ALTER TABLE public.auth_group_permissions OWNER TO ben;

--
-- Name: auth_group_permissions_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.auth_group_permissions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.auth_group_permissions_id_seq OWNER TO ben;

--
-- Name: auth_group_permissions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.auth_group_permissions_id_seq OWNED BY public.auth_group_permissions.id;


--
-- Name: auth_permission; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.auth_permission (
    id integer NOT NULL,
    name character varying(255) NOT NULL,
    content_type_id integer NOT NULL,
    codename character varying(100) NOT NULL
);


ALTER TABLE public.auth_permission OWNER TO ben;

--
-- Name: auth_permission_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.auth_permission_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.auth_permission_id_seq OWNER TO ben;

--
-- Name: auth_permission_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.auth_permission_id_seq OWNED BY public.auth_permission.id;


--
-- Name: auth_user; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.auth_user (
    id integer NOT NULL,
    password character varying(128) NOT NULL,
    last_login timestamp with time zone,
    is_superuser boolean NOT NULL,
    username character varying(150) NOT NULL,
    first_name character varying(30) NOT NULL,
    last_name character varying(150) NOT NULL,
    email character varying(254) NOT NULL,
    is_staff boolean NOT NULL,
    is_active boolean NOT NULL,
    date_joined timestamp with time zone NOT NULL
);


ALTER TABLE public.auth_user OWNER TO ben;

--
-- Name: auth_user_groups; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.auth_user_groups (
    id integer NOT NULL,
    user_id integer NOT NULL,
    group_id integer NOT NULL
);


ALTER TABLE public.auth_user_groups OWNER TO ben;

--
-- Name: auth_user_groups_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.auth_user_groups_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.auth_user_groups_id_seq OWNER TO ben;

--
-- Name: auth_user_groups_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.auth_user_groups_id_seq OWNED BY public.auth_user_groups.id;


--
-- Name: auth_user_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.auth_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.auth_user_id_seq OWNER TO ben;

--
-- Name: auth_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.auth_user_id_seq OWNED BY public.auth_user.id;


--
-- Name: auth_user_user_permissions; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.auth_user_user_permissions (
    id integer NOT NULL,
    user_id integer NOT NULL,
    permission_id integer NOT NULL
);


ALTER TABLE public.auth_user_user_permissions OWNER TO ben;

--
-- Name: auth_user_user_permissions_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.auth_user_user_permissions_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.auth_user_user_permissions_id_seq OWNER TO ben;

--
-- Name: auth_user_user_permissions_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.auth_user_user_permissions_id_seq OWNED BY public.auth_user_user_permissions.id;


--
-- Name: categories; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.categories (
    id integer NOT NULL,
    creation_date timestamp(6) with time zone DEFAULT now() NOT NULL,
    category character varying(100) NOT NULL,
    category_model_file_oid oid
);


ALTER TABLE public.categories OWNER TO ben;

--
-- Name: TABLE categories; Type: COMMENT; Schema: public; Owner: ben
--

COMMENT ON TABLE public.categories IS 'categories for the text. There are 2 per model: is_X and is_not_X. These are baked into the model file itself. ';


--
-- Name: categories_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.categories_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.categories_id_seq OWNER TO ben;

--
-- Name: categories_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.categories_id_seq OWNED BY public.categories.id;


--
-- Name: category_model_history; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.category_model_history (
    id integer NOT NULL,
    createdate timestamp(6) with time zone DEFAULT now() NOT NULL,
    category_id integer NOT NULL,
    category_model_file_oid oid NOT NULL
);


ALTER TABLE public.category_model_history OWNER TO ben;

--
-- Name: category_model_history_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.category_model_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.category_model_history_id_seq OWNER TO ben;

--
-- Name: category_model_history_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.category_model_history_id_seq OWNED BY public.category_model_history.id;


--
-- Name: category_sentences; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.category_sentences (
    id integer NOT NULL,
    category_text_id integer NOT NULL,
    sentence text NOT NULL,
    sentence_number integer NOT NULL
);


ALTER TABLE public.category_sentences OWNER TO ben;

--
-- Name: category_sentences_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.category_sentences_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.category_sentences_id_seq OWNER TO ben;

--
-- Name: category_sentences_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.category_sentences_id_seq OWNED BY public.category_sentences.id;


--
-- Name: category_sentences_metadata; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.category_sentences_metadata (
    id integer NOT NULL,
    category_sentence_id integer NOT NULL,
    sentence_index integer NOT NULL,
    token character varying(50) NOT NULL,
    pos_tag character varying(10) NOT NULL,
    chunk character varying(10) NOT NULL,
    stemmed_token character varying(50),
    is_raw_text boolean
);


ALTER TABLE public.category_sentences_metadata OWNER TO ben;

--
-- Name: category_sentences_metadata_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.category_sentences_metadata_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.category_sentences_metadata_id_seq OWNER TO ben;

--
-- Name: category_sentences_metadata_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.category_sentences_metadata_id_seq OWNED BY public.category_sentences_metadata.id;


--
-- Name: category_text; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.category_text (
    id integer NOT NULL,
    category_document_id character varying(100),
    category_document_text text NOT NULL,
    processed_text text,
    processed boolean DEFAULT false NOT NULL,
    num_tokens integer DEFAULT 0 NOT NULL,
    completed_processed_files_id integer NOT NULL
);


ALTER TABLE public.category_text OWNER TO ben;

--
-- Name: TABLE category_text; Type: COMMENT; Schema: public; Owner: ben
--

COMMENT ON TABLE public.category_text IS 'The source_text_id and source_text might be a bit misleading. Perhaps change them to org_text_id and org_text. These columns should come from the data source as a link to a document. For example, if a csv file has a column called "id" then the source_text_id column would contain the corresponding values in the "id" column. Documents going into this table are the "category" documents (i.e., known documents that represent a category). ';


--
-- Name: category_text_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.category_text_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.category_text_id_seq OWNER TO ben;

--
-- Name: category_text_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.category_text_id_seq OWNED BY public.category_text.id;


--
-- Name: category_training_data; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.category_training_data (
    id integer NOT NULL,
    creation_date timestamp(6) with time zone DEFAULT now() NOT NULL,
    category_text_id integer NOT NULL,
    category_id integer NOT NULL,
    is_in_category boolean DEFAULT true NOT NULL
);


ALTER TABLE public.category_training_data OWNER TO ben;

--
-- Name: TABLE category_training_data; Type: COMMENT; Schema: public; Owner: ben
--

COMMENT ON TABLE public.category_training_data IS 'This is the set of training data for each category. ';


--
-- Name: category_training_data_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.category_training_data_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.category_training_data_id_seq OWNER TO ben;

--
-- Name: category_training_data_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.category_training_data_id_seq OWNED BY public.category_training_data.id;


--
-- Name: multiuser_checked_out_documents_history; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.multiuser_checked_out_documents_history (
    id integer NOT NULL,
    createdate timestamp(6) with time zone DEFAULT now() NOT NULL,
    user_id integer NOT NULL,
    document_id integer NOT NULL,
    category_id integer NOT NULL,
    reviewing_user_id integer,
    review_timestamp time(6) with time zone
);


ALTER TABLE public.multiuser_checked_out_documents_history OWNER TO ben;

--
-- Name: checked_out_documents_history_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.checked_out_documents_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.checked_out_documents_history_id_seq OWNER TO ben;

--
-- Name: checked_out_documents_history_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.checked_out_documents_history_id_seq OWNED BY public.multiuser_checked_out_documents_history.id;


--
-- Name: completed_processed_files; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.completed_processed_files (
    id integer NOT NULL,
    file_path character varying(1000) NOT NULL,
    is_complete boolean NOT NULL
);


ALTER TABLE public.completed_processed_files OWNER TO ben;

--
-- Name: TABLE completed_processed_files; Type: COMMENT; Schema: public; Owner: ben
--

COMMENT ON TABLE public.completed_processed_files IS 'Files will be in 2 states: continuing and finished. The idea here is that if the files are not finished, delete everything with this file to clean it out and restart but if it is finished, ignore all finished files. ';


--
-- Name: completed_processed_files_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.completed_processed_files_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.completed_processed_files_id_seq OWNER TO ben;

--
-- Name: completed_processed_files_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.completed_processed_files_id_seq OWNED BY public.completed_processed_files.id;


--
-- Name: django_admin_log; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.django_admin_log (
    id integer NOT NULL,
    action_time timestamp with time zone NOT NULL,
    object_id text,
    object_repr character varying(200) NOT NULL,
    action_flag smallint NOT NULL,
    change_message text NOT NULL,
    content_type_id integer,
    user_id integer NOT NULL,
    CONSTRAINT django_admin_log_action_flag_check CHECK ((action_flag >= 0))
);


ALTER TABLE public.django_admin_log OWNER TO ben;

--
-- Name: django_admin_log_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.django_admin_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.django_admin_log_id_seq OWNER TO ben;

--
-- Name: django_admin_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.django_admin_log_id_seq OWNED BY public.django_admin_log.id;


--
-- Name: django_content_type; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.django_content_type (
    id integer NOT NULL,
    app_label character varying(100) NOT NULL,
    model character varying(100) NOT NULL
);


ALTER TABLE public.django_content_type OWNER TO ben;

--
-- Name: django_content_type_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.django_content_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.django_content_type_id_seq OWNER TO ben;

--
-- Name: django_content_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.django_content_type_id_seq OWNED BY public.django_content_type.id;


--
-- Name: django_migrations; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.django_migrations (
    id integer NOT NULL,
    app character varying(255) NOT NULL,
    name character varying(255) NOT NULL,
    applied timestamp with time zone NOT NULL
);


ALTER TABLE public.django_migrations OWNER TO ben;

--
-- Name: django_migrations_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.django_migrations_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.django_migrations_id_seq OWNER TO ben;

--
-- Name: django_migrations_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.django_migrations_id_seq OWNED BY public.django_migrations.id;


--
-- Name: django_session; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.django_session (
    session_key character varying(40) NOT NULL,
    session_data text NOT NULL,
    expire_date timestamp with time zone NOT NULL
);


ALTER TABLE public.django_session OWNER TO ben;

--
-- Name: document_sentences; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.document_sentences (
    id integer NOT NULL,
    document_text_id integer NOT NULL,
    sentence text NOT NULL,
    sentence_number integer NOT NULL
);


ALTER TABLE public.document_sentences OWNER TO ben;

--
-- Name: document_sentences_metadata; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.document_sentences_metadata (
    id integer NOT NULL,
    document_sentence_id integer NOT NULL,
    sentence_index integer NOT NULL,
    token character varying(50) NOT NULL,
    pos_tag character varying(10) NOT NULL,
    chunk character varying(10) NOT NULL,
    stemmed_token character varying(50),
    is_raw_text boolean
);


ALTER TABLE public.document_sentences_metadata OWNER TO ben;

--
-- Name: document_text; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.document_text (
    id integer NOT NULL,
    original_document_id character varying(100),
    original_document_text text NOT NULL,
    processed_text text,
    processed boolean DEFAULT false NOT NULL,
    num_tokens integer DEFAULT 0 NOT NULL,
    category_text_id integer,
    completed_processed_files_id integer NOT NULL
);


ALTER TABLE public.document_text OWNER TO ben;

--
-- Name: TABLE document_text; Type: COMMENT; Schema: public; Owner: ben
--

COMMENT ON TABLE public.document_text IS 'The table containing categorized text. This is optional but is useful when comparing source_text documents to known documents.';


--
-- Name: COLUMN document_text.original_document_id; Type: COMMENT; Schema: public; Owner: ben
--

COMMENT ON COLUMN public.document_text.original_document_id IS 'This is the ID from the orginal document. It isn''t used here but it will tie back to the ID of the orginal document. ';


--
-- Name: job_additional_parameters; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.job_additional_parameters (
    id integer NOT NULL,
    job_queue_id integer NOT NULL,
    param_name character varying(100) NOT NULL,
    param_value text,
    param_object_oid oid
);


ALTER TABLE public.job_additional_parameters OWNER TO ben;

--
-- Name: COLUMN job_additional_parameters.param_name; Type: COMMENT; Schema: public; Owner: ben
--

COMMENT ON COLUMN public.job_additional_parameters.param_name IS 'This is the name of the additional parameter.';


--
-- Name: COLUMN job_additional_parameters.param_value; Type: COMMENT; Schema: public; Owner: ben
--

COMMENT ON COLUMN public.job_additional_parameters.param_value IS 'This is the optional value of the parameter. It should probably only be null if the param_object_oid has a value. ';


--
-- Name: COLUMN job_additional_parameters.param_object_oid; Type: COMMENT; Schema: public; Owner: ben
--

COMMENT ON COLUMN public.job_additional_parameters.param_object_oid IS 'This is a Large object OID (https://jdbc.postgresql.org/documentation/head/binary-data.html) and probably should be null only when the param value is not null. ';


--
-- Name: job_additional_parameters_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.job_additional_parameters_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.job_additional_parameters_id_seq OWNER TO ben;

--
-- Name: job_additional_parameters_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.job_additional_parameters_id_seq OWNED BY public.job_additional_parameters.id;


--
-- Name: job_types; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.job_types (
    id integer NOT NULL,
    job_label character varying(100) NOT NULL
);


ALTER TABLE public.job_types OWNER TO ben;

--
-- Name: job_names_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.job_names_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.job_names_id_seq OWNER TO ben;

--
-- Name: job_names_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.job_names_id_seq OWNED BY public.job_types.id;


--
-- Name: job_queue; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.job_queue (
    id integer NOT NULL,
    user_id integer NOT NULL,
    job_type_id integer NOT NULL,
    job_status_id integer NOT NULL
);


ALTER TABLE public.job_queue OWNER TO ben;

--
-- Name: job_queue_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.job_queue_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.job_queue_id_seq OWNER TO ben;

--
-- Name: job_queue_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.job_queue_id_seq OWNED BY public.job_queue.id;


--
-- Name: job_status; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.job_status (
    id integer NOT NULL,
    job_status_label character varying(100) NOT NULL,
    next_status_id integer
);


ALTER TABLE public.job_status OWNER TO ben;

--
-- Name: COLUMN job_status.next_status_id; Type: COMMENT; Schema: public; Owner: ben
--

COMMENT ON COLUMN public.job_status.next_status_id IS 'The id of the next staus where null is none. ';


--
-- Name: job_status_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.job_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.job_status_id_seq OWNER TO ben;

--
-- Name: job_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.job_status_id_seq OWNED BY public.job_status.id;


--
-- Name: js_next_id; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.js_next_id (
    next_status_id integer
);


ALTER TABLE public.js_next_id OWNER TO ben;

--
-- Name: logging; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.logging (
    id bigint NOT NULL,
    create_date timestamp(6) with time zone DEFAULT now() NOT NULL,
    user_id integer NOT NULL,
    called_procedure text NOT NULL,
    previous_state text
);


ALTER TABLE public.logging OWNER TO ben;

--
-- Name: logging_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.logging_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.logging_id_seq OWNER TO ben;

--
-- Name: logging_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.logging_id_seq OWNED BY public.logging.id;


--
-- Name: multiuser_checked_out_documents; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.multiuser_checked_out_documents (
    id integer NOT NULL,
    createdate timestamp(6) with time zone DEFAULT now() NOT NULL,
    user_id integer NOT NULL,
    document_id integer NOT NULL
);


ALTER TABLE public.multiuser_checked_out_documents OWNER TO ben;

--
-- Name: multiuser_checked_out_documents_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.multiuser_checked_out_documents_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.multiuser_checked_out_documents_id_seq OWNER TO ben;

--
-- Name: multiuser_checked_out_documents_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.multiuser_checked_out_documents_id_seq OWNED BY public.multiuser_checked_out_documents.id;


--
-- Name: multiuser_document_classification_status; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.multiuser_document_classification_status (
    id integer NOT NULL,
    createdate timestamp(6) with time zone DEFAULT now() NOT NULL,
    document_id integer NOT NULL,
    status_id integer NOT NULL
);


ALTER TABLE public.multiuser_document_classification_status OWNER TO ben;

--
-- Name: multiuser_document_classification_status_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.multiuser_document_classification_status_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.multiuser_document_classification_status_id_seq OWNER TO ben;

--
-- Name: multiuser_document_classification_status_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.multiuser_document_classification_status_id_seq OWNED BY public.multiuser_document_classification_status.id;


--
-- Name: multiuser_document_classification_status_labels; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.multiuser_document_classification_status_labels (
    id integer NOT NULL,
    status_text text NOT NULL,
    next_id integer,
    is_ready_for_review boolean,
    is_initialize boolean
);


ALTER TABLE public.multiuser_document_classification_status_labels OWNER TO ben;

--
-- Name: multiuser_document_classification_status_labels_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.multiuser_document_classification_status_labels_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.multiuser_document_classification_status_labels_id_seq OWNER TO ben;

--
-- Name: multiuser_document_classification_status_labels_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.multiuser_document_classification_status_labels_id_seq OWNED BY public.multiuser_document_classification_status_labels.id;


--
-- Name: possible_duplicates; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.possible_duplicates (
    id integer NOT NULL,
    source_text_id integer NOT NULL,
    category_text_id integer NOT NULL,
    score double precision NOT NULL
);


ALTER TABLE public.possible_duplicates OWNER TO ben;

--
-- Name: possible_duplicates_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.possible_duplicates_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.possible_duplicates_id_seq OWNER TO ben;

--
-- Name: possible_duplicates_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.possible_duplicates_id_seq OWNED BY public.possible_duplicates.id;


--
-- Name: projects; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.projects (
    id integer NOT NULL,
    createdate timestamp(6) with time zone DEFAULT now() NOT NULL,
    owner_user_id integer NOT NULL,
    ready_for_review_id integer NOT NULL,
    checkout_timeout integer DEFAULT 600 NOT NULL
);


ALTER TABLE public.projects OWNER TO ben;

--
-- Name: COLUMN projects.checkout_timeout; Type: COMMENT; Schema: public; Owner: ben
--

COMMENT ON COLUMN public.projects.checkout_timeout IS 'This is the timeout in seconds for the checkout mechanism per project. ';


--
-- Name: projects_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.projects_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.projects_id_seq OWNER TO ben;

--
-- Name: projects_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.projects_id_seq OWNED BY public.projects.id;


--
-- Name: roles; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.roles (
    id integer NOT NULL,
    create_date timestamp(6) with time zone DEFAULT now() NOT NULL,
    name character varying(50) NOT NULL,
    display_name text,
    description text
);


ALTER TABLE public.roles OWNER TO ben;

--
-- Name: roles_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.roles_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.roles_id_seq OWNER TO ben;

--
-- Name: roles_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.roles_id_seq OWNED BY public.roles.id;


--
-- Name: selected_id; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.selected_id (
    id integer
);


ALTER TABLE public.selected_id OWNER TO ben;

--
-- Name: sorted_index; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.sorted_index (
    id integer NOT NULL,
    document_text_id integer NOT NULL,
    category_text_id integer NOT NULL,
    index integer NOT NULL,
    cos_theta double precision DEFAULT 0 NOT NULL
);


ALTER TABLE public.sorted_index OWNER TO ben;

--
-- Name: sorted_index_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.sorted_index_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sorted_index_id_seq OWNER TO ben;

--
-- Name: sorted_index_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.sorted_index_id_seq OWNED BY public.sorted_index.id;


--
-- Name: source_sentences_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.source_sentences_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.source_sentences_id_seq OWNER TO ben;

--
-- Name: source_sentences_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.source_sentences_id_seq OWNED BY public.document_sentences.id;


--
-- Name: source_sentences_metadata_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.source_sentences_metadata_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.source_sentences_metadata_id_seq OWNER TO ben;

--
-- Name: source_sentences_metadata_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.source_sentences_metadata_id_seq OWNED BY public.document_sentences_metadata.id;


--
-- Name: source_text_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.source_text_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.source_text_id_seq OWNER TO ben;

--
-- Name: source_text_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.source_text_id_seq OWNED BY public.document_text.id;


--
-- Name: spelling_corrections; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.spelling_corrections (
    id integer NOT NULL,
    orginal_word text NOT NULL,
    recomended_correction text,
    num integer NOT NULL
);


ALTER TABLE public.spelling_corrections OWNER TO ben;

--
-- Name: spelling_corrections_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.spelling_corrections_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.spelling_corrections_id_seq OWNER TO ben;

--
-- Name: spelling_corrections_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.spelling_corrections_id_seq OWNED BY public.spelling_corrections.id;


--
-- Name: text_category_batch_number_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.text_category_batch_number_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.text_category_batch_number_seq OWNER TO ben;

--
-- Name: SEQUENCE text_category_batch_number_seq; Type: COMMENT; Schema: public; Owner: ben
--

COMMENT ON SEQUENCE public.text_category_batch_number_seq IS 'the batch number for the text_category. This should allow users to generate a history of how documents got classified over time. ';


--
-- Name: text_category_probabilities; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.text_category_probabilities (
    id integer NOT NULL,
    creation_date timestamp(6) with time zone DEFAULT now() NOT NULL,
    document_text_id integer NOT NULL,
    category_id integer NOT NULL,
    batch_number bigint NOT NULL,
    probability_cat1 double precision NOT NULL,
    probability_cat2 double precision NOT NULL,
    cat1_label character varying(50),
    cat2_label character varying(50)
);


ALTER TABLE public.text_category_probabilities OWNER TO ben;

--
-- Name: TABLE text_category_probabilities; Type: COMMENT; Schema: public; Owner: ben
--

COMMENT ON TABLE public.text_category_probabilities IS 'a map between the text and the category. ';


--
-- Name: COLUMN text_category_probabilities.document_text_id; Type: COMMENT; Schema: public; Owner: ben
--

COMMENT ON COLUMN public.text_category_probabilities.document_text_id IS 'The text_id initally linked to the category_text. ';


--
-- Name: text_category_map_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.text_category_map_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.text_category_map_id_seq OWNER TO ben;

--
-- Name: text_category_map_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.text_category_map_id_seq OWNED BY public.text_category_probabilities.id;


--
-- Name: text_category_probabilities_history; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.text_category_probabilities_history (
    id integer NOT NULL,
    creation_date timestamp(6) with time zone DEFAULT now() NOT NULL,
    text_id integer NOT NULL,
    category_id integer NOT NULL,
    batch_number bigint NOT NULL,
    probability_cat1 double precision NOT NULL,
    probability_cat2 double precision NOT NULL,
    cat1_label character varying(50),
    cat2_label character varying(50)
);


ALTER TABLE public.text_category_probabilities_history OWNER TO ben;

--
-- Name: TABLE text_category_probabilities_history; Type: COMMENT; Schema: public; Owner: ben
--

COMMENT ON TABLE public.text_category_probabilities_history IS 'a map between the text and the category. ';


--
-- Name: COLUMN text_category_probabilities_history.text_id; Type: COMMENT; Schema: public; Owner: ben
--

COMMENT ON COLUMN public.text_category_probabilities_history.text_id IS 'The text_id initally linked to the category_text. ';


--
-- Name: text_category_probabilities_history_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.text_category_probabilities_history_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.text_category_probabilities_history_id_seq OWNER TO ben;

--
-- Name: text_category_probabilities_history_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.text_category_probabilities_history_id_seq OWNED BY public.text_category_probabilities_history.id;


--
-- Name: user_role_assignments; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.user_role_assignments (
    id integer NOT NULL,
    create_date timestamp(6) with time zone DEFAULT now() NOT NULL,
    user_id integer NOT NULL,
    role_id integer NOT NULL
);


ALTER TABLE public.user_role_assignments OWNER TO ben;

--
-- Name: user_role_assignments_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.user_role_assignments_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.user_role_assignments_id_seq OWNER TO ben;

--
-- Name: user_role_assignments_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.user_role_assignments_id_seq OWNED BY public.user_role_assignments.id;


--
-- Name: users; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.users (
    id integer NOT NULL,
    username text NOT NULL,
    email text NOT NULL,
    first_name text,
    last_name text,
    create_date timestamp(6) with time zone DEFAULT now() NOT NULL
);


ALTER TABLE public.users OWNER TO ben;

--
-- Name: users_id_seq; Type: SEQUENCE; Schema: public; Owner: ben
--

CREATE SEQUENCE public.users_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.users_id_seq OWNER TO ben;

--
-- Name: users_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: ben
--

ALTER SEQUENCE public.users_id_seq OWNED BY public.users.id;


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_group ALTER COLUMN id SET DEFAULT nextval('public.auth_group_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_group_permissions ALTER COLUMN id SET DEFAULT nextval('public.auth_group_permissions_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_permission ALTER COLUMN id SET DEFAULT nextval('public.auth_permission_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_user ALTER COLUMN id SET DEFAULT nextval('public.auth_user_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_user_groups ALTER COLUMN id SET DEFAULT nextval('public.auth_user_groups_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_user_user_permissions ALTER COLUMN id SET DEFAULT nextval('public.auth_user_user_permissions_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.categories ALTER COLUMN id SET DEFAULT nextval('public.categories_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.category_model_history ALTER COLUMN id SET DEFAULT nextval('public.category_model_history_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.category_sentences ALTER COLUMN id SET DEFAULT nextval('public.category_sentences_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.category_sentences_metadata ALTER COLUMN id SET DEFAULT nextval('public.category_sentences_metadata_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.category_text ALTER COLUMN id SET DEFAULT nextval('public.category_text_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.category_training_data ALTER COLUMN id SET DEFAULT nextval('public.category_training_data_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.completed_processed_files ALTER COLUMN id SET DEFAULT nextval('public.completed_processed_files_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.django_admin_log ALTER COLUMN id SET DEFAULT nextval('public.django_admin_log_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.django_content_type ALTER COLUMN id SET DEFAULT nextval('public.django_content_type_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.django_migrations ALTER COLUMN id SET DEFAULT nextval('public.django_migrations_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.document_sentences ALTER COLUMN id SET DEFAULT nextval('public.source_sentences_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.document_sentences_metadata ALTER COLUMN id SET DEFAULT nextval('public.source_sentences_metadata_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.document_text ALTER COLUMN id SET DEFAULT nextval('public.source_text_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.job_additional_parameters ALTER COLUMN id SET DEFAULT nextval('public.job_additional_parameters_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.job_queue ALTER COLUMN id SET DEFAULT nextval('public.job_queue_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.job_status ALTER COLUMN id SET DEFAULT nextval('public.job_status_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.job_types ALTER COLUMN id SET DEFAULT nextval('public.job_names_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.logging ALTER COLUMN id SET DEFAULT nextval('public.logging_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.multiuser_checked_out_documents ALTER COLUMN id SET DEFAULT nextval('public.multiuser_checked_out_documents_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.multiuser_checked_out_documents_history ALTER COLUMN id SET DEFAULT nextval('public.checked_out_documents_history_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.multiuser_document_classification_status ALTER COLUMN id SET DEFAULT nextval('public.multiuser_document_classification_status_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.multiuser_document_classification_status_labels ALTER COLUMN id SET DEFAULT nextval('public.multiuser_document_classification_status_labels_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.possible_duplicates ALTER COLUMN id SET DEFAULT nextval('public.possible_duplicates_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.projects ALTER COLUMN id SET DEFAULT nextval('public.projects_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.roles ALTER COLUMN id SET DEFAULT nextval('public.roles_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.sorted_index ALTER COLUMN id SET DEFAULT nextval('public.sorted_index_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.spelling_corrections ALTER COLUMN id SET DEFAULT nextval('public.spelling_corrections_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.text_category_probabilities ALTER COLUMN id SET DEFAULT nextval('public.text_category_map_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.text_category_probabilities_history ALTER COLUMN id SET DEFAULT nextval('public.text_category_probabilities_history_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.user_role_assignments ALTER COLUMN id SET DEFAULT nextval('public.user_role_assignments_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.users ALTER COLUMN id SET DEFAULT nextval('public.users_id_seq'::regclass);


--
-- Name: auth_group_name_key; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_group
    ADD CONSTRAINT auth_group_name_key UNIQUE (name);


--
-- Name: auth_group_permissions_group_id_permission_id_0cd325b0_uniq; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_group_permissions
    ADD CONSTRAINT auth_group_permissions_group_id_permission_id_0cd325b0_uniq UNIQUE (group_id, permission_id);


--
-- Name: auth_group_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_group_permissions
    ADD CONSTRAINT auth_group_permissions_pkey PRIMARY KEY (id);


--
-- Name: auth_group_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_group
    ADD CONSTRAINT auth_group_pkey PRIMARY KEY (id);


--
-- Name: auth_permission_content_type_id_codename_01ab375a_uniq; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_permission
    ADD CONSTRAINT auth_permission_content_type_id_codename_01ab375a_uniq UNIQUE (content_type_id, codename);


--
-- Name: auth_permission_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_permission
    ADD CONSTRAINT auth_permission_pkey PRIMARY KEY (id);


--
-- Name: auth_user_groups_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_user_groups
    ADD CONSTRAINT auth_user_groups_pkey PRIMARY KEY (id);


--
-- Name: auth_user_groups_user_id_group_id_94350c0c_uniq; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_user_groups
    ADD CONSTRAINT auth_user_groups_user_id_group_id_94350c0c_uniq UNIQUE (user_id, group_id);


--
-- Name: auth_user_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_user
    ADD CONSTRAINT auth_user_pkey PRIMARY KEY (id);


--
-- Name: auth_user_user_permissions_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_user_user_permissions
    ADD CONSTRAINT auth_user_user_permissions_pkey PRIMARY KEY (id);


--
-- Name: auth_user_user_permissions_user_id_permission_id_14a6b632_uniq; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_user_user_permissions
    ADD CONSTRAINT auth_user_user_permissions_user_id_permission_id_14a6b632_uniq UNIQUE (user_id, permission_id);


--
-- Name: auth_user_username_key; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_user
    ADD CONSTRAINT auth_user_username_key UNIQUE (username);


--
-- Name: categories_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.categories
    ADD CONSTRAINT categories_pkey PRIMARY KEY (id);


--
-- Name: category_model_history_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.category_model_history
    ADD CONSTRAINT category_model_history_pkey PRIMARY KEY (id);


--
-- Name: category_sentences_id_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.category_sentences
    ADD CONSTRAINT category_sentences_id_pkey PRIMARY KEY (id);


--
-- Name: category_sentences_metadata_id_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.category_sentences_metadata
    ADD CONSTRAINT category_sentences_metadata_id_pkey PRIMARY KEY (id);


--
-- Name: category_text_id_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.category_text
    ADD CONSTRAINT category_text_id_pkey PRIMARY KEY (id);


--
-- Name: category_training_data_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.category_training_data
    ADD CONSTRAINT category_training_data_pkey PRIMARY KEY (id);


--
-- Name: category_training_data_text_id_category_id_key; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.category_training_data
    ADD CONSTRAINT category_training_data_text_id_category_id_key UNIQUE (category_text_id, category_id);


--
-- Name: checked_out_documents_history_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.multiuser_checked_out_documents_history
    ADD CONSTRAINT checked_out_documents_history_pkey PRIMARY KEY (id);


--
-- Name: checked_out_documents_history_user_id_document_id_key; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.multiuser_checked_out_documents_history
    ADD CONSTRAINT checked_out_documents_history_user_id_document_id_key UNIQUE (user_id, document_id);


--
-- Name: completed_processed_files_file_path_key; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.completed_processed_files
    ADD CONSTRAINT completed_processed_files_file_path_key UNIQUE (file_path);


--
-- Name: completed_processed_files_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.completed_processed_files
    ADD CONSTRAINT completed_processed_files_pkey PRIMARY KEY (id);


--
-- Name: django_admin_log_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.django_admin_log
    ADD CONSTRAINT django_admin_log_pkey PRIMARY KEY (id);


--
-- Name: django_content_type_app_label_model_76bd3d3b_uniq; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.django_content_type
    ADD CONSTRAINT django_content_type_app_label_model_76bd3d3b_uniq UNIQUE (app_label, model);


--
-- Name: django_content_type_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.django_content_type
    ADD CONSTRAINT django_content_type_pkey PRIMARY KEY (id);


--
-- Name: django_migrations_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.django_migrations
    ADD CONSTRAINT django_migrations_pkey PRIMARY KEY (id);


--
-- Name: django_session_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.django_session
    ADD CONSTRAINT django_session_pkey PRIMARY KEY (session_key);


--
-- Name: document_classification_status_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.multiuser_document_classification_status_labels
    ADD CONSTRAINT document_classification_status_pkey PRIMARY KEY (id);


--
-- Name: job_additional_parameters_check; Type: CHECK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE public.job_additional_parameters
    ADD CONSTRAINT job_additional_parameters_check CHECK (((param_value IS NOT NULL) OR (param_object_oid IS NOT NULL))) NOT VALID;


--
-- Name: job_additional_parameters_job_queue_id_param_name_key; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.job_additional_parameters
    ADD CONSTRAINT job_additional_parameters_job_queue_id_param_name_key UNIQUE (job_queue_id, param_name);


--
-- Name: job_additional_parameters_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.job_additional_parameters
    ADD CONSTRAINT job_additional_parameters_pkey PRIMARY KEY (id);


--
-- Name: job_names_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.job_types
    ADD CONSTRAINT job_names_pkey PRIMARY KEY (id);


--
-- Name: job_queue_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.job_queue
    ADD CONSTRAINT job_queue_pkey PRIMARY KEY (id);


--
-- Name: job_status_job_status_label_key; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.job_status
    ADD CONSTRAINT job_status_job_status_label_key UNIQUE (job_status_label);


--
-- Name: job_status_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.job_status
    ADD CONSTRAINT job_status_pkey PRIMARY KEY (id);


--
-- Name: job_types_job_label_key; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.job_types
    ADD CONSTRAINT job_types_job_label_key UNIQUE (job_label);


--
-- Name: logging_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.logging
    ADD CONSTRAINT logging_pkey PRIMARY KEY (id);


--
-- Name: multiuser_checked_out_documents_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.multiuser_checked_out_documents
    ADD CONSTRAINT multiuser_checked_out_documents_pkey PRIMARY KEY (id);


--
-- Name: multiuser_checked_out_documents_user_id_document_id_key; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.multiuser_checked_out_documents
    ADD CONSTRAINT multiuser_checked_out_documents_user_id_document_id_key UNIQUE (user_id, document_id);


--
-- Name: multiuser_document_classification_status_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.multiuser_document_classification_status
    ADD CONSTRAINT multiuser_document_classification_status_pkey PRIMARY KEY (id);


--
-- Name: possible_duplicates_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.possible_duplicates
    ADD CONSTRAINT possible_duplicates_pkey PRIMARY KEY (id);


--
-- Name: projects_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT projects_pkey PRIMARY KEY (id);


--
-- Name: roles_name_key; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_name_key UNIQUE (name);


--
-- Name: roles_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.roles
    ADD CONSTRAINT roles_pkey PRIMARY KEY (id);


--
-- Name: sorted_index_id_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.sorted_index
    ADD CONSTRAINT sorted_index_id_pkey PRIMARY KEY (id);


--
-- Name: source_sentences_id_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.document_sentences
    ADD CONSTRAINT source_sentences_id_pkey PRIMARY KEY (id);


--
-- Name: source_sentences_metadata_id_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.document_sentences_metadata
    ADD CONSTRAINT source_sentences_metadata_id_pkey PRIMARY KEY (id);


--
-- Name: source_text_id_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.document_text
    ADD CONSTRAINT source_text_id_pkey PRIMARY KEY (id);


--
-- Name: spelling_corrections_orginal_word_unq; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.spelling_corrections
    ADD CONSTRAINT spelling_corrections_orginal_word_unq UNIQUE (orginal_word);


--
-- Name: spelling_corrections_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.spelling_corrections
    ADD CONSTRAINT spelling_corrections_pkey PRIMARY KEY (id);


--
-- Name: text_category_map_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.text_category_probabilities
    ADD CONSTRAINT text_category_map_pkey PRIMARY KEY (id);


--
-- Name: text_category_probabilities_history_map_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.text_category_probabilities_history
    ADD CONSTRAINT text_category_probabilities_history_map_pkey PRIMARY KEY (id);


--
-- Name: user_role_assignments_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.user_role_assignments
    ADD CONSTRAINT user_role_assignments_pkey PRIMARY KEY (id);


--
-- Name: users_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.users
    ADD CONSTRAINT users_pkey PRIMARY KEY (id);


--
-- Name: auth_group_name_a6ea08ec_like; Type: INDEX; Schema: public; Owner: ben
--

CREATE INDEX auth_group_name_a6ea08ec_like ON public.auth_group USING btree (name varchar_pattern_ops);


--
-- Name: auth_group_permissions_group_id_b120cbf9; Type: INDEX; Schema: public; Owner: ben
--

CREATE INDEX auth_group_permissions_group_id_b120cbf9 ON public.auth_group_permissions USING btree (group_id);


--
-- Name: auth_group_permissions_permission_id_84c5c92e; Type: INDEX; Schema: public; Owner: ben
--

CREATE INDEX auth_group_permissions_permission_id_84c5c92e ON public.auth_group_permissions USING btree (permission_id);


--
-- Name: auth_permission_content_type_id_2f476e4b; Type: INDEX; Schema: public; Owner: ben
--

CREATE INDEX auth_permission_content_type_id_2f476e4b ON public.auth_permission USING btree (content_type_id);


--
-- Name: auth_user_groups_group_id_97559544; Type: INDEX; Schema: public; Owner: ben
--

CREATE INDEX auth_user_groups_group_id_97559544 ON public.auth_user_groups USING btree (group_id);


--
-- Name: auth_user_groups_user_id_6a12ed8b; Type: INDEX; Schema: public; Owner: ben
--

CREATE INDEX auth_user_groups_user_id_6a12ed8b ON public.auth_user_groups USING btree (user_id);


--
-- Name: auth_user_user_permissions_permission_id_1fbb5f2c; Type: INDEX; Schema: public; Owner: ben
--

CREATE INDEX auth_user_user_permissions_permission_id_1fbb5f2c ON public.auth_user_user_permissions USING btree (permission_id);


--
-- Name: auth_user_user_permissions_user_id_a95ead1b; Type: INDEX; Schema: public; Owner: ben
--

CREATE INDEX auth_user_user_permissions_user_id_a95ead1b ON public.auth_user_user_permissions USING btree (user_id);


--
-- Name: auth_user_username_6821ab7c_like; Type: INDEX; Schema: public; Owner: ben
--

CREATE INDEX auth_user_username_6821ab7c_like ON public.auth_user USING btree (username varchar_pattern_ops);


--
-- Name: django_admin_log_content_type_id_c4bce8eb; Type: INDEX; Schema: public; Owner: ben
--

CREATE INDEX django_admin_log_content_type_id_c4bce8eb ON public.django_admin_log USING btree (content_type_id);


--
-- Name: django_admin_log_user_id_c564eba6; Type: INDEX; Schema: public; Owner: ben
--

CREATE INDEX django_admin_log_user_id_c564eba6 ON public.django_admin_log USING btree (user_id);


--
-- Name: django_session_expire_date_a5c62663; Type: INDEX; Schema: public; Owner: ben
--

CREATE INDEX django_session_expire_date_a5c62663 ON public.django_session USING btree (expire_date);


--
-- Name: django_session_session_key_c0390e0f_like; Type: INDEX; Schema: public; Owner: ben
--

CREATE INDEX django_session_session_key_c0390e0f_like ON public.django_session USING btree (session_key varchar_pattern_ops);


--
-- Name: auth_group_permissio_permission_id_84c5c92e_fk_auth_perm; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_group_permissions
    ADD CONSTRAINT auth_group_permissio_permission_id_84c5c92e_fk_auth_perm FOREIGN KEY (permission_id) REFERENCES public.auth_permission(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: auth_group_permissions_group_id_b120cbf9_fk_auth_group_id; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_group_permissions
    ADD CONSTRAINT auth_group_permissions_group_id_b120cbf9_fk_auth_group_id FOREIGN KEY (group_id) REFERENCES public.auth_group(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: auth_permission_content_type_id_2f476e4b_fk_django_co; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_permission
    ADD CONSTRAINT auth_permission_content_type_id_2f476e4b_fk_django_co FOREIGN KEY (content_type_id) REFERENCES public.django_content_type(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: auth_user_groups_group_id_97559544_fk_auth_group_id; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_user_groups
    ADD CONSTRAINT auth_user_groups_group_id_97559544_fk_auth_group_id FOREIGN KEY (group_id) REFERENCES public.auth_group(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: auth_user_groups_user_id_6a12ed8b_fk_auth_user_id; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_user_groups
    ADD CONSTRAINT auth_user_groups_user_id_6a12ed8b_fk_auth_user_id FOREIGN KEY (user_id) REFERENCES public.auth_user(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: auth_user_user_permi_permission_id_1fbb5f2c_fk_auth_perm; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_user_user_permissions
    ADD CONSTRAINT auth_user_user_permi_permission_id_1fbb5f2c_fk_auth_perm FOREIGN KEY (permission_id) REFERENCES public.auth_permission(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: auth_user_user_permissions_user_id_a95ead1b_fk_auth_user_id; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.auth_user_user_permissions
    ADD CONSTRAINT auth_user_user_permissions_user_id_a95ead1b_fk_auth_user_id FOREIGN KEY (user_id) REFERENCES public.auth_user(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: category_map_to_category_fk; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.text_category_probabilities
    ADD CONSTRAINT category_map_to_category_fk FOREIGN KEY (category_id) REFERENCES public.categories(id);


--
-- Name: category_map_to_category_fk; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.text_category_probabilities_history
    ADD CONSTRAINT category_map_to_category_fk FOREIGN KEY (category_id) REFERENCES public.categories(id);


--
-- Name: category_model_history_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.category_model_history
    ADD CONSTRAINT category_model_history_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.categories(id);


--
-- Name: category_sentences_category_text_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.category_sentences
    ADD CONSTRAINT category_sentences_category_text_id_fkey FOREIGN KEY (category_text_id) REFERENCES public.category_text(id) ON DELETE CASCADE;


--
-- Name: category_sentences_metadata_category_sentence_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.category_sentences_metadata
    ADD CONSTRAINT category_sentences_metadata_category_sentence_id_fkey FOREIGN KEY (category_sentence_id) REFERENCES public.category_sentences(id) ON DELETE CASCADE;


--
-- Name: category_sentences_text_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.category_sentences
    ADD CONSTRAINT category_sentences_text_id_fkey FOREIGN KEY (category_text_id) REFERENCES public.category_text(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: category_text_completed_processed_files_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.category_text
    ADD CONSTRAINT category_text_completed_processed_files_id_fkey FOREIGN KEY (completed_processed_files_id) REFERENCES public.completed_processed_files(id) ON DELETE CASCADE;


--
-- Name: category_training_data_category_text_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.category_training_data
    ADD CONSTRAINT category_training_data_category_text_id_fkey FOREIGN KEY (category_text_id) REFERENCES public.category_text(id) ON DELETE CASCADE;


--
-- Name: category_training_data_to_category_fk; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.category_training_data
    ADD CONSTRAINT category_training_data_to_category_fk FOREIGN KEY (category_id) REFERENCES public.categories(id);


--
-- Name: checked_out_documents_document_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.multiuser_checked_out_documents
    ADD CONSTRAINT checked_out_documents_document_id_fkey FOREIGN KEY (document_id) REFERENCES public.document_text(id);


--
-- Name: checked_out_documents_history_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.multiuser_checked_out_documents_history
    ADD CONSTRAINT checked_out_documents_history_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.categories(id);


--
-- Name: checked_out_documents_history_document_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.multiuser_checked_out_documents_history
    ADD CONSTRAINT checked_out_documents_history_document_id_fkey FOREIGN KEY (document_id) REFERENCES public.document_text(id);


--
-- Name: checked_out_documents_history_reviewing_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.multiuser_checked_out_documents_history
    ADD CONSTRAINT checked_out_documents_history_reviewing_user_id_fkey FOREIGN KEY (reviewing_user_id) REFERENCES public.users(id);


--
-- Name: checked_out_documents_history_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.multiuser_checked_out_documents_history
    ADD CONSTRAINT checked_out_documents_history_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: checked_out_documents_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.multiuser_checked_out_documents
    ADD CONSTRAINT checked_out_documents_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: django_admin_log_content_type_id_c4bce8eb_fk_django_co; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.django_admin_log
    ADD CONSTRAINT django_admin_log_content_type_id_c4bce8eb_fk_django_co FOREIGN KEY (content_type_id) REFERENCES public.django_content_type(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: django_admin_log_user_id_c564eba6_fk_auth_user_id; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.django_admin_log
    ADD CONSTRAINT django_admin_log_user_id_c564eba6_fk_auth_user_id FOREIGN KEY (user_id) REFERENCES public.auth_user(id) DEFERRABLE INITIALLY DEFERRED;


--
-- Name: document_sentences_document_text_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.document_sentences
    ADD CONSTRAINT document_sentences_document_text_id_fkey FOREIGN KEY (document_text_id) REFERENCES public.document_text(id) ON DELETE CASCADE;


--
-- Name: document_sentences_metadata_source_sentence_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.document_sentences_metadata
    ADD CONSTRAINT document_sentences_metadata_source_sentence_id_fkey FOREIGN KEY (document_sentence_id) REFERENCES public.document_sentences(id) ON DELETE CASCADE;


--
-- Name: document_text_completed_processed_files_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.document_text
    ADD CONSTRAINT document_text_completed_processed_files_id_fkey FOREIGN KEY (completed_processed_files_id) REFERENCES public.completed_processed_files(id) ON DELETE CASCADE;


--
-- Name: job_additional_parameters_job_queue_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.job_additional_parameters
    ADD CONSTRAINT job_additional_parameters_job_queue_id_fkey FOREIGN KEY (job_queue_id) REFERENCES public.job_queue(id) ON DELETE CASCADE;


--
-- Name: job_queue_job_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.job_queue
    ADD CONSTRAINT job_queue_job_status_id_fkey FOREIGN KEY (job_status_id) REFERENCES public.job_status(id);


--
-- Name: job_queue_job_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.job_queue
    ADD CONSTRAINT job_queue_job_type_id_fkey FOREIGN KEY (job_type_id) REFERENCES public.job_types(id);


--
-- Name: job_queue_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.job_queue
    ADD CONSTRAINT job_queue_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: logging_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.logging
    ADD CONSTRAINT logging_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id);


--
-- Name: multiuser_document_classification_status_document_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.multiuser_document_classification_status
    ADD CONSTRAINT multiuser_document_classification_status_document_id_fkey FOREIGN KEY (document_id) REFERENCES public.document_text(id);


--
-- Name: multiuser_document_classification_status_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.multiuser_document_classification_status
    ADD CONSTRAINT multiuser_document_classification_status_status_id_fkey FOREIGN KEY (status_id) REFERENCES public.multiuser_document_classification_status_labels(id);


--
-- Name: possible_duplicates_category_text_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.possible_duplicates
    ADD CONSTRAINT possible_duplicates_category_text_id_fkey FOREIGN KEY (category_text_id) REFERENCES public.category_text(id);


--
-- Name: possible_duplicates_source_text_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.possible_duplicates
    ADD CONSTRAINT possible_duplicates_source_text_id_fkey FOREIGN KEY (source_text_id) REFERENCES public.document_text(id);


--
-- Name: projects_final_status_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT projects_final_status_id_fkey FOREIGN KEY (ready_for_review_id) REFERENCES public.multiuser_document_classification_status_labels(id);


--
-- Name: projects_owner_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.projects
    ADD CONSTRAINT projects_owner_user_id_fkey FOREIGN KEY (owner_user_id) REFERENCES public.users(id);


--
-- Name: sorted_index_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.sorted_index
    ADD CONSTRAINT sorted_index_category_id_fkey FOREIGN KEY (category_text_id) REFERENCES public.category_text(id);


--
-- Name: sorted_index_source_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.sorted_index
    ADD CONSTRAINT sorted_index_source_id_fkey FOREIGN KEY (document_text_id) REFERENCES public.document_text(id);


--
-- Name: source_text_to_category_text_id_fk; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.document_text
    ADD CONSTRAINT source_text_to_category_text_id_fk FOREIGN KEY (category_text_id) REFERENCES public.category_text(id);


--
-- Name: text_category_probabilities_history_text_id_to_source_id; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.text_category_probabilities_history
    ADD CONSTRAINT text_category_probabilities_history_text_id_to_source_id FOREIGN KEY (text_id) REFERENCES public.document_text(id);


--
-- Name: text_category_probabilities_text_id_to_source_id; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.text_category_probabilities
    ADD CONSTRAINT text_category_probabilities_text_id_to_source_id FOREIGN KEY (document_text_id) REFERENCES public.document_text(id);


--
-- Name: user_role_assignments_role_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.user_role_assignments
    ADD CONSTRAINT user_role_assignments_role_id_fkey FOREIGN KEY (role_id) REFERENCES public.roles(id);


--
-- Name: user_role_assignments_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.user_role_assignments
    ADD CONSTRAINT user_role_assignments_user_id_fkey FOREIGN KEY (user_id) REFERENCES public.users(id) ON DELETE CASCADE;


--
-- Name: SCHEMA public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

COMMIT;
