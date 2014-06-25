/**
 * Date. Aug. 8th 2011
 * Author: Fengqiong
 */

/*about terms relations*/
alter table fna_v19_user_terms_decisions add isAdditional Boolean default false;
alter table fna_v19_user_terms_decisions add relatedTerms varchar(500);
drop table fna_v19_user_terms_relations;
create table fna_v19_user_terms_relations(
	term1 varchar(100),
	term2 varchar(100),
	relation integer,
	decision varchar(100),
	userid BIGINT
);

/*tables of hierarchy page*/
drop table fna_gloss_web_tags;
create table fna_gloss_web_tags(
	tagID BIGINT not null auto_increment unique,
	tagName varchar(100),
	primary key (tagID)
);
/*insert the default records*/
insert into fna_gloss_web_tags(tagID, tagName) 
values
(1, 'Plant'), (2, 'Root'), (3, 'Stem'), (4, 'Leaf'), (5, 'Fruit'), (6, 'Seed'), (7, 'Flower');

/*table to store the decisions of hierarchy tree*/
drop table  fna_gloss_user_tags_decisions;
create table fna_gloss_user_tags_decisions(
	ID  BIGINT not null auto_increment unique,
	tagID BIGINT,
	name varchar(100),
	tagPID BIGINT,
	path varchar(200),
	pathWithName varchar(500),
	removed Boolean,
	isLeaf Boolean,
	userid BIGINT,
	decisionDate DATETIME,
	primary key (ID)
);

/*insert default records in the tree*/
insert into fna_gloss_user_tags_decisions(tagID, tagPID, isLeaf, name, userid, path, pathWithName)
values
(1, 0, false, 'Plant', 0, '1', 'Plant'),
(2, 1, false, 'Root', 0, '1_2', 'Plant-Root'),
(3, 1, false, 'Stem', 0, '1_3', 'Plant-Stem'),
(4, 1, false, 'Leaf', 0, '1_4', 'Plant-Leaf'),
(5, 1, false, 'Fruit', 0, '1_5', 'Plant-Fruit'),
(6, 1, false, 'Seed', 0, '1_6', 'Plant-Seed'),
(7, 1, false, 'Flower', 0, '1_7', 'Plant-Flower')
;

/*tables for the terms order page*/
drop table  fna_gloss_web_orders;
create table fna_gloss_web_orders(
	id  BIGINT not null auto_increment unique,
	name varchar(100),
	isBase boolean default false,
	base BIGINT,
	explanation varchar(500),
	primary key (id)
);
/*sample records from demo*/
insert into fna_gloss_web_orders(id, name, isBase, base, explanation) 
values
(1, 'Pubescence', true, null, ''),
(2, 'Pubescence-Density Order', false, 1, 'explanation of Pubescence-Density Order'),
(3, 'Shape', true, null, ''),
(4, 'Shape Order', false, 3, 'explanation of Shape Order'),
(5, 'Orientation', true, null, ''),
(6, 'Orientation Order', false, 5, 'explanation of Orientation Order')
;

drop table fna_gloss_web_orders_terms;
create table fna_gloss_web_orders_terms(
	id BIGINT not null auto_increment unique,
	orderID BIGINT not null,
	name varchar(100),
	isBase boolean default false,
	primary key (id)
);
/*samples records from demo*/
insert into fna_gloss_web_orders_terms(orderID, name, isBase)
values
(1, 'papillate', false),
(1, 'hirsute', false),
(1, 'glabrous', true),
(1, 'hairy', false),
(1, 'bald', false),
(1, 'balding', false),
(1, 'barbate', false),
(1, 'bearded', false),
(1, 'bristly', false),
(2, 'glabrous', true),
(3, 'cylindric', false),
(3, 'ovoid', false),
(3, 'hemispheric', false),
(3, 'flat', true),
(3, 'convex', false),
(3, 'conic', false),
(3, 'columnar', false),
(3, 'ovate', false),
(3, 'lanceolate', false),
(3, 'linear', false),
(4, 'flat', true),
(5, 'erect', false),
(5, 'prostrate', false),
(5, 'ascending', false),
(5, 'spreading', false),
(5, 'reflexed', false),
(5, 'appressed', false),
(5, 'deflexed', false),
(6, 'erect', true);

drop table fna_v19_user_orders_decisions;
create table fna_v19_user_orders_decisions(
	userID BIGINT,
	orderID BIGINT,
	termName varchar(100),
	decisionDate DATETIME,
	distance integer,
	isTerm boolean default true,
	decision varchar(500)
);

/*changes of comments table: integrate comments from hierarchy tree and order pages*/
alter table fna_v19_comments add tagID integer;
alter table fna_v19_comments add orderID integer;

/*2012-1-14 new table: categories*/
drop table categories;
create table categories (
category varchar(50),
definition varchar(1000),
primary key (category)
);
INSERT INTO categories (category, definition) 
VALUES ('internal texture', 'Internal elements.'),
('arrangement', 'Disposition of structures with respect to one another within some explicit or implicit standard context.'),
('origin','Ontogenetic origin.'),
('coloration','Hue(s), intensity(ies), and/or pattern (if any) of coloring.'),
('count','Number of entities.'),
('course','course	Linear pattern of the centerline through the length of an axis or vein.'),
('dehiscence','Mode of opening (splitting or forming apertures).'),
('depth','Distance from the top or surface of an entity to its bottom.'),
('derivation','');

INSERT INTO categories (category, definition) 
VALUES
('development','Mode or pattern of growth and differentiation.'),
('duration','Extent of lifetime, or persistence and physical state after maturation.'),
('exudation','Discharged substance.'),
('fixation','Mode of attachment to a supporting structure.'),
('fragility','Resistance to being damaged or destroyed.'),
('fusion','Physical connection of equivalent or dissimilar structural entities.'),
('germination','The process wherein a dormant embryo or spore resumes active growth.'),
('habit','General appearance and/or function, usually including explicit or implicit reference to one or more aspects of habitat or other features of the external environment.'),
('height','Distance from the base of an entity to the top.'),
('length','Extent of an entity from end to end.'),
('life_stage',''),
('sculpture','General topographic aspect of a surface. Overlaps conceptually with solid shape.'),
('thickness','The distance between opposite sides of an entity.'),
('life_style','');

INSERT INTO categories (category, definition) 
VALUES
('location','Position with respect to aspects of environmental context.'),
('maturation','Timing of the attainment of functional maturity, sometimes relative to other structures, sometimes as to constituent structures relative to each other.'),
('external texture','Surface elements including pubescence and coatings.'),
('nutrition','Mode of acquiring nutrients.'),
('odor','Olfactory stimulation.'),
('orientation','Attitude or direction with respect to some explicit or implicit structure(s) or context.'),
('pattern','Exhibiting repetition of placement of its parts.'),
('position','Disposition of a structure with reference to some dissimilar structure(s) or larger context.'),
('prominence','Degree or nature of evidence when present within the context in point.'),
('volume','The amount per unit volume.');

INSERT INTO categories (category, definition) 
VALUES
('reflectance','Aspect as to proportion and pattern of incident light reflected from the surface.'),
('relief','General topographic aspect of a surface. '),
('reproduction','Type, morphology, disposition, function and/or dissemination of reproductive structures.'),
('season',''),
('shape','Overall two- or three-dimensional form or aspect(s) thereof.'),
('size','Nature as to absolute or comparative extent in any one dimension or in area or volume.'),
('taste','The sensation of flavor perceived in the mouth and throat on contact with a substance.'),
('density','The amount per unit area.'),
('width',''),
('vernation','Disposition of leaves in the bud.'),
('variability','An entity�s disposition to vary or change.'),
('venation','Configuration of laminar vasculature as to its hierarchical organization and the dispositions of component orders.'),
('structure','Organization of the organ�s various parts and the pattern underlying its form.');

/*2012-01-14 Fengqiong*/
alter table fna_gloss_user_terms_decisions add isActive Boolean default true;
alter table fna_gloss_user_terms_decisions add isLatest Boolean default true;
alter table fna_gloss_user_terms_decisions add hasConflict Boolean default false;
alter table fna_gloss_user_terms_decisions add hasSyn Boolean default false;
/*2012-01-25 Fengqiong: decisionid need to be primary key*/
alter table fna_gloss_user_terms_decisions drop decisionid;
alter table fna_gloss_user_terms_decisions add decisionid BIGINT not null auto_increment unique;
/*2012-01-23 Fengqiong: groupid not needed*/
alter table fna_gloss_user_terms_decisions drop groupid;
alter table fna_gloss_user_terms_decisions add groupid INT;
/*2012-01-26 Fengqiong: terms belongs to special 6 categories in fnaglossary table should not be shown*/
delete from fna_gloss_web_grouped_terms where term in 
(select term from fnaglossary where category in 
('character', 'structure', 'feature', 'substance', 'plant', 'nominative'));
/*2012-01-31 Fengqiong: James' update*/
delete from categories where category = 'thickness';
delete from categories where category = 'life_stage';
delete from categories where category = 'season';
update categories set definition = 'Extent of an entity from side to side.' where category = 'width';
update categories set definition = 'Ontogenetic origin.' where category = 'derivation';

/*2012-02-01 Fengqiong: order*/
alter table fna_gloss_user_orders_decisions drop decision;
alter table fna_gloss_user_orders_decisions add decision varchar(2000);
alter table fna_gloss_user_orders_decisions add isActive Boolean default true;
alter table fna_gloss_user_orders_decisions add isLatest Boolean default true;
alter table fna_gloss_user_orders_decisions add hasConflict Boolean default false;
alter table fna_gloss_user_orders_decisions add isBase Boolean default false;
/*re create order tables on server*/
/*re create tags tables on server*/

alter table fna_gloss_user_tags_decisions add hasConflict Boolean default false;


/*Fengqiong 20120525 each dataset has its own category*/
create table fna_gloss_categories like categories;
insert fna_gloss_categories select * from categories;

create table OTO_Demo_categories like categories;
insert OTO_Demo_categories select * from categories;

/*Fengqiong 20120701: add review history table*/
drop table OTO_Demo_review_history;
create table OTO_Demo_review_history (
id BIGINT not null auto_increment unique,
userid int,
term varchar(100),
reviewTime DATETIME,
primary key (id)
);

/*get saved decisions to review history*/
insert OTO_Demo_review_history (userid, term, reviewTime) select distinct userid, term, decisiondate from OTO_Demo_user_terms_decisions group by userid, term;

drop table fna_gloss_review_history;
create table fna_gloss_review_history (
id BIGINT not null auto_increment unique,
userid int,
term varchar(100),
reviewTime DATETIME,
primary key (id)
);

/*get saved decisions to review history*/
insert fna_gloss_review_history (userid, term, reviewTime) select distinct userid, term, decisiondate from fna_gloss_user_terms_decisions group by userid, term;

drop table treatise_review_history;
create table treatise_review_history (
id BIGINT not null auto_increment unique,
userid int,
term varchar(100),
reviewTime DATETIME,
primary key (id)
);

/*get saved decisions to review history*/
insert treatise_review_history (userid, term, reviewTime) select distinct userid, term, decisiondate from treatise_user_terms_decisions group by userid, term;

/*separate confirmed tables for each dataset*/
/*confirmed decisions of categoring terms*/
drop table if exists confirmed_category;
create table fna_gloss_confirmed_category (
	term varchar(100),
	category varchar(200),
	accepted boolean,
	userid integer,  /*who did the confirmation*/
	confirmDate DATETIME
);

/*confirmed decisions of hierarchy terms*/
drop table if exists confirmed_paths;
create table fna_gloss_confirmed_paths (
	term varchar(100),
	path varchar(200),
	pathWithName varchar(500),
	accepted boolean,
	userid integer,  /*who did the confirmation*/
	confirmDate DATETIME
);

/*confirmed decisions of orders*/
drop table if exists confirmed_orders;
create table fna_gloss_confirmed_orders (
	orderID integer,
	orderName varchar(100),
	term varchar(100),
	distance integer,
	accepted boolean,
	userid integer, 
	confirmDate DATETIME
);

drop table if exists OTO_Demo_confirmed_category;
create table OTO_Demo_confirmed_category like fna_gloss_confirmed_category;
drop table if exists OTO_Demo_confirmed_paths;
create table OTO_Demo_confirmed_paths like fna_gloss_confirmed_paths;
drop table if exists OTO_Demo_confirmed_orders;
create table OTO_Demo_confirmed_orders like fna_gloss_confirmed_orders;

drop table if exists fna_v19_confirmed_category;
create table fna_v19_confirmed_category like fna_gloss_confirmed_category;
drop table if exists fna_v19_confirmed_paths;
create table fna_v19_confirmed_paths like fna_gloss_confirmed_paths;
drop table if exists fna_v19_confirmed_orders;
create table fna_v19_confirmed_orders like fna_gloss_confirmed_orders;

drop table if exists treatise_confirmed_category;
create table treatise_confirmed_category like fna_gloss_confirmed_category;
drop table if exists treatise_confirmed_paths;
create table treatise_confirmed_paths like fna_gloss_confirmed_paths;
drop table if exists treatise_confirmed_orders;
create table treatise_confirmed_orders like fna_gloss_confirmed_orders;

drop table if exists treatise_o_confirmed_category;
create table treatise_o_confirmed_category like fna_gloss_confirmed_category;
drop table if exists treatise_o_confirmed_paths;
create table treatise_o_confirmed_paths like fna_gloss_confirmed_paths;
drop table if exists treatise_o_confirmed_orders;
create table treatise_o_confirmed_orders like fna_gloss_confirmed_orders;

drop table if exists fnav19_excerpt_2012_10_19_confirmed_category;
create table fnav19_excerpt_2012_10_19_confirmed_category like fna_gloss_confirmed_category;
drop table if exists fnav19_excerpt_2012_10_19_confirmed_paths;
create table fnav19_excerpt_2012_10_19_confirmed_paths like fna_gloss_confirmed_paths;
drop table if exists fnav19_excerpt_2012_10_19_confirmed_orders;
create table fnav19_excerpt_2012_10_19_confirmed_orders like fna_gloss_confirmed_orders;

drop table if exists foroto_fnav4_2012_10_25_confirmed_category;
create table foroto_fnav4_2012_10_25_confirmed_category like fna_gloss_confirmed_category;
drop table if exists foroto_fnav4_2012_10_25_confirmed_paths;
create table foroto_fnav4_2012_10_25_confirmed_paths like fna_gloss_confirmed_paths;
drop table if exists foroto_fnav4_2012_10_25_confirmed_orders;
create table foroto_fnav4_2012_10_25_confirmed_orders like fna_gloss_confirmed_orders;

/*remove finalized tables*/
drop table if exists  OTO_Demo_finalized_terms;
drop table  if exists OTO_Demo_finalized_tags;
drop table  if exists OTO_Demo_finalized_orders;

drop table  if exists fna_gloss_finalized_terms;
drop table  if exists fna_gloss_finalized_tags;
drop table if exists  fna_gloss_finalized_orders;

drop table  if exists fna_v19_finalized_terms;
drop table  if exists fna_v19_finalized_tags;
drop table  if exists fna_v19_finalized_orders;

drop table if exists  treatise_finalized_terms;
drop table  if exists treatise_finalized_tags;
drop table  if exists treatise_finalized_orders;

drop table if exists  treatise_o_finalized_terms;
drop table  if exists treatise_o_finalized_tags;
drop table  if exists treatise_o_finalized_orders;

drop table if exists  foroto_fnav4_2012_10_25_finalized_terms;
drop table  if exists foroto_fnav4_2012_10_25_finalized_tags;
drop table  if exists foroto_fnav4_2012_10_25_finalized_orders;

drop table if exists  fnav19_excerpt_2012_10_19_finalized_terms;
drop table  if exists fnav19_excerpt_2012_10_19_finalized_tags;
drop table  if exists fnav19_excerpt_2012_10_19_finalized_orders;

/*fix incorrect data in fna_gloss*/
/*double check first*/
select * from fna_gloss_user_terms_decisions where term like "\_%" or relatedTerms like "%\_%"; 

delete from fna_gloss_user_terms_decisions where term = "_3";

update fna_gloss_user_terms_decisions 
set relatedTerms = "'_verticillate','_whorled'" 
where term = "_cyclic" and relatedTerms = "'_verticillate','_whorled','_2'";

delete from fna_gloss_user_terms_decisions where term = "_2";

update fna_gloss_user_terms_decisions set relatedTerms = "" 
where term = "_gonal" and relatedTerms = "'_1'";

delete from fna_gloss_user_terms_decisions where term = "_1";

/*fix bug from last db update*/
update fna_gloss_user_terms_decisions set hasSyn = false 
where hasSyn = true and relatedTerms = "";

/*add flags of confirmation finished*/
alter table datasetprefix add grouptermsdownloadable Boolean default false;
alter table datasetprefix add structurehierarchydownloadable Boolean default false;
alter table datasetprefix add termorderdownloadable Boolean default false;

/*3-12-2013*/
/*dataset related admin*/
drop table if exists dataset_owner;
create table dataset_owner (
	dataset varchar(100),
	ownerID integer
);

alter table datasetprefix add note varchar(2000);
alter table datasetprefix modify note TEXT;

/*get current data*/
/*admin has access to manage all datasets,
 * user has access to datasets they own*/
/*
 * 
insert into dataset_owner (dataset, adminID) 
select 'OTO_Demo', userid 
from users
where email = 'ewtwetwetwe@gewtwe.wetwe'
 * 
 * */

update users set password = '[B@1fff8c2a' where password = 'Huang1023';
update users set password = 'Huang1023' where password = '[B@1fff8c2a';

/*let structures out*/
insert into fnav2_2013_02_05_categories (category, definition) values ('structure', '');

/*ontology look up part*/
alter table users add bioportalUserId varchar(20) default '';
alter table users add bioportalApiKey varchar(80) default '';

/*The table that holds all the bioportal subissions */
drop table bioportal_adoption;
CREATE TABLE IF NOT EXISTS bioportal_adoption (
	localId BIGINT not null auto_increment unique,
	term varchar(100) not null, 
	temporaryId varchar(100) NOT NULL,
	permanentId varchar(100) DEFAULT NULL,
	superClass varchar(100) DEFAULT NULL,
	submittedBy varchar(100) DEFAULT NULL,
	definition text,
	ontologyIds varchar(100) DEFAULT NULL,
	preferredName varchar(100) DEFAULT NULL,
	synonyms varchar(100) DEFAULT NULL,
	source text DEFAULT NULL,
	termType varchar(100) DEFAULT NULL,
	termCategory varchar(100) DEFAULT NULL,
	dataset varchar(100) default null,
	glossaryType int not null,
	primary key (localID)
);

/*The table that holds all the removed terms that may not need for submission */
create table if not exists bioportal_removedTerms (
	term varchar(100) not null,
	glossaryID int not null,
	primary key (term, glossaryID)
);

/*The table that holds all the candidate glossaries*/
create table if not exists glossarytypes (
	glossTypeID int not null auto_increment unique,
	glossaryName varchar(100) not null,
	primary key (glossTypeID)
);

insert into glossarytypes (glossTypeID, glossaryName) values 
(1, 'Plant'),
(2, 'Hymenoptera'),
(3, 'Algea'),
(4, 'Porifera'),
(5, 'Fossil');

alter table datasetprefix add glossaryType int default 1;
alter table datasetprefix modify glossaryType int default 1;

drop table if exists bioportal_deleted_submissions;
create table bioportal_deleted_submissions like bioportal_adoption;
alter table bioportal_deleted_submissions add deletedBy int not null;
alter table bioportal_deleted_submissions add deleteTime DATETIME;
alter table bioportal_deleted_submissions drop localId;
alter table bioportal_deleted_submissions add localId BIGINT not null;


update datasetprefix set glossaryType = 1 where prefix like "%fna%";
update datasetprefix set glossaryType = 1 where prefix like "%foc%";
update datasetprefix set glossaryType = 1 where prefix like "%demo%";
update datasetprefix set glossaryType = 5 where prefix like "part%";
update datasetprefix set glossaryType = 2 where prefix like "%ant%";
update datasetprefix set glossaryType = 3 where prefix like "%diatom%";
update datasetprefix set glossaryType = 4 where prefix like "%sponge%";
update datasetprefix set glossaryType = 5 where prefix like "%treatise%";
update datasetprefix set glossaryType = 1 where glossaryType is null;


show tables like "%web_grouped_terms%"; 
/*54 records*/
/*add source field for _web_grouped_terms tables*/
alter table OTO_Demo_web_grouped_terms add sourceDataset text;
alter table ant_agosti_2012_12_06_web_grouped_terms add sourceDataset text;
alter table ant_gloss_20130517080844_web_grouped_terms add sourceDataset text;
alter table diatom_test_2012_12_19_web_grouped_terms add sourceDataset text;
alter table fna2_jing_20130220160152_web_grouped_terms add sourceDataset text;
alter table fna2_jing_20130221132244_web_grouped_terms add sourceDataset text;
alter table fna2_jing_20130221171529_web_grouped_terms add sourceDataset text;
alter table fna2_v19_jing_20130227141308_web_grouped_terms add sourceDataset text;
alter table fna2_v20_jing_20130228082452_web_grouped_terms add sourceDataset text;
alter table fna2_v21_jing_20130228140000_web_grouped_terms add sourceDataset text;
alter table fna2_v22_jing_20130301080941_web_grouped_terms add sourceDataset text;
alter table fna2_v23_jing_20130301113018_web_grouped_terms add sourceDataset text;
alter table fna2_v26_jing_20130314134608_web_grouped_terms add sourceDataset text;
alter table fna2_v27_jing_20130314153702_web_grouped_terms add sourceDataset text;
alter table fna2_v2_jing_20130402084106_web_grouped_terms add sourceDataset text;
alter table fna2_v3_jing_20130314110323_web_grouped_terms add sourceDataset text;
alter table fna2_v4_jing_20130225160038_web_grouped_terms add sourceDataset text;
alter table fna2_v5_jing_20130226072402_web_grouped_terms add sourceDataset text;
alter table fna2_v7_jing_20130226143722_web_grouped_terms add sourceDataset text;
alter table fna2_v8_jing_20130227093016_web_grouped_terms add sourceDataset text;
alter table fnagloss_fromHong_20130517125327_web_grouped_terms add sourceDataset text;
alter table fnav2_2013_02_05_web_grouped_terms add sourceDataset text;
alter table fnav2_jing_20130212083354_web_grouped_terms add sourceDataset text;
alter table fnav2_jing_20130213204937_web_grouped_terms add sourceDataset text;
alter table fnav5_cui_2012_12_06_web_grouped_terms add sourceDataset text;
alter table fnav5_jing_2013_01_23_web_grouped_terms add sourceDataset text;
alter table fnav5_test_20130206182317_web_grouped_terms add sourceDataset text;
alter table fnav8_sonali_2012_12_07_web_grouped_terms add sourceDataset text;
alter table fna_gloss_web_grouped_terms add sourceDataset text;
alter table fna_gloss_final_20130517_web_grouped_terms add sourceDataset text;
alter table fna_jing_merge_web_grouped_terms add sourceDataset text;
alter table foc_v10_jing_20130409171802_web_grouped_terms add sourceDataset text;
alter table foc_v11_jing_20130502111907_web_grouped_terms add sourceDataset text;
alter table foc_v12_jing_20130502141840_web_grouped_terms add sourceDataset text;
alter table foc_v13_jing_20130503115239_web_grouped_terms add sourceDataset text;
alter table foc_v14_jing_20130506091135_web_grouped_terms add sourceDataset text;
alter table foc_v2021_jing_20130507150953_web_grouped_terms add sourceDataset text;
alter table foc_v23_jing_20130507134321_web_grouped_terms add sourceDataset text;
alter table foc_v25_jing_20130508081633_web_grouped_terms add sourceDataset text;
alter table foc_v4_jing_20130416123440_web_grouped_terms add sourceDataset text;
alter table foc_v5_jing_20130425141012_web_grouped_terms add sourceDataset text;
alter table foc_v6_jing_20130515110950_web_grouped_terms add sourceDataset text;
alter table foc_v7_jing_20130426135157_web_grouped_terms add sourceDataset text;
alter table foc_v8_jing_20130430091947_web_grouped_terms add sourceDataset text;
alter table foc_v8_jing_20130430113241_web_grouped_terms add sourceDataset text;
alter table foc_v9_jing_20130501080458_web_grouped_terms add sourceDataset text;
alter table foroto_fnav4_2012_10_25_web_grouped_terms add sourceDataset text;
alter table parthv2_2012_11_09_web_grouped_terms add sourceDataset text;
alter table plant_gloss_for_iplant_web_grouped_terms add sourceDataset text;
alter table sponges_1_20130425142742_web_grouped_terms add sourceDataset text;
alter table test_20130207094242_web_grouped_terms add sourceDataset text;
alter table treatise_web_grouped_terms add sourceDataset text;
alter table treatise_gloss_web_grouped_terms add sourceDataset text;
alter table treatise_o_web_grouped_terms add sourceDataset text;

/*add source field for _term_category tables*/
alter table OTO_Demo_term_category add sourceDataset text;
alter table ant_agosti_2012_12_06_term_category add sourceDataset text;
alter table ant_gloss_20130517080844_term_category add sourceDataset text;
alter table diatom_test_2012_12_19_term_category add sourceDataset text;
alter table fna2_jing_20130220160152_term_category add sourceDataset text;
alter table fna2_jing_20130221132244_term_category add sourceDataset text;
alter table fna2_jing_20130221171529_term_category add sourceDataset text;
alter table fna2_v19_jing_20130227141308_term_category add sourceDataset text;
alter table fna2_v20_jing_20130228082452_term_category add sourceDataset text;
alter table fna2_v21_jing_20130228140000_term_category add sourceDataset text;
alter table fna2_v22_jing_20130301080941_term_category add sourceDataset text;
alter table fna2_v23_jing_20130301113018_term_category add sourceDataset text;
alter table fna2_v26_jing_20130314134608_term_category add sourceDataset text;
alter table fna2_v27_jing_20130314153702_term_category add sourceDataset text;
alter table fna2_v2_jing_20130402084106_term_category add sourceDataset text;
alter table fna2_v3_jing_20130314110323_term_category add sourceDataset text;
alter table fna2_v4_jing_20130225160038_term_category add sourceDataset text;
alter table fna2_v5_jing_20130226072402_term_category add sourceDataset text;
alter table fna2_v7_jing_20130226143722_term_category add sourceDataset text;
alter table fna2_v8_jing_20130227093016_term_category add sourceDataset text;
alter table fnagloss_fromHong_20130517125327_term_category add sourceDataset text;
alter table fnav2_2013_02_05_term_category add sourceDataset text;
alter table fnav2_jing_20130212083354_term_category add sourceDataset text;
alter table fnav2_jing_20130213204937_term_category add sourceDataset text;
alter table fnav5_cui_2012_12_06_term_category add sourceDataset text;
alter table fnav5_jing_2013_01_23_term_category add sourceDataset text;
alter table fnav5_test_20130206182317_term_category add sourceDataset text;
alter table fnav8_sonali_2012_12_07_term_category add sourceDataset text;
alter table fna_gloss_term_category add sourceDataset text;
alter table fna_gloss_final_20130517_term_category add sourceDataset text;
alter table fna_jing_merge_term_category add sourceDataset text;
alter table foc_v10_jing_20130409171802_term_category add sourceDataset text;
alter table foc_v11_jing_20130502111907_term_category add sourceDataset text;
alter table foc_v12_jing_20130502141840_term_category add sourceDataset text;
alter table foc_v13_jing_20130503115239_term_category add sourceDataset text;
alter table foc_v14_jing_20130506091135_term_category add sourceDataset text;
alter table foc_v2021_jing_20130507150953_term_category add sourceDataset text;
alter table foc_v23_jing_20130507134321_term_category add sourceDataset text;
alter table foc_v25_jing_20130508081633_term_category add sourceDataset text;
alter table foc_v4_jing_20130416123440_term_category add sourceDataset text;
alter table foc_v5_jing_20130425141012_term_category add sourceDataset text;
alter table foc_v6_jing_20130515110950_term_category add sourceDataset text;
alter table foc_v7_jing_20130426135157_term_category add sourceDataset text;
alter table foc_v8_jing_20130430091947_term_category add sourceDataset text;
alter table foc_v8_jing_20130430113241_term_category add sourceDataset text;
alter table foc_v9_jing_20130501080458_term_category add sourceDataset text;
alter table foroto_fnav4_2012_10_25_term_category add sourceDataset text;
alter table parthv2_2012_11_09_term_category add sourceDataset text;
alter table plant_gloss_for_iplant_term_category add sourceDataset text;
alter table sponges_1_20130425142742_term_category add sourceDataset text;
alter table test_20130207094242_term_category add sourceDataset text;
alter table treatise_term_category add sourceDataset text;
alter table treatise_gloss_term_category add sourceDataset text;
alter table treatise_o_term_category add sourceDataset text;

/*user log: upload, merge, delete, display in the user report page*/
drop table if exists users_log;
create table  if not exists users_log(
	userid int,
	operation varchar(100) default "",
	dataset varchar(100) default "",
	operateTime DATETIME
);

alter table datasetprefix add mergedInto varchar(200) default null;


/*this update execute from here */
/*glossary versions*/
drop table if exists glossary_versions;
create table if not exists glossary_versions (
	dataset varchar(100) not null,
	glossaryType int not null,
	filename varchar(300) not null,
	primaryVersion int not null,
	secondaryVersion int not null,
	svnLink varchar(300),
	isLatest boolean,
	isForGlossaryDownload boolean,
	dateCreated datetime
);

/*download preference: e.g. iplant prefer to download fna_gloss_for_iplant
 * charaparser may prefer to download the largest system reserved dataset*/
drop table if exists download_preference;
create table if not exists download_preference(
	glossaryType int not null,
	dataset varchar(100) not null,
	preferredBy varchar(50) not null
);

/*glossary dictionary*/
drop table if exists glossary_dictionary;
create table if not exists glossary_dictionary (
	termID varchar(100) not null default "",
	term varchar(100) not null,
	category varchar(100),
	glossaryType int not null,
	definition text,
	primary key (termID)
);

CREATE TRIGGER glossary_dict_before_insert_uuid
  BEFORE INSERT ON glossary_dictionary 
  FOR EACH ROW
  SET new.termID = uuid();
  
drop trigger glossary_dict_before_insert_uuid;

/*load in default data*/
LOAD DATA INFILE 'D:\\Work\\glossary_dictionary_reviewed.csv' 
INTO TABLE glossary_dictionary 
FIELDS TERMINATED BY ',' ENCLOSED BY '"' 
LINES TERMINATED BY '\r\n' 
IGNORE 1 LINES;

/*modify category: one term - one category*/
update glossary_dictionary d 
left join 
(select term, category from (
select term, count(category) as c, category from fna_gloss_term_category 
group by term) a 
where c = 1) b 
on d.term = b.term
set d.category = b.category; 
  
/*for all term_category, add staticID varchar(100)*/

/*confirmed by - string*/
alter table OTO_Demo_confirmed_category add confirmedby varchar(200);
alter table ant_agosti_2012_12_06_confirmed_category add confirmedby varchar(200);
alter table ant_gloss_20130517080844_confirmed_category add confirmedby varchar(200);
alter table diatom_test_2012_12_19_confirmed_category add confirmedby varchar(200);
alter table fna2_jing_20130220160152_confirmed_category add confirmedby varchar(200);
alter table fna2_jing_20130221132244_confirmed_category add confirmedby varchar(200);
alter table fna2_jing_20130221171529_confirmed_category add confirmedby varchar(200);
alter table fna2_v19_jing_20130227141308_confirmed_category add confirmedby varchar(200);
alter table fna2_v20_jing_20130228082452_confirmed_category add confirmedby varchar(200);
alter table fna2_v21_jing_20130228140000_confirmed_category add confirmedby varchar(200);
alter table fna2_v22_jing_20130301080941_confirmed_category add confirmedby varchar(200);
alter table fna2_v23_jing_20130301113018_confirmed_category add confirmedby varchar(200);
alter table fna2_v26_jing_20130314134608_confirmed_category add confirmedby varchar(200);
alter table fna2_v27_jing_20130314153702_confirmed_category add confirmedby varchar(200);
alter table fna2_v2_jing_20130402084106_confirmed_category add confirmedby varchar(200);
alter table fna2_v3_jing_20130314110323_confirmed_category add confirmedby varchar(200);
alter table fna2_v4_jing_20130225160038_confirmed_category add confirmedby varchar(200);
alter table fna2_v5_jing_20130226072402_confirmed_category add confirmedby varchar(200);
alter table fna2_v7_jing_20130226143722_confirmed_category add confirmedby varchar(200);
alter table fna2_v8_jing_20130227093016_confirmed_category add confirmedby varchar(200);
alter table fnagloss_fromHong_20130517125327_confirmed_category add confirmedby varchar(200);
alter table fnav2_2013_02_05_confirmed_category add confirmedby varchar(200);
alter table fnav2_jing_20130212083354_confirmed_category add confirmedby varchar(200);
alter table fnav2_jing_20130213204937_confirmed_category add confirmedby varchar(200);
alter table fnav5_cui_2012_12_06_confirmed_category add confirmedby varchar(200);
alter table fnav5_jing_2013_01_23_confirmed_category add confirmedby varchar(200);
alter table fnav5_test_20130206182317_confirmed_category add confirmedby varchar(200);
alter table fnav8_sonali_2012_12_07_confirmed_category add confirmedby varchar(200);
alter table fna_gloss_confirmed_category add confirmedby varchar(200);
alter table fna_gloss_final_20130517_confirmed_category add confirmedby varchar(200);
alter table fna_jing_merge_confirmed_category add confirmedby varchar(200);
alter table foc_v10_jing_20130409171802_confirmed_category add confirmedby varchar(200);
alter table foc_v11_jing_20130502111907_confirmed_category add confirmedby varchar(200);
alter table foc_v12_jing_20130502141840_confirmed_category add confirmedby varchar(200);
alter table foc_v13_jing_20130503115239_confirmed_category add confirmedby varchar(200);
alter table foc_v14_jing_20130506091135_confirmed_category add confirmedby varchar(200);
alter table foc_v2021_jing_20130507150953_confirmed_category add confirmedby varchar(200);
alter table foc_v23_jing_20130507134321_confirmed_category add confirmedby varchar(200);
alter table foc_v25_jing_20130508081633_confirmed_category add confirmedby varchar(200);
alter table foc_v4_jing_20130416123440_confirmed_category add confirmedby varchar(200);
alter table foc_v5_jing_20130425141012_confirmed_category add confirmedby varchar(200);
alter table foc_v6_jing_20130515110950_confirmed_category add confirmedby varchar(200);
alter table foc_v7_jing_20130426135157_confirmed_category add confirmedby varchar(200);
alter table foc_v8_jing_20130430091947_confirmed_category add confirmedby varchar(200);
alter table foc_v8_jing_20130430113241_confirmed_category add confirmedby varchar(200);
alter table foc_v9_jing_20130501080458_confirmed_category add confirmedby varchar(200);
alter table foroto_fnav4_2012_10_25_confirmed_category add confirmedby varchar(200);
alter table parthv2_2012_11_09_confirmed_category add confirmedby varchar(200);
alter table plant_gloss_for_iplant_confirmed_category add confirmedby varchar(200);
alter table sponges_1_20130425142742_confirmed_category add confirmedby varchar(200);
alter table test_20130207094242_confirmed_category add confirmedby varchar(200);
alter table treatise_confirmed_category add confirmedby varchar(200);
alter table treatise_gloss_confirmed_category add confirmedby varchar(200);
alter table treatise_o_confirmed_category add confirmedby varchar(200);

/* copiedFrom*/
alter table  OTO_Demo_confirmed_category add copiedFrom varchar(100) default null;
alter table ant_agosti_2012_12_06_confirmed_category add copiedFrom varchar(100) default null;
alter table ant_gloss_20130517080844_confirmed_category add copiedFrom varchar(100) default null;
alter table diatom_test_2012_12_19_confirmed_category add copiedFrom varchar(100) default null;
alter table fna2_jing_20130220160152_confirmed_category add copiedFrom varchar(100) default null;
alter table fna2_jing_20130221132244_confirmed_category add copiedFrom varchar(100) default null;
alter table fna2_jing_20130221171529_confirmed_category add copiedFrom varchar(100) default null;
alter table fna2_v19_jing_20130227141308_confirmed_category add copiedFrom varchar(100) default null;
alter table fna2_v20_jing_20130228082452_confirmed_category add copiedFrom varchar(100) default null;
alter table fna2_v21_jing_20130228140000_confirmed_category add copiedFrom varchar(100) default null;
alter table fna2_v22_jing_20130301080941_confirmed_category add copiedFrom varchar(100) default null;
alter table fna2_v23_jing_20130301113018_confirmed_category add copiedFrom varchar(100) default null;
alter table fna2_v26_jing_20130314134608_confirmed_category add copiedFrom varchar(100) default null;
alter table fna2_v27_jing_20130314153702_confirmed_category add copiedFrom varchar(100) default null;
alter table fna2_v2_jing_20130402084106_confirmed_category add copiedFrom varchar(100) default null;
alter table fna2_v3_jing_20130314110323_confirmed_category add copiedFrom varchar(100) default null;
alter table fna2_v4_jing_20130225160038_confirmed_category add copiedFrom varchar(100) default null;
alter table fna2_v5_jing_20130226072402_confirmed_category add copiedFrom varchar(100) default null;
alter table fna2_v7_jing_20130226143722_confirmed_category add copiedFrom varchar(100) default null;
alter table fna2_v8_jing_20130227093016_confirmed_category add copiedFrom varchar(100) default null;
alter table fnagloss_fromHong_20130517125327_confirmed_category add copiedFrom varchar(100) default null;
alter table fnav2_2013_02_05_confirmed_category add copiedFrom varchar(100) default null;
alter table fnav2_jing_20130212083354_confirmed_category add copiedFrom varchar(100) default null;
alter table fnav2_jing_20130213204937_confirmed_category add copiedFrom varchar(100) default null;
alter table fnav5_cui_2012_12_06_confirmed_category add copiedFrom varchar(100) default null;
alter table fnav5_jing_2013_01_23_confirmed_category add copiedFrom varchar(100) default null;
alter table fnav5_test_20130206182317_confirmed_category add copiedFrom varchar(100) default null;
alter table fnav8_sonali_2012_12_07_confirmed_category add copiedFrom varchar(100) default null;
alter table fna_gloss_confirmed_category add copiedFrom varchar(100) default null;
alter table fna_gloss_final_20130517_confirmed_category add copiedFrom varchar(100) default null;
alter table fna_jing_merge_confirmed_category add copiedFrom varchar(100) default null;
alter table foc_v10_jing_20130409171802_confirmed_category add copiedFrom varchar(100) default null;
alter table foc_v11_jing_20130502111907_confirmed_category add copiedFrom varchar(100) default null;
alter table foc_v12_jing_20130502141840_confirmed_category add copiedFrom varchar(100) default null;
alter table foc_v13_jing_20130503115239_confirmed_category add copiedFrom varchar(100) default null;
alter table foc_v14_jing_20130506091135_confirmed_category add copiedFrom varchar(100) default null;
alter table foc_v2021_jing_20130507150953_confirmed_category add copiedFrom varchar(100) default null;
alter table foc_v23_jing_20130507134321_confirmed_category add copiedFrom varchar(100) default null;
alter table foc_v25_jing_20130508081633_confirmed_category add copiedFrom varchar(100) default null;
alter table foc_v4_jing_20130416123440_confirmed_category add copiedFrom varchar(100) default null;
alter table foc_v5_jing_20130425141012_confirmed_category add copiedFrom varchar(100) default null;
alter table foc_v6_jing_20130515110950_confirmed_category add copiedFrom varchar(100) default null;
alter table foc_v7_jing_20130426135157_confirmed_category add copiedFrom varchar(100) default null;
alter table foc_v8_jing_20130430091947_confirmed_category add copiedFrom varchar(100) default null;
alter table foc_v8_jing_20130430113241_confirmed_category add copiedFrom varchar(100) default null;
alter table foc_v9_jing_20130501080458_confirmed_category add copiedFrom varchar(100) default null;
alter table foroto_fnav4_2012_10_25_confirmed_category add copiedFrom varchar(100) default null;
alter table parthv2_2012_11_09_confirmed_category add copiedFrom varchar(100) default null;
alter table plant_gloss_for_iplant_confirmed_category add copiedFrom varchar(100) default null;
alter table sponges_1_20130425142742_confirmed_category add copiedFrom varchar(100) default null;
alter table test_20130207094242_confirmed_category add copiedFrom varchar(100) default null;
alter table treatise_confirmed_category add copiedFrom varchar(100) default null;
alter table treatise_gloss_confirmed_category add copiedFrom varchar(100) default null;
alter table treatise_o_confirmed_category add copiedFrom varchar(100) default null;

/*incorrect last time. may already fixed*/
alter table OTO_Demo_term_category add sourceDataset text;
alter table OTO_Demo_web_grouped_terms add sourceDataset text;

/*create system reserved datasets*/
/createSystemReservedDatasets.do

/*may need to give default comfirmedby for those datasets*/

/*need to import the published glossary IDs into glossary_dictionary*/

alter table OTO_Demo_web_grouped_terms modify sourceDataset varchar(100);
alter table Algea_glossary_web_grouped_terms modify sourceDataset varchar(100);
alter table Fossil_glossary_web_grouped_terms modify sourceDataset varchar(100);
alter table Hymenoptera_glossary_web_grouped_terms modify sourceDataset varchar(100);
alter table Plant_glossary_web_grouped_terms modify sourceDataset varchar(100);
alter table Porifera_glossary_web_grouped_terms modify sourceDataset varchar(100);
alter table ant_agosti_2012_12_06_web_grouped_terms modify sourceDataset varchar(100);
alter table ant_gloss_20130517080844_web_grouped_terms modify sourceDataset varchar(100);
alter table diatom_test_2012_12_19_web_grouped_terms modify sourceDataset varchar(100);
alter table fna2_jing_20130220160152_web_grouped_terms modify sourceDataset varchar(100);
alter table fna2_jing_20130221132244_web_grouped_terms modify sourceDataset varchar(100);
alter table fna2_jing_20130221171529_web_grouped_terms modify sourceDataset varchar(100);
alter table fna2_v19_jing_20130227141308_web_grouped_terms modify sourceDataset varchar(100);
alter table fna2_v20_jing_20130228082452_web_grouped_terms modify sourceDataset varchar(100);
alter table fna2_v21_jing_20130228140000_web_grouped_terms modify sourceDataset varchar(100);
alter table fna2_v22_jing_20130301080941_web_grouped_terms modify sourceDataset varchar(100);
alter table fna2_v23_jing_20130301113018_web_grouped_terms modify sourceDataset varchar(100);
alter table fna2_v26_jing_20130314134608_web_grouped_terms modify sourceDataset varchar(100);
alter table fna2_v27_jing_20130314153702_web_grouped_terms modify sourceDataset varchar(100);
alter table fna2_v2_jing_20130402084106_web_grouped_terms modify sourceDataset varchar(100);
alter table fna2_v3_jing_20130314110323_web_grouped_terms modify sourceDataset varchar(100);
alter table fna2_v4_jing_20130225160038_web_grouped_terms modify sourceDataset varchar(100);
alter table fna2_v5_jing_20130226072402_web_grouped_terms modify sourceDataset varchar(100);
alter table fna2_v7_jing_20130226143722_web_grouped_terms modify sourceDataset varchar(100);
alter table fna2_v8_jing_20130227093016_web_grouped_terms modify sourceDataset varchar(100);
alter table fnagloss_fromHong_20130517125327_web_grouped_terms modify sourceDataset varchar(100);
alter table fnav2_2013_02_05_web_grouped_terms modify sourceDataset varchar(100);
alter table fnav2_jing_20130212083354_web_grouped_terms modify sourceDataset varchar(100);
alter table fnav2_jing_20130213204937_web_grouped_terms modify sourceDataset varchar(100);
alter table fnav5_cui_2012_12_06_web_grouped_terms modify sourceDataset varchar(100);
alter table fnav5_jing_2013_01_23_web_grouped_terms modify sourceDataset varchar(100);
alter table fnav8_sonali_2012_12_07_web_grouped_terms modify sourceDataset varchar(100);
alter table fna_gloss_web_grouped_terms modify sourceDataset varchar(100);
alter table fna_jing_merge_web_grouped_terms modify sourceDataset varchar(100);
alter table foc_v10_jing_20130409171802_web_grouped_terms modify sourceDataset varchar(100);
alter table foc_v11_jing_20130502111907_web_grouped_terms modify sourceDataset varchar(100);
alter table foc_v12_jing_20130502141840_web_grouped_terms modify sourceDataset varchar(100);
alter table foc_v13_jing_20130503115239_web_grouped_terms modify sourceDataset varchar(100);
alter table foc_v14_jing_20130506091135_web_grouped_terms modify sourceDataset varchar(100);
alter table foc_v2021_jing_20130507150953_web_grouped_terms modify sourceDataset varchar(100);
alter table foc_v23_jing_20130507134321_web_grouped_terms modify sourceDataset varchar(100);
alter table foc_v25_jing_20130508081633_web_grouped_terms modify sourceDataset varchar(100);
alter table foc_v4_jing_20130416123440_web_grouped_terms modify sourceDataset varchar(100);
alter table foc_v5_jing_20130425141012_web_grouped_terms modify sourceDataset varchar(100);
alter table foc_v6_jing_20130515110950_web_grouped_terms modify sourceDataset varchar(100);
alter table foc_v7_jing_20130426135157_web_grouped_terms modify sourceDataset varchar(100);
alter table foc_v8_jing_20130430091947_web_grouped_terms modify sourceDataset varchar(100);
alter table foc_v8_jing_20130430113241_web_grouped_terms modify sourceDataset varchar(100);
alter table foc_v9_jing_20130501080458_web_grouped_terms modify sourceDataset varchar(100);
alter table foroto_fnav4_2012_10_25_web_grouped_terms modify sourceDataset varchar(100);
alter table parthv2_2012_11_09_web_grouped_terms modify sourceDataset varchar(100);
alter table plant_gloss_for_iplant_web_grouped_terms modify sourceDataset varchar(100);
alter table sponges_1_20130425142742_web_grouped_terms modify sourceDataset varchar(100);
alter table test_20130207094242_web_grouped_terms modify sourceDataset varchar(100);
alter table treatise_web_grouped_terms modify sourceDataset varchar(100);
alter table treatise_o_web_grouped_terms modify sourceDataset varchar(100);

/*track mergedInto info for existing download*/
alter table glossary_versions add mergedInto varchar(100) default null;

/*to support transaction, needs to use storage engine INNODB*/
ALTER TABLE OTO_Demo_categories ENGINE = INNODB;
ALTER TABLE OTO_Demo_comments ENGINE = INNODB;
ALTER TABLE OTO_Demo_confirmed_category ENGINE = INNODB;
ALTER TABLE OTO_Demo_review_history ENGINE = INNODB;
ALTER TABLE OTO_Demo_sentence ENGINE = INNODB;
ALTER TABLE OTO_Demo_user_terms_decisions ENGINE = INNODB;
ALTER TABLE OTO_Demo_user_terms_relations ENGINE = INNODB;
ALTER TABLE OTO_Demo_web_grouped_terms ENGINE = INNODB;
ALTER TABLE OTO_Demo_syns ENGINE = INNODB;

ALTER TABLE Algea_glossary_categories ENGINE = INNODB;
ALTER TABLE Algea_glossary_comments ENGINE = INNODB;
ALTER TABLE Algea_glossary_confirmed_category ENGINE = INNODB;
ALTER TABLE Algea_glossary_review_history ENGINE = INNODB;
ALTER TABLE Algea_glossary_sentence ENGINE = INNODB;
ALTER TABLE Algea_glossary_user_terms_decisions ENGINE = INNODB;
ALTER TABLE Algea_glossary_user_terms_relations ENGINE = INNODB;
ALTER TABLE Algea_glossary_web_grouped_terms ENGINE = INNODB;
ALTER TABLE Algea_glossary_syns ENGINE = INNODB;

ALTER TABLE Fossil_glossary_categories ENGINE = INNODB;
ALTER TABLE Fossil_glossary_comments ENGINE = INNODB;
ALTER TABLE Fossil_glossary_confirmed_category ENGINE = INNODB;
ALTER TABLE Fossil_glossary_review_history ENGINE = INNODB;
ALTER TABLE Fossil_glossary_sentence ENGINE = INNODB;
ALTER TABLE Fossil_glossary_user_terms_decisions ENGINE = INNODB;
ALTER TABLE Fossil_glossary_user_terms_relations ENGINE = INNODB;
ALTER TABLE Fossil_glossary_web_grouped_terms ENGINE = INNODB;
ALTER TABLE Fossil_glossary_syns ENGINE = INNODB;

ALTER TABLE Hymenoptera_glossary_categories ENGINE = INNODB;
ALTER TABLE Hymenoptera_glossary_comments ENGINE = INNODB;
ALTER TABLE Hymenoptera_glossary_confirmed_category ENGINE = INNODB;
ALTER TABLE Hymenoptera_glossary_review_history ENGINE = INNODB;
ALTER TABLE Hymenoptera_glossary_sentence ENGINE = INNODB;
ALTER TABLE Hymenoptera_glossary_user_terms_decisions ENGINE = INNODB;
ALTER TABLE Hymenoptera_glossary_user_terms_relations ENGINE = INNODB;
ALTER TABLE Hymenoptera_glossary_web_grouped_terms ENGINE = INNODB;
ALTER TABLE Hymenoptera_glossary_syns ENGINE = INNODB;

ALTER TABLE Plant_glossary_categories ENGINE = INNODB;
ALTER TABLE Plant_glossary_comments ENGINE = INNODB;
ALTER TABLE Plant_glossary_confirmed_category ENGINE = INNODB;
ALTER TABLE Plant_glossary_review_history ENGINE = INNODB;
ALTER TABLE Plant_glossary_sentence ENGINE = INNODB;
ALTER TABLE Plant_glossary_user_terms_decisions ENGINE = INNODB;
ALTER TABLE Plant_glossary_user_terms_relations ENGINE = INNODB;
ALTER TABLE Plant_glossary_web_grouped_terms ENGINE = INNODB;
ALTER TABLE Plant_glossary_syns ENGINE = INNODB;

ALTER TABLE Porifera_glossary_categories ENGINE = INNODB;
ALTER TABLE Porifera_glossary_comments ENGINE = INNODB;
ALTER TABLE Porifera_glossary_confirmed_category ENGINE = INNODB;
ALTER TABLE Porifera_glossary_review_history ENGINE = INNODB;
ALTER TABLE Porifera_glossary_sentence ENGINE = INNODB;
ALTER TABLE Porifera_glossary_user_terms_decisions ENGINE = INNODB;
ALTER TABLE Porifera_glossary_user_terms_relations ENGINE = INNODB;
ALTER TABLE Porifera_glossary_web_grouped_terms ENGINE = INNODB;
ALTER TABLE Porifera_glossary_syns ENGINE = INNODB;
/*executed on the server*/


/*add system user*/
insert into users (userid, email, password, firstname, lastname, affiliation)
values(1, 'OTO System', 'test', 'OTO', 'System', 'OTO System');

/*map clean glossary back to db: hymenoptera*/
delete from hymenoptera_glossary_term_category_cleaned;
select * from hymenoptera_glossary_term_category_cleaned;

LOAD DATA INFILE '/tmp/term_category_published.csv' 
INTO TABLE hymenoptera_glossary_term_category_cleaned
FIELDS TERMINATED BY ',' 
Optionally ENCLOSED BY '"'
LINES TERMINATED BY '\r\n';

delete from hymenoptera_glossary_syns_cleaned;
select * from hymenoptera_glossary_syns_cleaned;

LOAD DATA INFILE '/tmp/syn_published.csv' 
INTO TABLE hymenoptera_glossary_syns_cleaned
FIELDS TERMINATED BY ',' 
Optionally ENCLOSED BY '"'
LINES TERMINATED BY '\r\n';


update hymenoptera_glossary_syns_cleaned set category = 'life_stage'
where term = 'old';
update hymenoptera_glossary_syns_cleaned set category = 'behavior'
where term = 'fight';

/*add field: isPrivate, default false*/
alter table datasetprefix add isPrivate Boolean default false;
