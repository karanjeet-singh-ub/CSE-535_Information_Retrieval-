
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;

public class CSE535Assignment {

    static LinkedList<String> linkedlistAllData = new LinkedList<String>();        //Linkedlist takes the term.idx values
    static LinkedList<String> linkedlistInputData = new LinkedList<String>();      ///Linked List stores the input data

    static LinkedList<String> linkedlistTermFreq = new LinkedList<String>();         //Linked Lost has the Document ID arranged by decreasing term frequency
    static LinkedList<String> linkedlistDocumentID = new LinkedList<String>();       //Linked Lost has the Document ID arranged by Increasing term DocumentID
    
    
    static HashMap<String , classForStorage>  HashmapOfEverything = new HashMap<String , classForStorage>(); // HashMap Stores final mapping of term and Posting List

	static int NumberOFcomparisons = 0;  
	static long startTime;
	static long endTime;
    static String logFile = "";
    static String inputFile = "";
    static int TopTerms = 0 ;
    static String queryFile = "";
    static PrintWriter logger ;
    
    
	public static void main(String[] args) throws IOException , FileNotFoundException {
	
		
		
//		// handling inputs from command prompt
		if(args.length == 0){
			System.out.println("Enter the input file name");
		}else{
			inputFile = args[0];
		}
		if(args.length == 0){
			System.out.println("Enter the log output file name");
		}else{
			logFile = args[1];
		}
		if(args.length == 0){
			TopTerms=10; 
		}else{
			TopTerms = Integer.parseInt(args[2]);
		}
		if(args.length == 0){
			System.out.println("Enter the input query file"); 
		}else{
			queryFile = args[3];
		}
		
		 

		//Step 1  Read from IDX file////////////
		
		ReadFromIDXFile read = new ReadFromIDXFile();
		linkedlistAllData = read.readFromFile(inputFile);    // read all data from IDX file  
		
		 //Step 2  Read from input file////////////

		ReadInputFile readInput = new ReadInputFile();
		linkedlistInputData = readInput.readFromFile(queryFile);    // read all data from input file 
		
		logger = new PrintWriter(logFile, "UTF-8");

		
		
		 //Step 3  Create Hashmap of Term and Posting List ////////////
		createDataForBothLinkedList(linkedlistAllData);
		
		
		
		 //Step 4  check for queries in each line and execute function accordingly ////////////
		GetTopK(TopTerms , linkedlistAllData);
		
		
		for(int i = 0 ; i < linkedlistInputData.size() ; i++){    // for every line of input query terms file
			String[] split  =   linkedlistInputData.get(i).split(" ");
			for (int j = 0 ; j < split.length ; j++){              // for every single query terms of each line
				GetPostingList(split[j]);
			}
			termAtATimeQueryAnd(linkedlistInputData.get(i));
			termAtATimeQueryOr(linkedlistInputData.get(i));
			docAtATimeQueryAnd(linkedlistInputData.get(i));
			//docAtATimeQueryOr(linkedlistInputData.get(i));
		}
				
		logger.close();

   }
	
		
	
	/****************************************** Get Top K ****************************************/
	
		
	public static void GetTopK(int NumberOfTerms ,LinkedList<String> TopTerms ){
		
	     LinkedList<String> Templinkedlist = new LinkedList<String>(TopTerms);		
		
		Collections.sort(Templinkedlist, new ByTerm()); // Sort the linked List by the Posting List Size

		// After sorting get the Top K Terms from the Linked List
		String Temp ="";
		for(int i = 0 ; i < NumberOfTerms ; i++){
			 String[] tempA = Templinkedlist.get(i).split("\\\\");
             Temp = Temp + tempA[0];
             if(i<NumberOfTerms-1){
            	 Temp += ",";
             }
		}
		
		String Log = "FUNCTION: getTopK" + NumberOfTerms + "\nResult: " + Temp;
		logger.println(Log);
		
	}
	
	
	
	/***************************************************** getPostings ********************************************/
	

	
	public static void GetPostingList(String QueryTerm  ){
        
		  ////////////////////// check if terms are present//////////////////////////
       
        	if(HashmapOfEverything.get(QueryTerm) == null){
        		String temp = "FUNCTION: getPostings : terms not found";
        		logger.println(temp);
        		return;
        	}
        
		
	    classForStorage cQuery = HashmapOfEverything.get(QueryTerm);
	    String tempDOC = "";
	    for(int i = 0 ; i < cQuery.linkedlistDocumentID.size() ; i++){
	    	tempDOC = tempDOC + cQuery.linkedlistDocumentID.get(i);
	    	if(i<cQuery.linkedlistTermFreq.size()-1){
	    		tempDOC += ",";
            }
	    }
	    
	    String tempFreq = "";
	    for(int i = 0 ; i < cQuery.linkedlistTermFreq.size() ; i++){
	    	tempFreq = tempFreq + cQuery.linkedlistTermFreq.get(i) ;
	    	if(i<cQuery.linkedlistTermFreq.size()-1){
	    		tempFreq += ",";
            }
	    	
	    }
	    
		String Log = "FUNCTION: getPostings " + QueryTerm + "\nOrdered by doc IDs:  " + tempDOC + "\nOrdered by TF:  " + tempFreq;
		logger.println(Log);
	}

	
	
	/************************** termAtATimeQueryAnd *******************************************************/
	
	// This emulates an evaluation of a multi-term
	//Boolean AND query on the index with term-at-a-time query. Note here the number of query terms could be
	//varied. The index ordered by decreasing term frequencies .
	// One linked list is kept as reference and stored into temporary LinkedList and compared with other and the result of both is again stored into Linkedlist
	// Process is repeated until all terms are checked
	
	public static void termAtATimeQueryAnd(String  query){
		startTime =  (int)System.currentTimeMillis() ;
        NumberOFcomparisons = 0;
		String[] newQuery = query.split(" "); // split input line into  terms into array
		
        if(newQuery.length<1){
           return ;
        }
        
        ////////////////////// check if terms are present//////////////////////////
        for(int i = 0 ; i<newQuery.length ; i++){
        	if(HashmapOfEverything.get(newQuery[i]) == null){
        		String temp = "FUNCTION: termAtATimeQueryAnd : terms not found";
        		logger.println(temp);
        		return;
        	}
        }
        
        // take out our posting list for the given query term from the Hashmap
		classForStorage cQuery = HashmapOfEverything.get(newQuery[0]);
		// Recursive list contains the final value of TAAT ANd Result
		LinkedList<Integer> RecursiveLinkedList = new LinkedList<Integer>(cQuery.linkedlistTermFreq); 

		if(newQuery.length<2){
			// do nothing
		}
		
		else{
			if(newQuery.length>=2){
				for (int i = 1; i < newQuery.length; i++){
				RecursiveLinkedList = TAATAND(RecursiveLinkedList , newQuery[i]);        // call the function of TAAT AND until all terms are checked      
			}
		  }		
	    }
		
        Collections.sort(RecursiveLinkedList);             // sort the Intersected LinkedList

		
		//Make the string for log Output for this termAtATimeQueryAnd///////////////////////////////
        String TermAtTimeAnd = "";
        TermAtTimeAnd =  "FUNCTION: termAtATimeQueryAnd ";
        for(int i = 0 ; i < newQuery.length;i++){
        	TermAtTimeAnd = TermAtTimeAnd +newQuery[i];
        	if(i < newQuery.length-1){
            	TermAtTimeAnd = TermAtTimeAnd + " ,";
            	}
        }
        TermAtTimeAnd = TermAtTimeAnd +"\n";
        TermAtTimeAnd = TermAtTimeAnd + RecursiveLinkedList.size()  +" documents are found \n";
        TermAtTimeAnd = TermAtTimeAnd + NumberOFcomparisons  +" comparisons are made \n";
        endTime =(int) System.currentTimeMillis();
        float Duration =  (float) ((endTime - startTime)/1000.0);
        TermAtTimeAnd = TermAtTimeAnd + Duration + " seconds are used \n";
        TermAtTimeAnd = TermAtTimeAnd + "Result: " ;
        for(int i = 0 ; i < RecursiveLinkedList.size(); i++){
        	TermAtTimeAnd = TermAtTimeAnd +RecursiveLinkedList.get(i);
        	if(i < RecursiveLinkedList.size()-1){
        	TermAtTimeAnd = TermAtTimeAnd + " ,";
        	}
        }
        ////////////////////////////////////////////////////////////////////////////////////////
        
        // then log the output to an output.log file
        logger.println(TermAtTimeAnd);

}
	

	//   call this function for TAAT 
	public static LinkedList<Integer> TAATAND(LinkedList<Integer> temp , String second){
		LinkedList <Integer> TempLinkedList = new LinkedList<Integer>();

		
		classForStorage secondTerm = HashmapOfEverything.get(second);
		for(int i = 0 ; i < temp.size() ; i++){
			for(int j = 0 ; j < secondTerm.linkedlistTermFreq.size() ; j++){
                NumberOFcomparisons++;
                 if( temp.get(i).equals(secondTerm.linkedlistTermFreq.get(j))){
                	 TempLinkedList.add(temp.get(i));
                	 break;
                	 }
                 }
		    }
		return TempLinkedList;

	}
	
	
	
	/*************************************************** termAtATimeQueryOr *******************************************/
	
	// the operations takes o(xy) operations where x and y are posting list
	
	public static void termAtATimeQueryOr(String  query){
        startTime = (int)System.currentTimeMillis();
        NumberOFcomparisons = 0;

		String[] newQuery = query.split(" "); // split input line into  terms into array
		
        if(newQuery.length<1){
           return ;
        }
        
        ////////////////////// check if terms are present//////////////////////////
        for(int i = 0 ; i<newQuery.length ; i++){
        	if(HashmapOfEverything.get(newQuery[i]) == null){
        		String temp = "FUNCTION: termAtATimeQueryOR : terms not found";
        		logger.println(temp);
        		return;
        	}
        }
        
        // take out our posting list for the given query term from the Hashmap
		classForStorage cQuery = HashmapOfEverything.get(newQuery[0]);
		
		LinkedList<Integer> RecursiveLinkedList = new LinkedList<Integer>(cQuery.linkedlistTermFreq);
		if(newQuery.length<2){
			// do nothing
		}
		else{
			
			if(newQuery.length>=2){

				for (int i = 1; i < newQuery.length; i++){
				RecursiveLinkedList = TAATOR(RecursiveLinkedList , newQuery[i]);
			}
		  }		
	    }
		
        Collections.sort(RecursiveLinkedList);
		
		// make the final string for printing to log file for termAtATimeQueryOr///////////////////////////
		String TermAtTimeOR = "";
		TermAtTimeOR =  "FUNCTION: termAtATimeQueryOR ";
        for(int i = 0 ; i < newQuery.length;i++){
        	TermAtTimeOR = TermAtTimeOR +newQuery[i];
        	if(i < newQuery.length-1){
        		TermAtTimeOR = TermAtTimeOR + " ,";
            	}
        }
        TermAtTimeOR = TermAtTimeOR +"\n";
        TermAtTimeOR = TermAtTimeOR + RecursiveLinkedList.size()  +" documents are found \n";
        TermAtTimeOR = TermAtTimeOR + NumberOFcomparisons  +" comparisons are made \n";
        endTime =(int) System.currentTimeMillis();
        float Duration = (float) ((endTime - startTime)/1000.0);
        TermAtTimeOR = TermAtTimeOR + Duration + " seconds are used \n";
        TermAtTimeOR = TermAtTimeOR + "Result: " ;
        for(int i = 0 ; i < RecursiveLinkedList.size(); i++){
        	TermAtTimeOR = TermAtTimeOR +RecursiveLinkedList.get(i) ;
        	if(i < RecursiveLinkedList.size()-1){
        		TermAtTimeOR = TermAtTimeOR + " ,";
            	}
        }
        
        //////////////////////////////////////////////////////////////////////////////
                
       // Print the result into the log file
        
        logger.println(TermAtTimeOR);

		
	}
	
	//   call this function for TAAT until all terms are checked
    
		public static LinkedList<Integer> TAATOR(LinkedList<Integer> temp , String second){
			LinkedList <Integer> TempLinkedList = new LinkedList<Integer>();
			classForStorage secondTerm = HashmapOfEverything.get(second);
			
			for(int i = 0 ; i < temp.size() ; i++){
				TempLinkedList.add(temp.get(i));   // Put Everything in one Linked list and then compare it with others
			}
			
			for(int i = 0 ; i < temp.size() ; i++){
				for(int j = 0 ; j < secondTerm.linkedlistTermFreq.size() ; j++){
	                NumberOFcomparisons++;                                                       // maintain the number of comparison counter
	                 if( temp.get(i).equals(secondTerm.linkedlistTermFreq.get(j))){            
	                       	 }
	                      else{                                                                                      // If elements are not equal then add to the list
	                	       if(!elementExists(TempLinkedList , secondTerm.linkedlistTermFreq.get(j))){       // but also check if the element is already present
	     				         TempLinkedList.add(secondTerm.linkedlistTermFreq.get(j));                      // if already not present then add to our final result
	                	       }
	                	   }
	                  }
	               }
			return TempLinkedList;
		}
		
	
	public static boolean elementExists(LinkedList<Integer> Temp , Integer element){   // Check if the elements exists
		Boolean exists = false;
		for (int p = 0 ; p <Temp.size() ; p++){
		     if(Temp.get(p).equals(element)){
		    	 exists = true;
		     }
	     }		
		return exists;
	}
		
		
	
	/************************** docAtATimeQueryAnd  ********************************************************************/
	
	//If the lengths of the postings lists are x and y, the intersection takes O(x + y) operations. 
	//In this case we have to keep track of concurrent pointer for each linkedList and follow the Algorithm
  //	INTERSECT(p1, p2) 1 answer←⟨⟩
  //	while p1 ̸= NIL and p2 ̸= NIL
  //do if docID(p1) = docID(p2)
  //	then ADD(answer, docID(p1)) p1 ← next(p1)
  //	p2 ← next(p2)
  //	else if docID(p1) < docID(p2)
  //	then p1 ← next(p1
  //	
		public static void docAtATimeQueryAnd(String  query){
			   startTime = (int)System.currentTimeMillis() ;
		       NumberOFcomparisons = 0;
			   String[] newQuery = query.split(" "); // split input line into  terms into array
				
		        if(newQuery.length<1){        // if there is no input at all then return
					    return ;
		        }
		        
		        ////////////////////// check if terms are present//////////////////////////
		        for(int i = 0 ; i<newQuery.length ; i++){
		        	if(HashmapOfEverything.get(newQuery[i]) == null){
		        		String temp = "FUNCTION: DOCAtATimeQueryAnd : terms not found";
		        		logger.println(temp);
		        		return;
		        	}
		        }
		        
				if(newQuery.length<2){    // If query terms less than 2 then no documents found
					// do nothing
				}
				
				else{
					LinkedList<Integer> RecursiveList = new LinkedList<Integer>();
			        ArrayList<LinkedList<Integer>> array = new ArrayList<LinkedList<Integer>>();

					for(int i = 0 ; i < newQuery.length ; i++){
						classForStorage cQuery = HashmapOfEverything.get(newQuery[i]);
						LinkedList<Integer> RecursiveListName = new LinkedList<Integer>(cQuery.linkedlistDocumentID);
						array.add(RecursiveListName);
					}
		           
					RecursiveList = DAATAND(array);
					//System.out.println(RecursiveList.size());
					
					
					// Make the String for Docatatime Query And for Log File///////////////////
					String DoctAtATimeAnd = "";
					DoctAtATimeAnd =  "FUNCTION: docAtATimeQueryAnd ";
			        for(int i = 0 ; i < newQuery.length;i++){
			        	DoctAtATimeAnd = DoctAtATimeAnd +newQuery[i];
			        	if(i < newQuery.length-1){
			        		DoctAtATimeAnd = DoctAtATimeAnd + " ,";
			            	}
			        }
			        DoctAtATimeAnd = DoctAtATimeAnd +"\n";
			        DoctAtATimeAnd = DoctAtATimeAnd + RecursiveList.size()  +" documents are found \n";
			        DoctAtATimeAnd = DoctAtATimeAnd + NumberOFcomparisons  +" comparisons are made \n";
			        endTime = (int)System.currentTimeMillis();
			        float Duration = (float) ((endTime - startTime)/1000.0);
			        DoctAtATimeAnd = DoctAtATimeAnd + Duration + " seconds are used \n";
			        DoctAtATimeAnd = DoctAtATimeAnd + "Result: " ;
			        for(int i = 0 ; i < RecursiveList.size(); i++){
			        	DoctAtATimeAnd = DoctAtATimeAnd +RecursiveList.get(i) ;
			        	if(i < RecursiveList.size()-1){
			        		DoctAtATimeAnd = DoctAtATimeAnd + " ,";
			            	}
			        	
			        	}
			
			        logger.println(DoctAtATimeAnd);
			  }
		}
		
		
		//call this function for DAAT AND
		public static LinkedList<Integer> DAATAND(ArrayList<LinkedList<Integer>> array){
			LinkedList<Integer> IntersectedLinkedList = new LinkedList<Integer>();
			LinkedList<Integer> referenceLinkedList = new LinkedList<Integer>();
			
			NumberOFcomparisons = 0 ;
             
			int x=0; // Keeps track of the counter of the reference linked List
			
			referenceLinkedList = array.get(0);// take the reference List
			
			int y=1; 
			int p=0 ;
			int q=0;
			
			if (array.size()>=2){
				while(x<referenceLinkedList.size()){       // While Loop for reference linked List
					y=1;p=0;q=0;
					while(y<array.size() && p<array.get(y).size()){
						if (referenceLinkedList.get(x).intValue() == array.get(y).get(p).intValue()){
							q++;y++;p=0;
						}
						else if(referenceLinkedList.get(x).intValue() > array.get(y).get(p).intValue()){
							p++;
						}
						else{
							y++;
						}
						NumberOFcomparisons++;
					}
					if (q==array.size()-1){
						IntersectedLinkedList.add(referenceLinkedList.get(x));            // Add elements into the Intersected Linkedlist if all match
					}
					x++;
				}
			}
			
			return IntersectedLinkedList;			// returns the Intersected Linked List			
		}
		
		
	
	/********************************************* docAtATimeQueryOr **********************************************************/
		
		public static void docAtATimeQueryOr(String  query){
			   startTime = System.currentTimeMillis() /1000;
		       NumberOFcomparisons = 0;
		        
				String[] newQuery = query.split(" "); // split input line into  terms into array
		        if(newQuery.length<1){
		           return ;
		        }
				
		        ////////////////////// check if terms are present//////////////////////////
		        for(int i = 0 ; i<newQuery.length ; i++){
		        	if(HashmapOfEverything.get(newQuery[i]) == null){
		        		String temp = "FUNCTION: DOCAtATimeQueryAnd : terms not found";
		        		logger.println(temp);
		        		return;
		        	}
		        }
		        

				if(newQuery.length<2){
					// do nothing
				}
				
				else{
					LinkedList<Integer> RecursiveList = new LinkedList<Integer>();
			        ArrayList<LinkedList<Integer>> array = new ArrayList<LinkedList<Integer>>();
					for(int i = 0 ; i < newQuery.length ; i++){
						classForStorage cQuery = HashmapOfEverything.get(newQuery[i]);		
						LinkedList<Integer> RecursiveListName = new LinkedList<Integer>(cQuery.linkedlistDocumentID);
						array.add(RecursiveListName);
					}   
					RecursiveList = DAATOR(array);
					System.out.println(RecursiveList.size());
			  }
		}
		
		
		
		//call this function for DAAT OR
		public static LinkedList<Integer> DAATOR(ArrayList<LinkedList<Integer>> array){
			LinkedList<Integer> UnionLinkedList = new LinkedList<Integer>();
			//LinkedList<Integer> TempLinkedList = new LinkedList<Integer>();
			
		
			return UnionLinkedList;		   // Returns the Union List for DAAT OR		
		}
		
		

	
	
	
	
	/*******************************************finish of 6 function here *******************************************************/
		
		
	
	
	
	
	public static void createDataForBothLinkedList(LinkedList<String> allData){
		
		for (int i = 0;i< allData.size(); i++){
			String Temp =  allData.get(i);
			createHashMapOfData(Temp);
		}
	}
	
	
	
	// This method create our Hashmap for Query terms and Posting List
	// Final Hashmap which contains query term as the key and value as the Custom Object (Class for storage)
	public static void createHashMapOfData(String eachLine){
		
		classForStorage cc = new classForStorage();
		
		 String[] tempA = eachLine.split("\\[");         // remove [ so that we need only posting list
		 tempA[1] = tempA[1].split("\\]")[0];        // remove ] as well from last

		 String[] tempB = tempA[1].split(",");
		 
		 for (String postingList : tempB){
			 String[] tempC = postingList.split("/");
			 String tempFirst = tempC[0].replaceAll("\\s+", "");
			 int termFreq =  Integer.parseInt(tempC[1]);
			 int DocID = Integer.parseInt(tempFirst);
			 //cc.linkedlistTermFreq.add(termFreq);
			 cc.linkedlistDocumentID.add(DocID);
             cc.linkedlistDocumentbyTermFreq.add(DocID+"/"+termFreq);
		 }
		 String[] temp = eachLine.split("\\\\");
		 String[] part = temp[1].split("(?<=\\D)(?=\\d)");
	     cc.postingSize= Integer.parseInt(part[1]);
	 		 
		 Collections.sort(cc.linkedlistDocumentID, new Comparator<Integer>(){
			   @Override
			   public int compare(Integer o1, Integer o2){
			        if(o1 < o2){
			           return -1; 
			        }
			        if(o1 > o2){
			           return 1; 
			        }
			        return 0;
			   }
			});
		 
		 Collections.sort(cc.linkedlistDocumentbyTermFreq, new Comparator<String>(){
			   @Override
			   public int compare(String o1, String o2){
				   int temp1 =Integer.parseInt(o1.split("/")[1]);
				   int temp2 =Integer.parseInt(o2.split("/")[1]);

			        if(temp1 < temp2){
			           return 1; 
			        }
			        if(temp1 > temp2){
			           return -1; 
			        }
			        return 0;
			   }
			});
		 
		 for(int i = 0; i < cc.linkedlistDocumentbyTermFreq.size() ; i ++){
			 int temp1 =Integer.parseInt(cc.linkedlistDocumentbyTermFreq.get(i).split("/")[0]);
			 cc.linkedlistTermFreq.add(temp1);
		 }		 

		 HashmapOfEverything.put(temp[0], cc);           // Final Hashmap which contains query term as the key and value as the Custom Object (Class for storage)
	}
	

	
	
	
	
	  
	  public static int findQueryIndex(String queryTerm){
		  
		  int indexofQuery = 0;
			for(int i = 0 ; i < linkedlistTermFreq.size() ; i++){
				 String[] tempA = linkedlistTermFreq.get(i).split("\\\\");
	              if(queryTerm.equals(tempA[0])){
	            	  indexofQuery = i;
	              }
			}
		return indexofQuery;
		  
	  }

	  
	  // This method is used to write logs to the Log File
	  public static void writeToLog(String inString)
	  {
		  File f = new File("yourFile.txt");
		    boolean existsFlag = f.exists();

		    if(!existsFlag)
		    {
		        try {
		            f.createNewFile();
		        } catch (IOException e) {
		            System.out.println("could not create new log file");
		            e.printStackTrace();
		        }

		    }

		    FileWriter fstream;
		    try {
		        fstream = new FileWriter(f, true);
		         BufferedWriter out = new BufferedWriter(fstream);
		         out.write(inString+"\n");
		         out.newLine();
		         out.close();
		    } catch (IOException e) {
		        System.out.println("could not write to the file");
		        e.printStackTrace();
		    } 

		    return;
	  }
	  
	  
}



// this class object acts as values for the Hashmap of and key is the Query Terms
class classForStorage{
	
	  LinkedList<Integer> linkedlistTermFreq = new LinkedList<Integer>();
	  LinkedList<Integer> linkedlistDocumentID = new LinkedList<Integer>();
	  LinkedList<String> linkedlistDocumentbyTermFreq = new LinkedList<String>();
	  int postingSize;
}


// This class is used to sort the LinkList
class arrangeList implements Comparator<LinkedList<Integer>>{
	@Override
	public int compare(LinkedList<Integer> o1, LinkedList<Integer> o2) {
		 if(o1.get(0) < o2.get(0)){
	            return -1;
	        } else {
	            return 1;
	        }
	}
}


//This class reads all the input from the IDX file and puts it into a linked List
class ReadFromIDXFile {    

	public LinkedList<String> readFromFile(String fileString){      

	    LinkedList<String> linkedlistAllData = new LinkedList<String>();

        try {
			File file = new File(fileString);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				linkedlistAllData.add(line);
				stringBuffer.append(line);
				stringBuffer.append("\n");
			}
			fileReader.close();

			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return linkedlistAllData;
    }
}




//this class is read the Input file which has the query Terms

class ReadInputFile {    

	public LinkedList<String> readFromFile(String QueryFile){
		
		LinkedList<String> linkedInputData= new LinkedList<String>();

        try {
			File file = new File(QueryFile);
			FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			StringBuffer stringBuffer = new StringBuffer();
			String line;
			while ((line = bufferedReader.readLine()) != null) {
				linkedInputData.add(line);
				stringBuffer.append(line);
				stringBuffer.append("\n");
			}
			fileReader.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return linkedInputData;  // returns the linked list containing all the input Query terms
    }
}




class ByTerm implements Comparator<String> {
 public int compare(String a, String b) {
	 
	 int topA = termFreq(a);
	 int topB = termFreq(b);
	 
	 if(topA < topB){
		 return 1;
	 }
	 else if (topA > topB){
		 return -1;
	 }
 else {
	 return 0;
 }
	 
  }
 
 public int termFreq(String t){
	 String[] tempA = t.split("\\\\");
	 String[] part = tempA[1].split("(?<=\\D)(?=\\d)");
     return Integer.parseInt(part[1]);
	  }
 
}



