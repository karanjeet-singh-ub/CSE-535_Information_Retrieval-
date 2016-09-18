import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * @author Ramanpreet Singh Khinda
 * @category  CSE 535 IR Project B
 *
 * @code This program will convert the train.json file into beautified json file. 
 *       See : "Beautified_Train_Data.json" file
 *
 **/
public class JsonBeauty {

	private static final String filePath = "train.json";
	private static long id;
	private static String created_at, lang, text_en, text_de, text_ru;
	private static JSONArray tweet_urls, tweet_hashtags;
	private static int tweetCounter = 0;

	public static void main(String[] args) {

		try {
			// read the json file
			FileReader reader = new FileReader(filePath);
			File tweetDataFile = new File("Beautified_Train_Data.json");
			FileWriter tweetFile = new FileWriter(tweetDataFile, true);

			tweetFile.write("[");

			JSONParser jsonParser = new JSONParser();
			JSONArray jsonArray = (JSONArray) jsonParser.parse(reader);

			int size = jsonArray.size();
			while (tweetCounter < size) {
				JSONObject jsonObject = (JSONObject) jsonArray.get(tweetCounter);

				id = (long) jsonObject.get("id");
				created_at = (String) jsonObject.get("created_at");
				lang = (String) jsonObject.get("lang");
				text_en = (String) jsonObject.get("text_en");
				text_de = (String) jsonObject.get("text_de");
				text_ru = (String) jsonObject.get("text_ru");
				tweet_urls = (JSONArray) jsonObject.get("tweet_urls");
				tweet_hashtags = (JSONArray) jsonObject.get("tweet_hashtags");

				tweetFile.write("\n    {");
				tweetFile.write("\n        \"id\" : " + id + ",");
				tweetFile.write("\n        \"created_at\" : " + "\"" + created_at + "\",");
				tweetFile.write("\n        \"lang\" : " + "\"" + lang + "\",");

 	                        tweetFile.write("\n        \"text_en\" : " + "\"" + text_en.replace("\n", "dummyTextToCorrectlyParseOriginalFile").replace("\"", "\\\"") + "\",");
				
				tweetFile.write("\n        \"text_de\" : " + "\"" + text_de.replace("\n", "dummyTextToCorrectlyParseOriginalFile").replace("\"", "\\\"") + "\",");

				tweetFile.write("\n        \"text_ru\" : " + "\"" + text_ru.replace("\n", "dummyTextToCorrectlyParseOriginalFile").replace("\"", "\\\"") + "\",");

				tweetFile.write("\n        \"tweet_urls\" : " +  tweet_urls +",");
				tweetFile.write("\n        \"tweet_hashtags\" : "  + tweet_hashtags);
	
				++tweetCounter;

				if(tweetCounter == size) {
    				tweetFile.write("\n    }");
				} else {
				    tweetFile.write("\n    },");
				}
				System.out.println("\n " + tweetCounter + " tweets copied to File...");
			}
			tweetFile.write("\n]");
			tweetFile.flush();
			tweetFile.close();

		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
			System.out.println("\n Exception when copying tweet number " + tweetCounter + " to File");
		} catch (IOException ex) {
			ex.printStackTrace();
			System.out.println("\n Exception when copying tweet number " + tweetCounter + " to File");
		} catch (ParseException ex) {
			ex.printStackTrace();
			System.out.println("\n Exception when copying tweet number " + tweetCounter + " to File");
		} catch (NullPointerException ex) {
			ex.printStackTrace();
			System.out.println("\n Exception when copying tweet number " + tweetCounter + " to File");
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("\n Generic Exception when copying tweet number " + tweetCounter + " to File");
		}

	}

        //unused function
	public static String toUtcDate(String sourceDate) {
		String lv_dateFormateInUTC = ""; // Will hold the final converted date
			
		Date tweetDate = new Date(sourceDate);
		String ISO_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS zzz";
		SimpleDateFormat lv_formatter = new SimpleDateFormat(ISO_FORMAT);
		lv_formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
		lv_dateFormateInUTC = lv_formatter.format(tweetDate);

		return lv_dateFormateInUTC.replace(" UTC", "Z");
	}

	//unused function
	public static ArrayList<String> extractTags(String tweetText) {
		//For ASCII lang
		//Pattern MY_PATTERN = Pattern.compile("#(\\w+|\\W+)");
	    
		//for UTF 8 lang
		Pattern MY_PATTERN = Pattern.compile("(?:^|\\s|[\\p{Punct}&&[^/]])(#[\\p{L}0-9-_]+)");
		Matcher mat = MY_PATTERN.matcher(tweetText);
		ArrayList<String> tags = new ArrayList<String>();
		while (mat.find()) {
			tags.add("\"" + mat.group(1).replace("#", "") + "\"");
		}

		return tags;
	}

	//unused function
	public static ArrayList<String> extractAllUrls(String tweetText) {
		ArrayList<String> containedUrls = new ArrayList<String>();
		String urlRegex = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
		Pattern pattern = Pattern.compile(urlRegex, Pattern.CASE_INSENSITIVE);
		Matcher urlMatcher = pattern.matcher(tweetText);

		while (urlMatcher.find()) {
			containedUrls.add("\"" + tweetText.substring(urlMatcher.start(0), urlMatcher.end(0)) + "\"");
		}

		return containedUrls;
	}

}
