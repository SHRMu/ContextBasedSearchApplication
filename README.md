## Entity-Context-Based Search Suggestion Application

#### TU Darmstadt, Summer Term 2019, Data Management Project

### Introdcution
Elasticsearch is now a very popular search engine based on Lucene. But we think, in future the simple keywords search is not enougy.
Therefore we consider to merge the machine learning technology, especially embeddings into traditional search engine. 

### Implementation

1. Elasticsearch development framework was forked from https://github.com/panholly/esfilesearch.

2. Tensorflow model was trained and saved in Colaboratory, and then loaded by java code locally.

3. Loading model from java code, the entity autocompletion is implemented with trieTree

### Entity Embedding

rather then using the word embedding in traiditional NLP task, in this assignment we use the whole entity
for model training and predicting. 

### Result

Valid result after 50 epochs with windows_size = 10 :
+ Nearest to donald_trump: toby_keith, yemen, superfund, eric_schmidt, max_rose, adam_goldman, united_states_office_of_special_counsel, appalachian_trail,
+ Nearest to china: xi_jinping, central_military_commission, china_daily, lindsay_kemp, dandong, ashok_rajagopalan, rupert_brooke, forum_on_chinaafrica_cooperation,
+ Nearest to barack_obama: melania_trump, bessie_coleman, werner_heisenberg, victor_trumper, howard_county, eileen_atkins, bobby_fischer, fayez_alsarraj,
+ Nearest to angela_merkel: christian_democratic_union_of_germany, germany, schlumberger, danube, berlin, arab_world, wiesbaden, friedrich_merz,
+ Nearest to harry_potter: j_k_rowling, shannon_hale, sheryl_crow, hogwarts, citizens_united_v_fec, ellen_muth, sofia, h_a_hellyer,
+ Nearest to olympic_games: international_olympic_committee, toshir_mut, yuriko_koike, board_of_audit, bykada, american_banker, berkeley_heights, uur_erdener,
+ Nearest to wikipedia: gerontology_research_group, konstantin_novoselov, wikipedia_community, battle_of_gettysburg, katherine_harris, college_of_william__mary, mv_tsgt_john_a_chapman, church_square,
+ Nearest to alibaba_group: jack_ma, shanghai, claude_taylor, saeb_erekat, qingdao, national_retail_federation, domain_name_system, tokyo_stock_exchange,

### Figure

50 example entity vectors 

![entity_vector_similarity](https://raw.githubusercontent.com/SHRMu/Entity-Context-Based-Search-Suggestion/master/img/simi.png "entity_vector_similarity")

Website demo


 
