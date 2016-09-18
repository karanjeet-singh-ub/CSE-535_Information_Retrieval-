<p align="center">Information Retrieval</br>Multilingual Search System for Social Network</br>CSE 535 - Fall 2015

Goal
------
The goal of this project is to build a multilingual [faceted search](https://en.wikipedia.org/wiki/Faceted_search) system, including a front end that allows users to search and browse multilingual data based on various criteria: topic, location, person, etc.


Highlights of the Search System
---------------
> - A pure **multilingual faceted search** system
> - Can handle queries in **5 different languages**- English, Russian, German, French and Arabic
> - Based on twitter data corpus with data of around **0.1 million tweets**
> - Data spans more than **120 countries**


Components:
----
### 1. Faceted Search
This option involves leveraging the faceted search capability provided by Solr to allow various types of drill-down. Facets include people, topics, locations etc.

### 2. Cross-Document Analytics 
This option involves computing various analytics that provide insight into the data.

Examples include: volume of tweets by region/topic/hashtag, sentiment analysis, analytics illustrating cultural differences, etc. 

### 3. Cross-Lingual Retrieval/Analysis 
In this option, we demonstrates cross-lingual capabilities. This can take on many aspects: one example involves cross-lingual queries, and automatic translation of resulting foreign language snippets.

For example, a search for a particular individual/place/organization should take place simultaneously in multiple languages –achieved by automatically tagging and normalizing entities across languages. 

### 4. Ranking tweets 
This option involves coming up with a novel ranking algorithm for tweets that balances recency with importance of content when presenting tweets. It could also take into account the popularity of a tweet, or the influence of a person tweeting, the location of the user, their interests etc...

### 5. Graphical Analysis 
This option involves inferring some graphical structure from the tweets, based on entities mentioned, topics discussed etc. Graph structures (or relationships between tweets) could also be inferred through connection of topics reflected in the tweets


References
------
We have taken reference from below sources to design this search system: -</br>
1. [Introduction to Information Retrieval](http://nlp.stanford.edu/IR-book/)</br>
2. [Course by Oresoft LWC](https://www.youtube.com/watch?v=q0srNT_XM_Y&list=PL0ZVw5-GryEkGAQT7lX7oIHqyDPeUyOMQ)</br>
3. [Apache Solr Tutorials](http://lucene.apache.org/solr/quickstart.html)</br>
4. [Apache Solr Wiki](https://wiki.apache.org/solr/FrontPage)</br>
5. [Apache Solr Reference Guide](https://cwiki.apache.org/confluence/display/solr/Apache+Solr+Reference+Guide)

Credits
-------
This project uses below open source api's. We are grateful for their contribution: -

> 1. Language Detection Api of [detectlanguage.com](https://detectlanguage.com/) 
> 2. [Microsoft Bing Language Translation Api](https://github.com/boatmeme/microsoft-translator-java-api)