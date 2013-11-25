# --- First database schema

# --- !Ups

set ignorecase true;

create table url (
  id                        varchar(20)  not null,
  ref                      varchar(1024) not null,
  constraint pk_company primary key (id))
;

# --- !Downs

SET REFERENTIAL_INTEGRITY FALSE;

drop table if exists url;

SET REFERENTIAL_INTEGRITY TRUE;

