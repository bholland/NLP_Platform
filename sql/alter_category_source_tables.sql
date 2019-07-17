-- Table: public.source_text
rollback;
BEGIN;
ALTER TABLE public.possible_duplicates DROP CONSTRAINT possible_duplicates_source_text_id_fkey;
ALTER TABLE public.possible_duplicates DROP CONSTRAINT possible_duplicates_category_text_id_fkey;
ALTER TABLE public.sorted_index DROP CONSTRAINT sorted_index_category_id_fkey;
ALTER TABLE public.sorted_index DROP CONSTRAINT sorted_index_source_id_fkey;
ALTER TABLE public.source_sentences DROP CONSTRAINT source_sentences_text_id_fkey;
ALTER TABLE public.category_sentences DROP CONSTRAINT category_sentences_text_id_fkey;

DROP TABLE public.source_text;
DROP TABLE public.category_text;

CREATE TABLE public.source_text
(
    id SERIAL NOT NULL ,
    source_text_id varchar(100),
    source_text text COLLATE pg_catalog."default" NOT NULL,
    processed_text text COLLATE pg_catalog."default",
    processed boolean NOT NULL DEFAULT false,
    num_tokens integer NOT NULL DEFAULT 0,
    CONSTRAINT source_text_id_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.source_text
    OWNER to ben;
COMMENT ON TABLE public.source_text
    IS 'The table containing categorized text. This is optional but is useful when comparing source_text documents to known documents.';


CREATE TABLE public.category_text
(
    id SERIAL NOT NULL,
    source_text_id varchar(100),
    source_text text COLLATE pg_catalog."default" NOT NULL,
    processed_text text COLLATE pg_catalog."default",
    processed boolean NOT NULL DEFAULT false,
    num_tokens integer NOT NULL DEFAULT 0,
    CONSTRAINT category_text_id_pkey PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
)
TABLESPACE pg_default;

ALTER TABLE public.category_text
    OWNER to ben;
COMMENT ON TABLE public.category_text
    IS 'The source_text_id and source_text might be a bit misleading. Perhaps change them to org_text_id and org_text. These columns should come from the data source as a link to a document. For example, if a csv file has a column called "id" then the source_text_id column would contain the corresponding values in the "id" column. Documents going into this table are the "category" documents (i.e., known documents that represent a category). ';

ALTER TABLE public.possible_duplicates
    ADD CONSTRAINT possible_duplicates_source_text_id_fkey FOREIGN KEY (source_text_id)
    REFERENCES public.source_text (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;
	
ALTER TABLE public.possible_duplicates
    ADD CONSTRAINT possible_duplicates_category_text_id_fkey FOREIGN KEY (category_text_id)
    REFERENCES public.category_text (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;
	
ALTER TABLE public.sorted_index
    ADD CONSTRAINT sorted_index_category_id_fkey FOREIGN KEY (category_id)
    REFERENCES public.category_text (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

ALTER TABLE public.sorted_index
    ADD CONSTRAINT sorted_index_source_id_fkey FOREIGN KEY (source_id)
    REFERENCES public.source_text (id) MATCH SIMPLE
    ON UPDATE NO ACTION
    ON DELETE NO ACTION;

ALTER TABLE public.source_sentences
    ADD CONSTRAINT source_sentences_text_id_fkey FOREIGN KEY (text_id)
    REFERENCES public.source_text (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE;

ALTER TABLE public.category_sentences
    ADD CONSTRAINT category_sentences_text_id_fkey FOREIGN KEY (text_id)
    REFERENCES public.category_text (id) MATCH SIMPLE
    ON UPDATE CASCADE
    ON DELETE CASCADE;
COMMIT;


