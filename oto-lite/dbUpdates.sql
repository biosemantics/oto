/*the light version of OTO*/
drop database charaparser_db;
create database charaparser_db;
use charaparser_db;

create table uploads(
	uploadID  BIGINT not null auto_increment unique,
	uploadTime DATETIME,
	sentToOTO Boolean,
	isFinalized Boolean,
	prefixForOTO varchar(100),
	readyToDelete DATETIME default null,
	primary key (uploadID)
) ENGINE=InnoDB;

create table terms (
	uploadID BIGINT,
	term varchar(50),
	type int
) ENGINE=InnoDB;

create table types (
	typeID int,
	typeName varchar(50),
	primary key (typeID)
) ENGINE=InnoDB;

insert into types(typeID, typeName)
values
(1, 'structures'),
(2, 'characters'),
(3, 'others');

create table categories(
	uploadID BIGINT,
	category varchar(50),
	definition varchar(1000)
) ENGINE=InnoDB;

create table decisions(
	uploadID BIGINT,
	term varchar(50),
	isMainTerm Boolean,
	category varchar(50)
) ENGINE=InnoDB;

create table synonyms(
	uploadID BIGINT,
	mainTerm varchar(50),
	synonym varchar(50),
	category varchar(50)
) ENGINE=InnoDB;

create table sentences (
	uploadID BIGINT,
	sentid BIGINT,
	source varchar(500),
	sentence varchar(2000),
	originalsent varchar(2000)
);

/*sample data*/
insert into uploads (uploadTime, sentToOTO, isFinalized) values 
(now(), false, false);

insert into terms (uploadID, term, type) 
select 1 as uploadID, term, 1 as type 
from markedupdatasets.OTO_Demo_web_grouped_terms;

insert into categories (category, definition)
select category, definition
from markedupdatasets.OTO_Demo_categories;

insert into sentences (uploadID, sentid, source, sentence, originalsent)
select 1 as uploadID, sentid, source, sentence, originalsent 
from markedupdatasets.OTO_Demo_sentence;

drop table if exists typos;
create table typos (
	id BIGINT not null auto_increment unique,
	uploadID int,
	originalTerm varchar(100),
	replacedBy varchar(100),	
	primary key (id)
) ENGINE=InnoDB;

/*change tables to be innodb to support transaction*/
ALTER TABLE categories ENGINE = INNODB;
ALTER TABLE decisions ENGINE = INNODB;
ALTER TABLE synonyms ENGINE = INNODB;
ALTER TABLE terms ENGINE = INNODB;
ALTER TABLE types ENGINE = INNODB;
ALTER TABLE typos ENGINE = INNODB;
ALTER TABLE uploads ENGINE = INNODB;

/*synonyms table should have category*/
alter table synonyms add category varchar(50);

/*update existing data of category field in table synonyms*/
update synonyms a left join 
decisions b 
on a.uploadID = b.uploadID and a.mainTerm = b.term
set a.category = b.category;

/*add secret into table [uploads]*/
alter table uploads add secret varchar(50);
