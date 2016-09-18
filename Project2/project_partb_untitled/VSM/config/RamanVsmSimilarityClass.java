package org.apache.lucene.search.similarities;

/**
 * @author Ramanpreet Singh Khinda
 * @category  CSE 535 IR Project B
 *
 * @code This is custom Similarity class for VSM Model Implementation.
 *       See : "RamanVsmSimilarityClass.jar" file which we have to copy in /solr/dist/ folder
 *       Dependency : "lucene-core-5.3.0.jar" present in ~/solr_2/solr/solr-5.3.0/server/solr-webapp/webapp/WEB-INF/lib/
 *
 **/
public class RamanVsmSimilarityClass extends DefaultSimilarity {

	@Override
	public float tf(float freq) {
		return (float) (1.0f + Math.log(freq));
	}
}
