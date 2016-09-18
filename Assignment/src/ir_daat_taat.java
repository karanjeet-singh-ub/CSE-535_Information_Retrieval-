import java.util.*;
import java.io.*;

class index_1_creator {

	private String term;
	private int freq;
	private String term_docs;
	
	index_1_creator(String trm, int tf, String docs) {
	    term=trm;
	    freq=tf;
	    term_docs=docs;
	  }
	public String getterm() {
	    return term;
	  }
	public int getfreq() {
	    return freq;
	  }
	public TreeMap<Integer, Integer> getsorteddocs() {
		String[] doc_with_freq=(term_docs.substring(2,term_docs.length()-1)).split(", ");
		TreeMap<Integer, Integer> docs_with_freq = new TreeMap<Integer, Integer>();
		for (int i=0;i<doc_with_freq.length;i++){
			String[] split_doc = doc_with_freq[i].split("/");
			docs_with_freq.put(Integer.parseInt(split_doc[0]), Integer.parseInt(split_doc[1]));
		}
		
		return docs_with_freq;
	  }
}


class index_2_creator {

	private String term;
	private int freq;
	private String term_docs;
	
	index_2_creator(String trm, int tf, String docs) {
	    term=trm;
	    freq=tf;
	    term_docs=docs;
	  }
	public String getterm() {
	    return term;
	  }
	public int getfreq() {
	    return freq;
	  }
	@SuppressWarnings("unchecked")
	public Map<Integer, Integer> getsorteddocs() {
		String[] doc_with_freq=(term_docs.substring(2,term_docs.length()-1)).split(", ");
		HashMap<Integer, Integer> docs_with_freq = new HashMap<Integer, Integer>();
		for (int i=0;i<doc_with_freq.length;i++){
			String[] split_doc = doc_with_freq[i].split("/");
			docs_with_freq.put(Integer.parseInt(split_doc[0]), Integer.parseInt(split_doc[1]));
		}
		List mapKeys = new ArrayList(docs_with_freq.keySet());
		List mapValues = new ArrayList(docs_with_freq.values());
		Collections.sort(mapValues);
		Collections.sort(mapKeys);
		Collections.reverse(mapValues);
		Collections.reverse(mapKeys);

		LinkedHashMap sortedMap = new LinkedHashMap();

		Iterator valueIt = mapValues.iterator();
		while (valueIt.hasNext()) {
		    Object val = valueIt.next();
		    Iterator keyIt = mapKeys.iterator();

		    while (keyIt.hasNext()) {
		        Object key = keyIt.next();
		        String comp1 = docs_with_freq.get(key).toString();
		        String comp2 = val.toString();

		        if (comp1.equals(comp2)){
		        	docs_with_freq.remove(key);
		            mapKeys.remove(key);
		            sortedMap.put((Integer)key, (Integer)val);
		            break;
		        }

		    }

		}
//		@SuppressWarnings("rawtypes")
//		Map sortedMap = sortByValue(docs_with_freq);
		return sortedMap;
	  }
//	@SuppressWarnings({ "rawtypes", "unchecked" })
//	private Map sortByValue(HashMap<Integer, Integer> docs_with_freq) {
//		Map sortedMap = new TreeMap(new ValueComparator(docs_with_freq));
//		sortedMap.putAll(docs_with_freq);
//		return sortedMap;
//	}
}

class Entity implements Comparable<Entity> {
	Set<Integer> pst1;
    int num2;
    Entity(Set<Integer> pst1, int num2) {
        this.pst1 = pst1;
        this.num2 = num2;
    }
    @Override
    public int compareTo(Entity o) {
        if (this.num2 > o.num2)
            return 1;
        else if (this.num2 < o.num2)
            return -1;
        return 0;
    }
}
public class ir_daat_taat {
	
	public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException{
		String fileName = "";
		String outfile = "";
		int top_terms=0;
		String inpfile = "";
		if(args.length == 0){
			fileName = "/home/karan/Downloads/term.idx"; 
		}
		else{
			fileName = args[0];
		}
		if(args.length == 0){
			outfile = "output.log"; 
		}
		else{
			outfile = args[1];
		}
		if(args.length == 0){
			top_terms=10; 
		}
		else{
			top_terms = Integer.parseInt(args[2]);
		}
		if(args.length == 0){
			inpfile="query_file.txt"; 
		}
		else{
			inpfile = args[3];
		}
		PrintWriter writer = new PrintWriter(outfile, "UTF-8");
		String line = null;
		LinkedList<index_1_creator> ll1 = new LinkedList<index_1_creator>();
		LinkedList<index_2_creator> ll2 = new LinkedList<index_2_creator>();
		TreeMap<String, Integer> term_freq = new TreeMap<String, Integer>();
		try {
            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
            	String[] line_parts=line.split("\\\\");
            	ll1.add(new index_1_creator(line_parts[0], Integer.parseInt(line_parts[1].substring(1)), line_parts[2]));
            	ll2.add(new index_2_creator(line_parts[0], Integer.parseInt(line_parts[1].substring(1)), line_parts[2]));
            	term_freq.put(line_parts[0], Integer.parseInt(line_parts[1].substring(1)));
            }   
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
//		 for(int i=0; i< ll1.size(); i++)
//		    {
//			 System.out.println(i+"th item is: "+ll2.get(i).getsorteddocs());
//		    }
		
		Map<String, Integer> sorted_term_freq = sortByComparator(term_freq);
		String[] terms = new String[term_freq.size()];
		int pos = 0;
		for (String key : term_freq.keySet()) {
		    terms[pos++] = key;
		}
		
		writer.println(getTopK(top_terms, sorted_term_freq));
		try {
            FileReader fileReader = new FileReader(inpfile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while((line = bufferedReader.readLine()) != null) {
            	writer.println(getPostings(line, ll1, ll2, terms));
            }   
            bufferedReader.close();         
        }
        catch(FileNotFoundException ex) {
            System.out.println("Unable to open file '" + fileName + "'");                
        }
        catch(IOException ex) {
            System.out.println("Error reading file '" + fileName + "'");
        }
		writer.close();
	}
	
	public static String getTopK(int no_of_terms, Map<String, Integer> sorted_term_freq){
		int j=0;
		String terms="";
		Set set = sorted_term_freq.entrySet(); 
		Iterator i = set.iterator(); 
		while(j<no_of_terms) { 
			Map.Entry me = (Map.Entry)i.next();
			terms=terms+me.getKey() + ", ";
			j=j+1;
		}
		String result = "FUNCTION: getTopK "+no_of_terms+"\nResult: "+terms.substring(0,terms.length()-2);
		return result;
	}
	
	public static String getPostings(String query_terms, LinkedList<index_1_creator> ll1, LinkedList<index_2_creator> ll2, String[] terms){
		String[] q_terms = query_terms.split(" ");
		String ll1_output = "";
		String result = "";
		ArrayList<Set<Integer>> postings_2_list = new ArrayList<Set<Integer>>();
		ArrayList<Set<Integer>> postings_1_list = new ArrayList<Set<Integer>>();
		Set<Integer> postings_2;
		Set<Integer> postings_1;
		ArrayList<Integer> postings_2_num = new ArrayList<Integer>();
		ArrayList<Integer> postings_1_num = new ArrayList<Integer>();
		for (int i=0;i<q_terms.length;i++){
			result+="FUNCTION: getPostings "+q_terms[i]+"\n";
			int index_of_posting=Arrays.binarySearch(terms, q_terms[i]);
			if (index_of_posting<0){
				result+="term not found\n";
			}
			else{
				postings_1=(((index_1_creator) ll1.get(index_of_posting)).getsorteddocs().keySet());
				ll1_output+=(((index_1_creator) ll1.get(index_of_posting)).getsorteddocs().keySet());
				result+="Ordered by doc IDs: "+ ll1_output.substring(1,ll1_output.length()-1)+"\n";
				ll1_output="";
				postings_2=((index_2_creator) ll2.get(index_of_posting)).getsorteddocs().keySet();
				postings_2_list.add(postings_2);
				postings_1_list.add(postings_1);
				postings_2_num.add(postings_2.size());
				postings_1_num.add(postings_1.size());
				ll1_output+=(postings_2);
				result+="Ordered by TF: "+ ll1_output.substring(1,ll1_output.length()-1)+"\n";
				ll1_output = "";
			}
			
		}
		termAtATimeQueryAnd(postings_2_list, postings_2_num);
		docAtATimeQueryAnd(postings_1_list, postings_1_num);
		return result;
	}
	
	public static void termAtATimeQueryAnd(ArrayList<Set<Integer>> line, ArrayList<Integer> line_no){
		List<Entity> entities = new ArrayList<Entity>();
		for (int j=0;j<line.size();j++){
			entities.add(new Entity(line.get(j), line_no.get(j)));
		}
		Collections.sort(entities);
		if (line_no.size() == 2){
			Set<Integer> common_postings= getcommons(line.get(0), line.get(1));
			System.out.println("Taat and");
			System.out.println(common_postings);
		}
		else{
			Set<Integer> common_postings_1 = getcommons(entities.get(0).pst1, entities.get(1).pst1);
			for (int i=2;i<line_no.size();i++){
				for(Integer j : entities.get(i).pst1){
					if (common_postings_1.contains(j)){
						common_postings_1.add(j);
					}
				}
			}
			System.out.println("Taat and");
			System.out.println(common_postings_1);
			
		}
		termAtATimeQueryOr(entities);
	}
	
	public static Set<Integer> getcommons(Set<Integer> set1, Set<Integer> set2){
		Set<Integer> set3 = new HashSet<Integer>();
		for(Integer j : set1){
			if (set2.contains(j)){
				set3.add(j);
			}
		}
		List<Integer> sorted_list = new ArrayList<Integer>(set3);
		Collections.sort(sorted_list);
		Set<Integer> sorted_set = new HashSet<Integer>(sorted_list);
		return sorted_set;
	}
	
	public static void termAtATimeQueryOr(List<Entity> entities){
		Set<Integer> or_taat_posting = new HashSet<Integer>();
		for (int i=0;i<entities.size();i++){
			for (Integer j: entities.get(i).pst1){
				or_taat_posting.add(j);
			}
		}
		List<Integer> sorted_list = new ArrayList<Integer>(or_taat_posting);
		Collections.sort(sorted_list);
		System.out.println("taat or");
		System.out.println(sorted_list);
	}	
	
	public static void docAtATimeQueryAnd(ArrayList<Set<Integer>> line, ArrayList<Integer> line_no){
		if (line_no.size() == 2){
			Set<Integer> common_postings= getcommons(line.get(0), line.get(1));
			System.out.println("Daat and");
			System.out.println(common_postings);
		}
		else{
			Set<Integer> common_postings_1 = getcommons(line.get(0), line.get(1));
			for (int i=2;i<line_no.size();i++){
				for(Integer j : line.get(i)){
					if (common_postings_1.contains(j)){
						common_postings_1.add(j);
					}
				}
			}
			System.out.println("Daat and");
			System.out.println(common_postings_1);
		}
		docAtATimeQueryOr(line);
	}
	
	public static void docAtATimeQueryOr(ArrayList<Set<Integer>> line){
		Set<Integer> or_daat_posting = new HashSet<Integer>();
		for (int i=0;i<line.size();i++){
			for (Integer j: line.get(i)){
				or_daat_posting.add(j);
			}
		}
		List<Integer> sorted_list = new ArrayList<Integer>(or_daat_posting);
		Collections.sort(sorted_list);
		System.out.println("daat or");
		System.out.println(sorted_list);
	}
	
	private static Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap) {

		// Convert Map to List
		List<Map.Entry<String, Integer>> list = 
			new LinkedList<Map.Entry<String, Integer>>(unsortMap.entrySet());

		// Sort list with comparator, to compare the Map values
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1,
                                           Map.Entry<String, Integer> o2) {
//				return (o1.getValue()).compareTo(o2.getValue());
				return (o2.getValue()).compareTo(o1.getValue());
			}
		});

		// Convert sorted map back to a Map
		Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Iterator<Map.Entry<String, Integer>> it = list.iterator(); it.hasNext();) {
			Map.Entry<String, Integer> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}
	
}

//@SuppressWarnings("rawtypes")
//class ValueComparator implements Comparator {
//	 
//	Map map;
// 
//	public ValueComparator(Map map) {
//		this.map = map;
//	}
// 
//	@SuppressWarnings("unchecked")
//	public int compare(Object keyA, Object keyB) {
//		Comparable valueA = (Comparable) map.get(keyA);
//		Comparable valueB = (Comparable) map.get(keyB);
//		return valueB.compareTo(valueA);
//	}
//}

//points on each one, if null, optimal + normal
