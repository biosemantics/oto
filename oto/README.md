Database introduction:

1. db name: markedupdatasets
   set up a user with all privileges on markedupdatasets 
	user name: termsuser
	password: termspassword
	SQL you may use: grant all privileges on markedupdatasets.* to termsuser@localhost identified by 'termspassword';
	
2. tables list
	+--------------------------------+
	| 21 Tables in markedupdatasets  |
	+--------------------------------+
	| confirmed_category             |
	| confirmed_orders               |
	| confirmed_paths                |
	| datasetprefix                  |
	| fna_v19_comments               |
	| fna_v19_finalized_orders       |
	| fna_v19_finalized_tags         |
	| fna_v19_finalized_terms        |
	| fna_v19_sentence               |
	| fna_v19_user_grouped_decisions |
	| fna_v19_user_orders_decisions  |
	| fna_v19_user_tags_decisions    |
	| fna_v19_user_terms_decisions   |
	| fna_v19_user_terms_relations   |
	| fna_v19_web_grouped_terms      |
	| fna_v19_web_orders             |
	| fna_v19_web_orders_terms       |
	| fna_v19_web_tags               |
	| fna_v19_web_user_grouped_terms |
	| fnaglossary                    |
	| users                          |
	+--------------------------------+

3. introduction of tables
	(1) The following three tables are storing manager's decisions on all datasets.
	 	confirmed_category: stores manager's decisions(accepted or not) on users' opinions on categorizing page.
		confirmed_orders: stores manager's decisions on users' opinions on order page.
 		confirmed_paths: stores manager's decisions on users' opinions on hierarchy page.
 	
 	(2) The following three tables general for this website. Data in these tables are not dataset specified.
		datasetprefix: stores dataset prefixes
 		fnaglossary: 
 			stores glossary. 
 			Categories listed in categorizing page come from the �category� field.
 		users: stores users' information
 	
 	(3) The following tables are dataset specified, named with a dataset prefix (e.g. dataset prefix is fna_v19). 
		fna_v19_comments: stores user's comments in dataset fna_v19
		
		fna_v19_finalized_orders: stores entropy score of decisions on order page in dataset fna_v19
		fna_v19_finalized_tags: stores entropy score of decisions on hierarchy page in dataset fna_v19
		fna_v19_finalized_terms: stores entropy score of decisions on categorizing page in dataset fna_v19
			
		fna_v19_user_orders_decisions: stores users' decisions on order page in dataset fna_v19
		fna_v19_user_tags_decisions: stores users' decisions on hierarchy page in dataset fna_v19
		fna_v19_user_terms_decisions: stores users' decisions of term's category on categorizing page in dataset fna_v19
		fna_v19_user_terms_relations: stores users' decisions of term's relationship (exclusive or synonym) on categorizing page in dataset fna_v19
		
		fna_v19_web_grouped_terms: stored grouped terms in dataset fna_v19
			data should be loaded when setting up this dataset 
			Essential fields: (all terms listed in categorizing page comes from 'term' and 'cooccurTerm')
				groupId
				term 
				cooccurTerm
		
		fna_v19_web_orders: stores orders list in dataset fna_v19
			data should be loaded when setting up this dataset
			Essential fields:
				id: 	order's id
				name: 	the order's name
				isBase: if the order is base, isBase = true
				base: 	base order's id, is some id in fna_v19_web_orders
				explanation: a description of the order, will be shown as hint when mouse is on the order's name
		fna_v19_web_orders_terms: stores terms of orders in dataset fna_v19
			data should be loaded when setting up this dataset
			Essential fields:
				id: term's ID
				orderID: is some base order's id in fna_v19_web_orders
				name: term name
				isBase: is true if you want this term to be the default first term in the orders
		
		fna_v19_web_tags: stores terms listed on hierarchy page in dataset fna_v19
			initially there are 7 records (id 1 to 7) in this table. 
			Make sure these 7 records are always in this table.If not, insert with the following SQL:
				 insert into fna_v19_web_tags(tagID, tagName) values
	      		 (1, 'Plant'), (2, 'Root'), (3, 'Stem'), (4, 'Leaf'), (5, 'Fruit'), (6, 'Seed'), (7, 'Flower');
		fna_v19_sentence: stores sentences in dataset fna_v19	
			all tags in hierarchy page come from this table 'tag' field. 
			When loading the hierarchy page at the very first time, tags from this table will be inserted into fna_v19_web_tags automatically.
			Later changes in this table will not be handled.
			field 'source', 'originalsent' in this table will be used for term's context lookup.		 	 
		
		fna_v19_web_user_grouped_terms: this table will not be used in ONTNEW, keep it for reference to the old ONT
		fna_v19_user_grouped_decisions: this table will not be used in ONTNEW, keep it for reference to the old ONT

4. add a new dataset

	(1) add a new prefix into table 'datasetprefix'

	(2) add the following tables with the (new prefix)
		(new prefix)_web_grouped_terms
		(new prefix)_web_tags
		(new prefix)_sentence
		(new prefix)_web_orders
		(new prefix)_web_orders_terms
		(new prefix)_user_terms_decisions
		(new prefix)_user_terms_relations
		(new prefix)_user_tags_decisions
		(new prefix)_user_orders_decisions
		(new prefix)_finalized_orders
		(new prefix)_finalized_tags
		(new prefix)_finalized_terms
		(new prefix)_comments
		
	(3) upload data to the following tables:
		For categorizing page: 
			(new prefix)_web_grouped_terms: All terms in categorizing page come from this table. 
	
		For Structure Hierarchy page: 
			(new prefix)_web_tags: 
				 
				 insert into (new prefix)_web_tags(tagID, tagName) values
		      	 (1, 'Plant'), (2, 'Root'), (3, 'Stem'), (4, 'Leaf'), (5, 'Fruit'), (6, 'Seed'), (7, 'Flower');
			(new prefix)_sentence: 
				all tags in hierarchy page come from this table �tag� field. 
				When loading the hierarchy page at the very first time, tags from this table will be inserted into (new prefix)_web_tags automatically.
				field �source�, �originalsent� in this table will be used for term's context lookup.
	
		For Orders page:
			(new prefix)_web_orders: 
			(new prefix)_web_orders_terms
			
	(4) the other new added table should be empty when setting up a new dataset

