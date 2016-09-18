/*
The MIT License (MIT)

Original Copyright (c) 2015 Jaideep Bhoosreddy
Updated Version 2 by Ramanpreet Singh Khinda

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

package com.solr.query;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

import twitter4j.JSONArray;
import twitter4j.JSONException;
import twitter4j.JSONObject;

/**
 * @author Ramanpreet Singh Khinda
 * @category CSE 535 IR Project B
 *
 * @code This Program will run queries from queries.txt file and will generate a
 *       TREC-eval compatible output result file.
 * 
 *       Features :- 1. Query Language Detection 2. Query Language Translation
 *       3. Multilingual Query Processing
 *
 **/

class ExecuteQueriesOnSolr {
	private String queryInputFileName, charSet, workingDirectory;
	private final String USER_AGENT = "Mozilla/5.0";
	private String trecInputFile = "trec_supported_input.txt";

	private String projectB_Core_URL = "http://ramanpreet1990.koding.io:8983/solr/RamanVsmModel";

	// Used 3rd party language detection API. See : https://detectlanguage.com/
	private String langDetectionUrl = "http://ws.detectlanguage.com/0.2/detect";
	private final String LANG_DETECTION_API_KEY = "7bd4928c9e29cc17138d3c810f39c3a8";

	// Used Windows Azure language translation API. See :
	// https://www.microsoft.com/en-us/translator/getstarted.aspx
	private final String langTranslationClientId = "IR_Project_B";
	private final String LANG_TRANSLATION_SECRET_KEY = "FPxC7f5ikFfzZm5EWqbYd5R4wvxB0niS1FhS3rYt16A=";

	ExecuteQueriesOnSolr(String queryInputFileName) {
		this.queryInputFileName = queryInputFileName;

		charSet = java.nio.charset.StandardCharsets.UTF_8.name();
		workingDirectory = System.getProperty("user.dir") + "/src/com/solr/query/";
	}

	/**
	 * reads the input query file and make multilingual search on Solr
	 */
	public int executeQueriesOnSolr() {
		ArrayList<String> inputQueries = readQueriesFromFile();

		if (inputQueries.size() == 0) {
			return -1; // error code -1: no input queries
		}

		int numOfRows = 1000;
		StringBuilder trecInputEvalFormat = new StringBuilder();

		String searchEngine = "ramanVsmSearchEn";
		String defaultField = "text_en,text_custom_en,text_ru,text_custom_ru,text_de,text_custom_de,tweet_hashtags";

		String lang, query, response, q1, q2, translated_en, translated_ru, translated_de;

		Translate.setClientId(langTranslationClientId);
		Translate.setClientSecret(LANG_TRANSLATION_SECRET_KEY);

		for (int i = 0; i < inputQueries.size(); i++) {
			lang = getLanguageOfQuery(inputQueries.get(i).trim());

			try {
				q1 = inputQueries.get(i); // escapeSpecialChars(inputQueries.get(i));
				System.out.println("lang :" + lang);

				q2 = "\"\"" + q1 + "\"\"";
				System.out.println("query text :" + q2);

				try {
					if (lang.equals("ru")) {
						translated_en = Translate.execute(q1, Language.RUSSIAN, Language.ENGLISH);
						translated_de = Translate.execute(q1, Language.RUSSIAN, Language.GERMAN);

						q2 = "text_custom_ru:\"\"" + q1 + "\"\"" + "ORtext_custom_en:\"\"" + translated_en + "\"\"" + "ORtext_custom_de:\"\""
								+ translated_de + "\"\"";

						// q2 = "\"\"" + removeDuplicate(q1 + " " +
						// translated_en + " " + translated_de) + "\"\"";
						searchEngine = "ramanVsmSearchRu";

					} else if (lang.equals("de")) {
						translated_en = Translate.execute(q1, Language.GERMAN, Language.ENGLISH);
						translated_ru = Translate.execute(q1, Language.GERMAN, Language.RUSSIAN);

						q2 = "text_custom_de:\"\"" + q1 + "\"\"" + "ORtext_custom_en:\"\"" + translated_en + "\"\"" + "ORtext_custom_ru:\"\""
								+ translated_ru + "\"\"";

						// q2 = "\"\"" + removeDuplicate(q1 + " " +
						// translated_en + " " + translated_ru) + "\"\"";
						searchEngine = "ramanVsmSearchDe";

					} else {
						lang = "en";

						translated_ru = Translate.execute(q1, Language.ENGLISH, Language.RUSSIAN);
						translated_de = Translate.execute(q1, Language.ENGLISH, Language.GERMAN);

						q2 = "text_custom_en:\"\"" + q1 + "\"\"" + "ORtext_custom_ru:\"\"" + translated_ru + "\"\"" + "ORtext_custom_de:\"\""
								+ translated_de + "\"\"";

						// q2 = "\"\"" + removeDuplicate(q1 + " " +
						// translated_ru + " " + translated_de) + "\"\"";
						searchEngine = "ramanVsmSearchEn";
					}
				} catch (Exception e) {
					e.printStackTrace();
				}

				query = "q=" + URLEncoder.encode(q2, charSet) + "&start=0&rows="
						+ URLEncoder.encode(String.valueOf(numOfRows), charSet) + "&df=" + defaultField
						+ "&mm=2<1%208<2" + "&fl=id%2Cscore&wt=json&indent=true";

				response = fetchHTTPData(projectB_Core_URL + searchEngine, query);

				System.out.println("query :" + query);
				System.out.println("response :" + response);

				trecInputEvalFormat = trecInputEvalFormat
						.append(parseJSONResponseFromSolr(String.format("%03d", (i + 1)), response, "default", false));

			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
				return -2;
			} catch (IOException e) {
				e.printStackTrace();
				return -3;
			} catch (NumberFormatException e) {
				e.printStackTrace();
				return -4;
			} catch (JSONException e) {
				e.printStackTrace();
				return -5;
			}
		}

		writeDataInTrecFormat(trecInputEvalFormat.toString());

		return 0;
	}

	public ArrayList<String> readQueriesFromFile() {
		ArrayList<String> queries = new ArrayList<>();
		String aLine;

		try {
			FileInputStream fstream1 = new FileInputStream(queryInputFileName);
			DataInputStream in = new DataInputStream(fstream1);
			BufferedReader br = new BufferedReader(new InputStreamReader(in, "UTF8"));

			while ((aLine = br.readLine()) != null) {
				queries.add(aLine.substring(4));
			}

			br.close();
		} catch (Exception e1) {
			e1.printStackTrace();
		}

		return queries;
	}

	/**
	 * @param queryText
	 * 
	 * @code The Language of query is tested by firing a HTTP post request to
	 *       http://ws.detectlanguage.com/0.2/detect.
	 * 
	 *       The output result is in JSON Format which is parsed to extract
	 *       language field.
	 */
	public String getLanguageOfQuery(String queryText) {
		String lang = "";
		queryText = queryText.replace(" ", "+");
		String response;

		try {
			String query = String.format("q=%s&key=%s", URLEncoder.encode(queryText, charSet),
					URLEncoder.encode(LANG_DETECTION_API_KEY, charSet));

			response = fetchHTTPData(langDetectionUrl, query);

			if (!response.equals(""))
				lang = parseJSONStringAndFindLanguage(response);
			else
				System.out.println("No response from Language detection server...");

		} catch (Exception ex) {
			System.out.println("Exception occured while detecting language...");
			ex.printStackTrace();
		}

		return lang;
	}

	public String parseJSONStringAndFindLanguage(String jSONString) throws JSONException {
		JSONObject jObj;
		String language = "";

		jObj = new JSONObject(jSONString);
		language = jObj.getJSONObject("data").getJSONArray("detections").getJSONObject(0).get("language").toString();

		return language;
	}

	public String fetchHTTPData(String URL, String query) throws IOException {
		String response = "";
		int responseCode = 0;

		HttpURLConnection httpConn = (HttpURLConnection) new URL(URL + "?" + query).openConnection();
		System.out.println("httpConn : " + httpConn);

		httpConn.setDoOutput(true);
		httpConn.setRequestProperty("Accept-Charset", charSet);
		httpConn.setRequestProperty("User-Agent", USER_AGENT);
		httpConn.setRequestProperty("Content-type", "text/json; charset=utf-8");

		responseCode = httpConn.getResponseCode();

		if (responseCode == 200) {
			BufferedReader in = new BufferedReader(new InputStreamReader(httpConn.getInputStream()));
			String inputLine;
			StringBuffer responseBuffer = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				responseBuffer.append(inputLine);
			}

			in.close();
			response = responseBuffer.toString();
		}

		return response;
	}

	public String parseJSONResponseFromSolr(String queryNumber, String responseFromSolr, String modelName,
			boolean bIsOnlyNumFoundRequired) throws NumberFormatException, JSONException {
		String numFound;
		StringBuilder trec_Response = new StringBuilder();
		JSONObject jObjTemp;

		if (responseFromSolr.equals("")) {
			System.out.println("queryNumber :" + queryNumber);
			System.out.println("-------------------------------------");

			return trec_Response.toString();
		}

		JSONObject jObj = new JSONObject(responseFromSolr).getJSONObject("response");
		numFound = jObj.get("numFound").toString();

		if (bIsOnlyNumFoundRequired)
			return numFound;

		JSONArray jObjArray = new JSONArray(jObj.getString("docs").toString());

		for (int i = 0; i < jObjArray.length(); i++) {
			jObjTemp = jObjArray.getJSONObject(i);

			trec_Response = trec_Response.append(queryNumber + " Q0 " + jObjTemp.getString("id") + " "
					+ String.valueOf(i) + " " + jObjTemp.getString("score") + " " + modelName + System.lineSeparator());

		}

		System.out.println("***********");
		return trec_Response.toString();
	}

	public void writeDataInTrecFormat(String trecSupportedInput) {
		try {
			File file = new File(workingDirectory + trecInputFile);

			if (!file.exists()) {
				file.createNewFile();
			}

			OutputStream outputStream = new FileOutputStream(file);
			Writer outputStreamWriter = new OutputStreamWriter(outputStream);

			outputStreamWriter.write(trecSupportedInput);
			outputStreamWriter.close();

			System.out.println("Created file is : " + new File(workingDirectory + trecInputFile).getAbsolutePath());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String removeDuplicate(String queryString) {
		StringBuilder sb = new StringBuilder();

		String[] queryTerms = queryString.split("\\s+");
		Set<String> seen = new HashSet<String>();

		for (int i = 0; i < queryTerms.length; ++i) {
			String terms = queryTerms[i];
			if (!seen.contains(terms)) {
				seen.add(terms);
				sb.append(terms + " ");
			}
		}
		return sb.toString().trim();
	}

	// unused function
	public String escapeSpecialChars(String s) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);

			if (c == '#') {
				sb.append("%23");
			} else if (c == ' ') {
				sb.append("%20");
			} else if (c == '%') {
				sb.append("%25");
			} else if (c == '!') {
				sb.append("%21");
			} else if (c == '\'') {
				sb.append("%27");
			} else if (c == ',') {
				sb.append("%2C");
			} else if (c == '*') {
				sb.append("%2A");
			} else {
				sb.append(c);
			}
		}

		return sb.toString();
	}
}

class FetchQueryResultForTrecEval {

	public static boolean IsFileExists(String filePathString) {
		File f = new File(filePathString);

		if (f.exists() && f.isFile()) {
			return true;
		} else
			return false;
	}

	public static void main(String[] args) {
		String queryFileName = "queries.txt";
		queryFileName = System.getProperty("user.dir") + "/src/com/solr/query/" + queryFileName;

		if (!FetchQueryResultForTrecEval.IsFileExists(queryFileName)) {
			System.out.println("File not found. Please provide queries.txt file in : " + queryFileName);
			return;
		}

		ExecuteQueriesOnSolr executeQueries = new ExecuteQueriesOnSolr(queryFileName);
		executeQueries.executeQueriesOnSolr();
	}
}
