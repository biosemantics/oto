/**
 * Date. May. 17th 2012
 * Author: Fengqiong
 */

/*insert dataset prefix*/
insert into datasetprefix (prefix) value ('OTO_Demo');

/*table comments*/
create table OTO_Demo_comments like fna_gloss_comments;

/*page 1*/
create table OTO_Demo_categories like categories;
create table OTO_Demo_confirmed_category like fna_gloss_confirmed_category;
insert OTO_Demo_categories select * from categories;
create table OTO_Demo_web_grouped_terms like fna_gloss_web_grouped_terms;
insert OTO_Demo_web_grouped_terms select * from fna_gloss_web_grouped_terms;
delete from OTO_Demo_web_grouped_terms where groupid in (4, 8, 51, 56, 46, 48, 38, 43, 52, 5, 49);

drop table OTO_Demo_review_history;
create table OTO_Demo_review_history (
id BIGINT not null auto_increment unique,
userid int,
term varchar(100),
reviewTime DATETIME,
primary key (id)
);

create table OTO_Demo_user_terms_decisions like fna_gloss_user_terms_decisions;
create table OTO_Demo_user_terms_relations like fna_gloss_user_terms_relations;


/*page 2*/
create table OTO_Demo_sentence like fna_gloss_sentence;
insert OTO_Demo_sentence select * from fna_gloss_sentence;
delete from OTO_Demo_sentence where sentid > 6000;
create table OTO_Demo_confirmed_paths like fna_gloss_confirmed_paths;

create table OTO_Demo_web_tags like fna_gloss_web_tags;
create table OTO_Demo_user_tags_decisions like fna_gloss_user_tags_decisions;

/*page 3*/
create table OTO_Demo_user_orders_decisions like fna_gloss_user_orders_decisions;
create table OTO_Demo_confirmed_orders like fna_gloss_confirmed_orders;
create table OTO_Demo_web_orders like fna_gloss_web_orders;
/*sample records from demo*/
insert into OTO_Demo_web_orders(id, name, isBase, base, explanation)
values
(1, 'Pubescence', true, null, ''),
(2, 'Pubescence-Density Order', false, 1, 'explanation of Pubescence-Density Order'),
(3, 'Shape', true, null, ''),
(4, 'Shape Order', false, 3, 'explanation of Shape Order'),
(5, 'Orientation', true, null, ''),
(6, 'Orientation Order', false, 5, 'explanation of Orientation Order')
;

create table OTO_Demo_web_orders_terms like fna_gloss_web_orders_terms;
/*samples records from demo*/
insert into OTO_Demo_web_orders_terms(orderID, name, isBase)
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


/*new dataset: treatise*/

/*insert dataset prefix*/
insert into datasetprefix (prefix) value ('treatise');

/*table comments*/
create table treatise_comments like fna_gloss_comments;

/*page 1*/
/*categories*/
create table treatise_categories like categories;
insert treatise_categories select * from categories;
create table treatise_web_grouped_terms like fna_gloss_web_grouped_terms;
create table treatise_user_terms_decisions like fna_gloss_user_terms_decisions;
create table treatise_user_terms_relations like fna_gloss_user_terms_relations;

/*page 2*/
create table treatise_sentence like fna_gloss_sentence;
create table treatise_web_tags like fna_gloss_web_tags;
create table treatise_user_tags_decisions like fna_gloss_user_tags_decisions;

/*page 3*/
create table treatise_user_orders_decisions like fna_gloss_user_orders_decisions;
create table treatise_web_orders like fna_gloss_web_orders;
create table treatise_web_orders_terms like fna_gloss_web_orders_terms;

/*import data: sentence, term_category; user_terms_decisions*/
use markedupdatasets;
source ~/treatises_b_term_category.sql;
source ~/treatises_b_sentence.sql;

/*terms for page 1*/
insert into treatise_web_grouped_terms(term, groupid) select distinct term, 1 as groupid from treatises_b_term_category;
/*sentence for page 1*/
insert into treatise_sentence(sentid, source, sentence, originalsent, lead, status, tag, modifier, charsegment)
select sentid, source, sentence, originalsent, lead, status, tag, modifier, charsegment from treatises_b_sentence;


/*original decisions*/
insert into treatise_user_terms_decisions(term, decision, userid, decisiondate, groupid) 
select distinct term, category, 32 as userid, sysdate(), 1 as groupid
from treatises_b_term_category where category in (select category from treatise_categories);
update treatise_user_terms_decisions set relatedTerms = "";
/*use drag to group the rest*/

drop table treatise_review_history;
create table treatise_review_history (
id BIGINT not null auto_increment unique,
userid int,
term varchar(100),
reviewTime DATETIME,
primary key (id)
);