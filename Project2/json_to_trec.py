# -*- coding: utf-8 -*-
"""
Created on Tue Oct 27 17:47:48 2015

@author: ruhansa
"""
import json
# if you are using python 3, you should 
# import urllib.request 
import urllib2


# change the url according to your own koding username and query
inurl = 'http://localhost:8983/solr/booksdemo/BM25SearchEn?q=text_en:""PM Medvedev’s+delegation+to+coordinate+anti-terrorist+actions""ORtext_ru:""М.+Медведев+делегация+координировать+действия антитеррористической""ORtext_de:""PM+Medvedev+Delegation+in+Anti-Terror-Maßnahmen+zu koordinieren""&start=0&rows=1000&df=text_en,text_custom_en,text_ru,text_custom_ru,text_de,text_custom_de,tweet_hashtags&mm=2<1%208<2&fl=id%2Cscore&wt=json&indent=true'
outfn = '1.txt'


# change query id and IRModel name accordingly
qid = '001'
IRModel='LM'
outf = open(outfn, 'a+')
data = urllib2.urlopen(inurl)
# if you're using python 3, you should use
# data = urllib.request.urlopen(inurl)

docs = json.load(data)['response']['docs']
# the ranking should start from 1 and increase
rank = 1
for doc in docs:
    outf.write(qid + ' ' + 'Q0' + ' ' + str(doc['id']) + ' ' + str(rank) + ' ' + str(doc['score']) + ' ' + IRModel + '\n')
    rank += 1
outf.close()
