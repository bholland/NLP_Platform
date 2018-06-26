--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.12
-- Dumped by pg_dump version 9.5.12

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
-- Name: CleanSortedIndex(); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public."CleanSortedIndex"() RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
truncate sorted_index RESTART IDENTITY;
end;
$$;


ALTER FUNCTION public."CleanSortedIndex"() OWNER TO ben;

--
-- Name: clean_all_text(); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.clean_all_text() RETURNS void
    LANGUAGE sql
    AS $$
truncate source_text cascade;
truncate category_text cascade;
$$;


ALTER FUNCTION public.clean_all_text() OWNER TO ben;

--
-- Name: clean_category_sentences(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.clean_category_sentences(in_text_id integer DEFAULT NULL::integer) RETURNS void
    LANGUAGE sql STRICT LEAKPROOF
    AS $$

delete from category_sentences
where text_id = in_text_id;

$$;


ALTER FUNCTION public.clean_category_sentences(in_text_id integer) OWNER TO ben;

--
-- Name: clean_sorted_index(); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.clean_sorted_index() RETURNS void
    LANGUAGE plpgsql
    AS $$
begin
truncate sorted_index RESTART IDENTITY;
end;
$$;


ALTER FUNCTION public.clean_sorted_index() OWNER TO ben;

--
-- Name: clean_source_sentences(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.clean_source_sentences(in_text_id integer DEFAULT NULL::integer) RETURNS void
    LANGUAGE sql STRICT LEAKPROOF
    AS $$
delete from source_sentences
where text_id = in_text_id
$$;


ALTER FUNCTION public.clean_source_sentences(in_text_id integer) OWNER TO ben;

--
-- Name: insert_category_sentence_metadata(integer, integer, character varying, character varying, character varying); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_category_sentence_metadata(in_source_sentence_id integer, in_sentence_index_id integer, in_token character varying, in_pos_tag character varying, in_chunk character varying) RETURNS void
    LANGUAGE sql
    AS $$

insert into category_sentences_metadata
(category_sentence_id, sentence_index, token, pos_tag, chunk)
values
(in_source_sentence_id, in_sentence_index_id, in_token, in_pos_tag, in_chunk);

$$;


ALTER FUNCTION public.insert_category_sentence_metadata(in_source_sentence_id integer, in_sentence_index_id integer, in_token character varying, in_pos_tag character varying, in_chunk character varying) OWNER TO ben;

--
-- Name: insert_category_text(character varying, text); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_category_text(in_category_text_id character varying, in_category_text text) RETURNS integer
    LANGUAGE sql
    AS $$

insert into category_text
(source_text_id, source_text) VALUES
(in_category_text_id, in_category_text)
returning id;

$$;


ALTER FUNCTION public.insert_category_text(in_category_text_id character varying, in_category_text text) OWNER TO ben;

--
-- Name: insert_category_token_count(integer, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_category_token_count(in_processed_text integer, in_num_tokens integer) RETURNS void
    LANGUAGE sql
    AS $$

update category_text
set processed_text=in_processed_text,
processed=TRUE,
num_tokens=in_num_tokens;

$$;


ALTER FUNCTION public.insert_category_token_count(in_processed_text integer, in_num_tokens integer) OWNER TO ben;

--
-- Name: insert_category_token_count(integer, text, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_category_token_count(in_processed_text_id integer, in_processed_text text, in_num_tokens integer) RETURNS void
    LANGUAGE sql
    AS $$

update category_text
set processed_text=in_processed_text,
processed=TRUE,
num_tokens=in_num_tokens
where id = in_processed_text_id;

$$;


ALTER FUNCTION public.insert_category_token_count(in_processed_text_id integer, in_processed_text text, in_num_tokens integer) OWNER TO ben;

--
-- Name: insert_sorted_index(integer, integer[], integer[], double precision[]); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_sorted_index(source_id integer, category_ids integer[], ranks integer[], cos_thetas double precision[]) RETURNS void
    LANGUAGE plpgsql
    AS $$

BEGIN
Insert into sorted_index (source_id, category_id, "index", cos_theta) (
	select source_id, r.*
	from unnest(category_ids, ranks, cos_thetas) as r
	);
END;

$$;


ALTER FUNCTION public.insert_sorted_index(source_id integer, category_ids integer[], ranks integer[], cos_thetas double precision[]) OWNER TO ben;

--
-- Name: FUNCTION insert_sorted_index(source_id integer, category_ids integer[], ranks integer[], cos_thetas double precision[]); Type: COMMENT; Schema: public; Owner: ben
--

COMMENT ON FUNCTION public.insert_sorted_index(source_id integer, category_ids integer[], ranks integer[], cos_thetas double precision[]) IS 'This will insert the rankings of each category. This should probably be limited on the client side to this many to many table doesn''t blow up.  ';


--
-- Name: insert_source_sentence_metadata(integer, integer, character varying, character varying, character varying); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_source_sentence_metadata(in_source_sentence_id integer, in_sentence_index_id integer, in_token character varying, in_pos_tag character varying, in_chunk character varying) RETURNS void
    LANGUAGE sql
    AS $$
insert into source_sentences_metadata
(source_sentence_id, sentence_index, token, pos_tag, chunk)
values
(in_source_sentence_id, in_sentence_index_id, in_token, in_pos_tag, in_chunk);
$$;


ALTER FUNCTION public.insert_source_sentence_metadata(in_source_sentence_id integer, in_sentence_index_id integer, in_token character varying, in_pos_tag character varying, in_chunk character varying) OWNER TO ben;

--
-- Name: insert_source_text(character varying, text); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_source_text(in_source_text_id character varying, in_source_text text) RETURNS integer
    LANGUAGE sql
    AS $$

insert into source_text
(source_text_id, source_text) VALUES
(in_source_text_id, in_source_text)
returning id;

$$;


ALTER FUNCTION public.insert_source_text(in_source_text_id character varying, in_source_text text) OWNER TO ben;

--
-- Name: insert_source_token_count(integer, text, integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.insert_source_token_count(in_processed_text_id integer, in_processed_text text, in_num_tokens integer) RETURNS void
    LANGUAGE sql
    AS $$

update source_text
set processed_text=in_processed_text,
processed=TRUE,
num_tokens=in_num_tokens
where id = in_processed_text_id;

$$;


ALTER FUNCTION public.insert_source_token_count(in_processed_text_id integer, in_processed_text text, in_num_tokens integer) OWNER TO ben;

--
-- Name: select_category_text(); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_category_text() RETURNS TABLE(id integer, category_text text)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin

return QUERY
select category_text.id, category_text.source_text from category_text where processed=FALSE order by category_text.id;

return QUERY
select category_text.id, category_text.processed_text from category_text where processed=TRUE order by category_text.id;

END;

$$;


ALTER FUNCTION public.select_category_text() OWNER TO ben;

--
-- Name: select_category_text_from_id(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_category_text_from_id(in_category_text_id integer) RETURNS TABLE(category_text text)
    LANGUAGE plpgsql
    AS $$

begin

return QUERY
select category_text.source_text 
from category_text 
where processed=FALSE and 
id=in_category_text_id;

return QUERY
select category_text.processed_text 
from category_text 
where processed=TRUE and
id=in_category_text_id;
end;

$$;


ALTER FUNCTION public.select_category_text_from_id(in_category_text_id integer) OWNER TO ben;

--
-- Name: select_source_text(); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_source_text() RETURNS TABLE(id integer, source_text text)
    LANGUAGE plpgsql STRICT LEAKPROOF
    AS $$

begin

return QUERY
select source_text.id, source_text.source_text from source_text where processed=FALSE order by source_text.id;

return QUERY
select source_text.id, source_text.processed_text from source_text where processed=TRUE order by source_text.id;

END;

$$;


ALTER FUNCTION public.select_source_text() OWNER TO ben;

--
-- Name: select_source_text_from_id(integer); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_source_text_from_id(in_source_text_id integer) RETURNS TABLE(source_text text)
    LANGUAGE plpgsql
    AS $$
begin

return QUERY
select source_text.source_text 
from source_text 
where processed=FALSE and 
id=in_source_text_id;

return QUERY
select source_text.processed_text 
from source_text 
where processed=TRUE and
id=in_source_text_id;
end;

$$;


ALTER FUNCTION public.select_source_text_from_id(in_source_text_id integer) OWNER TO ben;

--
-- Name: select_source_text_ids(); Type: FUNCTION; Schema: public; Owner: ben
--

CREATE FUNCTION public.select_source_text_ids() RETURNS TABLE(id integer)
    LANGUAGE sql
    AS $$

select "id"
from source_text;

$$;


ALTER FUNCTION public.select_source_text_ids() OWNER TO ben;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: category_sentences; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.category_sentences (
    id integer NOT NULL,
    text_id integer NOT NULL,
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
    token character varying(25) NOT NULL,
    pos_tag character varying(10) NOT NULL,
    chunk character varying(10) NOT NULL
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
    source_text_id character varying(100),
    source_text text NOT NULL,
    processed_text text,
    processed boolean DEFAULT false NOT NULL,
    num_tokens integer DEFAULT 0 NOT NULL
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
-- Name: sorted_index; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.sorted_index (
    id integer NOT NULL,
    source_id integer NOT NULL,
    category_id integer NOT NULL,
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
-- Name: source_sentences; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.source_sentences (
    id integer NOT NULL,
    text_id integer NOT NULL,
    sentence text NOT NULL,
    sentence_number integer NOT NULL
);


ALTER TABLE public.source_sentences OWNER TO ben;

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

ALTER SEQUENCE public.source_sentences_id_seq OWNED BY public.source_sentences.id;


--
-- Name: source_sentences_metadata; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.source_sentences_metadata (
    id integer NOT NULL,
    source_sentence_id integer NOT NULL,
    sentence_index integer NOT NULL,
    token character varying(25) NOT NULL,
    pos_tag character varying(10) NOT NULL,
    chunk character varying(10) NOT NULL
);


ALTER TABLE public.source_sentences_metadata OWNER TO ben;

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

ALTER SEQUENCE public.source_sentences_metadata_id_seq OWNED BY public.source_sentences_metadata.id;


--
-- Name: source_text; Type: TABLE; Schema: public; Owner: ben
--

CREATE TABLE public.source_text (
    id integer NOT NULL,
    source_text_id character varying(100),
    source_text text NOT NULL,
    processed_text text,
    processed boolean DEFAULT false NOT NULL,
    num_tokens integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.source_text OWNER TO ben;

--
-- Name: TABLE source_text; Type: COMMENT; Schema: public; Owner: ben
--

COMMENT ON TABLE public.source_text IS 'The table containing categorized text. This is optional but is useful when comparing source_text documents to known documents.';


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

ALTER SEQUENCE public.source_text_id_seq OWNED BY public.source_text.id;


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

ALTER TABLE ONLY public.possible_duplicates ALTER COLUMN id SET DEFAULT nextval('public.possible_duplicates_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.sorted_index ALTER COLUMN id SET DEFAULT nextval('public.sorted_index_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.source_sentences ALTER COLUMN id SET DEFAULT nextval('public.source_sentences_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.source_sentences_metadata ALTER COLUMN id SET DEFAULT nextval('public.source_sentences_metadata_id_seq'::regclass);


--
-- Name: id; Type: DEFAULT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.source_text ALTER COLUMN id SET DEFAULT nextval('public.source_text_id_seq'::regclass);


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
-- Name: possible_duplicates_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.possible_duplicates
    ADD CONSTRAINT possible_duplicates_pkey PRIMARY KEY (id);


--
-- Name: sorted_index_id_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.sorted_index
    ADD CONSTRAINT sorted_index_id_pkey PRIMARY KEY (id);


--
-- Name: source_sentences_id_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.source_sentences
    ADD CONSTRAINT source_sentences_id_pkey PRIMARY KEY (id);


--
-- Name: source_sentences_metadata_id_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.source_sentences_metadata
    ADD CONSTRAINT source_sentences_metadata_id_pkey PRIMARY KEY (id);


--
-- Name: source_text_id_pkey; Type: CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.source_text
    ADD CONSTRAINT source_text_id_pkey PRIMARY KEY (id);


--
-- Name: category_sentences_metadata_category_sentence_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.category_sentences_metadata
    ADD CONSTRAINT category_sentences_metadata_category_sentence_id_fkey FOREIGN KEY (category_sentence_id) REFERENCES public.category_sentences(id);


--
-- Name: category_sentences_text_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.category_sentences
    ADD CONSTRAINT category_sentences_text_id_fkey FOREIGN KEY (text_id) REFERENCES public.category_text(id) ON UPDATE CASCADE ON DELETE CASCADE;


--
-- Name: possible_duplicates_category_text_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.possible_duplicates
    ADD CONSTRAINT possible_duplicates_category_text_id_fkey FOREIGN KEY (category_text_id) REFERENCES public.category_text(id);


--
-- Name: possible_duplicates_source_text_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.possible_duplicates
    ADD CONSTRAINT possible_duplicates_source_text_id_fkey FOREIGN KEY (source_text_id) REFERENCES public.source_text(id);


--
-- Name: sorted_index_category_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.sorted_index
    ADD CONSTRAINT sorted_index_category_id_fkey FOREIGN KEY (category_id) REFERENCES public.category_text(id);


--
-- Name: sorted_index_source_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.sorted_index
    ADD CONSTRAINT sorted_index_source_id_fkey FOREIGN KEY (source_id) REFERENCES public.source_text(id);


--
-- Name: source_sentences_metadata_source_sentence_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.source_sentences_metadata
    ADD CONSTRAINT source_sentences_metadata_source_sentence_id_fkey FOREIGN KEY (source_sentence_id) REFERENCES public.source_sentences(id);


--
-- Name: source_sentences_text_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: ben
--

ALTER TABLE ONLY public.source_sentences
    ADD CONSTRAINT source_sentences_text_id_fkey FOREIGN KEY (text_id) REFERENCES public.source_text(id) ON UPDATE CASCADE ON DELETE CASCADE;


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

