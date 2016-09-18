#!/usr/bin/env python 
# -*- coding: utf-8 -*- 

import sys
import re
import os
import json
# if you are using python 3, you should 
# import urllib.request 

from urllib import urlencode
import urllib2
import commands

def drange(start, stop, step):
	r = start
	while r < stop:
		yield r
		r += step

def main():

	k1_list=drange(0.1, 3.0, 0.1)
	b_list=drange(0.1, 0.9, 0.1)

	schema_pointer=open('schema.xml', 'r')
	schema_lines = schema_pointer.readlines()
	schema_pointer.close()
	queries_pointer=open('queries2.txt', 'r')
	k1_l=[]
	b_l=[]
	for k in ["%g" % x for x in k1_list]:
		k1_l.append(k)
	for b1 in ["%g" % x for x in b_list]:
		b_l.append(b1)
	for each_query in queries_pointer:
		for k1 in k1_l:
			for b in b_l:
				print 'Searching for b = ',b,'and k1 = ',k1
				schema_lines[126] = '\t\t\t<float name="k1">'+ str(k1) +'</float>\n'
				schema_lines[127] = '\t\t\t<float name="b">'+ str(b) +'</float>\n'
				schema_pointer=open('schema.xml', 'w')
				schema_pointer.writelines(schema_lines)
				schema_pointer.close()
				(status1, output1)=commands.getstatusoutput("~/solr/solr-5.3.0/bin/solr stop -all")
				print output1
				(status2, output2)=commands.getstatusoutput("~/solr/solr-5.3.0/bin/solr start -s ~/solr/solr-5.3.0/booksdemo/solr")
				print output2
				(status3, output3)=commands.getstatusoutput(' curl http://localhost:8983/solr/booksdemo/update?commit=true -H "Content-Type: text/xml" --data-binary \'<delete><query>*:*</query></delete>\'')
				print output3
				(status4, output4)=commands.getstatusoutput(" curl 'http://localhost:8983/solr/booksdemo/update/json?commit=true' --data-binary @$(echo ~/solr/solr-5.3.0/booksdemo/Train_Data.json) -H 'Content-type:application'")
				print output4
				outfn = 'file1.txt'
				qid = each_query[:3]
				IRModel='BM25'
				outf = open(outfn, 'w')
				
				a=unicode(each_query[4:], 'utf-8')
				params = {'where': 'nexearch', 'q': a.encode('utf-8')}
				params = urlencode(params)
				url = "http://localhost:8983/solr/booksdemo/BM25SearchEn?q=%22%22" + params[2:] + "%22%22&fl=id,text_en%2Cscore&wt=json&indent=true&&rows=1500&mm=2%3C1%208%3C2"
				data=urllib2.urlopen(url)

				docs = json.load(data)['response']['docs']
				rank = 1
				for doc in docs:
				    outf.write(qid + ' ' + 'Q0' + ' ' + str(doc['id']) + ' ' + str(rank) + ' ' + str(doc['score']) + ' ' + IRModel + '\n')
				    rank += 1
				outf.close()
				res2_pointer = open('res2.txt', 'w+')
				res2_pointer.write(str(k1)+", "+str(b)+"\n==================================================================================\n\n\n")
				res2_pointer.close()
				os.system("cat res2.txt >> res.txt")
				os.system("trec_eval.9.0/./trec_eval -q -c -M1000 -m set_F.05 -m ndcg -m map -m bpref qrels.txt file1.txt >> res.txt")
			
if __name__ == '__main__':
	main()

