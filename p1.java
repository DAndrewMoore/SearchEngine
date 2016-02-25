/*
 * Name: Daniel Moore
 * Class: 4930.002 - Information Retrieval
 * Date: 2/4/16
 * Description: Creates a dictionary of terms and their term frequencies. Displays number of terms parsed, vocabulary size, top 20 words,
 * and words that account for 15% of the words encountered.
 */

import java.io.*;
import java.util.*;

class p1{
	
	static int numWords = 0;
	
	public static String[] tokenizer(String doc){
		doc = doc.replaceAll("\'","");
		doc = doc.replaceAll("<[^>]*", "");
		doc = doc.replaceAll("\\p{Punct}", " ");
		String[] tDoc = doc.split("[\\s+\\n+]");
		return tDoc;
	}
	
	public static ArrayList<String> loadStopWords() throws FileNotFoundException{
		Scanner blah = new Scanner(new File("C:\\Users\\Andrew\\Desktop\\4930.002\\stopwords.txt"));
		ArrayList<String> stopWords = new ArrayList<String>();
		
		while(blah.hasNext())
			stopWords.add(blah.next());
		
		blah.close();
		
		return stopWords;
	}
	
	public static HashMap<String, ArrayList<Node>> parseFiles(File folder, ArrayList<String> stopWords) throws FileNotFoundException{
		File[] listOfFiles = folder.listFiles(); //get the files in the directory
		HashMap<String, ArrayList<Node>> docMap = new HashMap<String, ArrayList<Node>>();
		Porter stem = new Porter();
		
		for(File file : listOfFiles){ //parse through the files
			
			if(file != null && file.isFile()){ //makes sure it is a file
				Scanner blah = new Scanner(file); //set up object to read the file
				ArrayList<Node> tList = new ArrayList<Node>(); //create arraylist of t->tf nodes
				while(blah.hasNext()){ //parse the file
					
					String doc = blah.next(); //get the next file (auto-ignores whitespace)
					String[] tDoc = tokenizer(doc); //if our token was actually two or more tokens
					
					for(String meh : tDoc){ //parse through the retrieved token(s)
						if(meh.length() >= 1){
							meh = stem.stripAffixes(meh); //this may need to be put inside the if statement
							if(stopWords.indexOf(meh) == -1){
								boolean maybe = false; //our array has no elements or we couldn't find the term
								
								for(int i=0; i<tList.size() && !maybe; i++) //parse through nodes to search for term
									if(tList.get(i).getToken().compareToIgnoreCase(meh) == 0){ //if we found it
										Node tNode = tList.get(i); //get the term's node
										tNode.incrementTf(); //increment the frequency by 1
										tList.set(i, tNode); //replace that node in the list
										maybe = true; //we found it we can exit the loop
									}
								
								if(!maybe){ //if we didn't find it or if there are no elements
									Node tNode = new Node(meh, (double) 1); //create a new node with that term and tf = 1
									tList.add(tNode); //put it at the back
								}
							}
							numWords++; //increase the number of words parsed
							//System.out.print(meh+" ");
						}
					} //end token array parser
					//System.out.println();
				} //end word parser for certain file
				
				//start tf calculations
				double max = getMax(tList); //get the max tf from the list
				tList = modifyTF(tList, max); //modify all nodes with respect to the max
				docMap.put(file.getName(), tList); //this attaches the node list to the filename
				
				blah.close();
			} //end check if file
		} //end directory parser
		//docMap should contain all documents mapped to their term frequency node lists
		
		return docMap;
	}

	public static HashMap<String, HashSet<String>> createInvIndex(HashMap<String, ArrayList<Node>> docMap){
		HashMap<String, HashSet<String>> invIndex = new HashMap<String, HashSet<String>>();
		Set<String> dKeys = docMap.keySet();
		Iterator<String> i = dKeys.iterator();
		while(i.hasNext()){ //parse all file names
			String docName = i.next(); //get next document name and corresponding term list
			ArrayList<Node> tList = docMap.get(docName); //get correpsonding term list
			for(int j = 0; j<tList.size(); j++){ //for each term in list
				Node tNode = tList.get(j); //get the term node
				HashSet<String> tDocSet = new HashSet<String>(); //create a new hashset
				if(invIndex.containsKey(tNode.getToken())) //if the term already has a doc list
					tDocSet = invIndex.get(tNode.getToken()); //get that instead
				tDocSet.add(docName); //add the docname to the docset (auto-ignore duplicates)
				invIndex.put(tNode.getToken(), tDocSet); //attach the updated docset to the term
			}
		} //end file name parser, all terms should have corresponding document sets
		
		return invIndex;
	}
	
	private static HashMap<String, ArrayList<Node>> updateTF(HashMap<String, ArrayList<Node>> docMap, HashMap<String, HashSet<String>> invIndex) {
		int corpusSize = docMap.size(); //N
		
		Set<String> s = docMap.keySet();
		Iterator<String> i = s.iterator();
		while(i.hasNext()){
			String fileName = i.next();
			ArrayList<Node> tList = docMap.get(fileName); //get term list
			for(int j=0; j<tList.size(); j++){ //parse list
				Node tNode = tList.get(j); //specific term 'j'
				double docsThatMentionTerm = invIndex.get(tNode.getToken()).size(); //get size of list of docs that have term j in them
				double tfidf = Math.log(corpusSize/docsThatMentionTerm); //log( N / {d in D | j in d} )
				tNode.setTF(tfidf); //update the node's tf to tfidf
				tList.set(j, tNode); //set the node back in place with updated information
			} //end term list
			docMap.put(fileName, tList); //update docMap with updated term list
		} //end doc list
		
		return docMap;
	}
	
	public static ArrayList<Node> modifyTF(ArrayList<Node> tList, double max) {
		for(int i=0; i<tList.size(); i++){
			Node tNode = tList.get(i);
			tNode.setTF(tNode.getTermFrequency() / max);
			tList.set(i, tNode);
		}
		return tList;
	}

	public static double getMax(ArrayList<Node> tList) {
		double theMax = 0;
		for(int i=0; i<tList.size(); i++)
			if(tList.get(i).getTermFrequency() > theMax)
				theMax = tList.get(i).getTermFrequency();
		return theMax;
	}

	public static ArrayList<Node> nodify(HashMap<String, Integer> dictionary){
		Set<String> sset = dictionary.keySet(); //get the tokens found
		Iterator<String> it = sset.iterator(); //create an iterator over those words
		ArrayList<Node> dict = new ArrayList<Node>();
		while(it.hasNext()){ //parse through the words
			String word = it.next(); //get the next word
			Node temp = new Node(word, (double) dictionary.get(word)); //create a node for that word and it's term frequency
			dict.add(temp); //add the node to our arraylist
		}
		
		Collections.sort(dict); //sort the arraylist
		
		return dict;
	}
	
	public static void writeResults(ArrayList<Node> dict) throws FileNotFoundException{
		//print the results
		PrintWriter pw = new PrintWriter(new File("C:\\Users\\Andrew\\Desktop\\4930.002\\results.txt"));
		for(int i=0; i<dict.size(); i++)
			pw.write(dict.get(i).getToken()+" - "+dict.get(i).getTermFrequency()+"\n");
		pw.close();
		//end printing results
	}
	
	public static void learnedToday(ArrayList<Node> dict){
		//Tell us what we learned today!
		System.out.println("Total words encountered => "+numWords);
		System.out.println("Total vocabulary size => "+dict.size());
		System.out.println("\nTop 20 words");
		for(int i=dict.size()-1, a=1; i>dict.size()-21; i--, a++)
			System.out.println(a+" "+dict.get(i).getToken()+" "+dict.get(i).getTermFrequency());
		
		System.out.println("\nWords that account for 15% of the number parsed");
		int minMax = (int) (0.15*numWords);
		int total = 0;
		for(int i=dict.size()-1; ;i--){
			total += dict.get(i).getTermFrequency();
			System.out.println(dict.get(i).getToken());
			if(total > minMax){
				break;
			}
		}
	}

	public static void main(String args[]) throws IOException{
		File folder = new File("C:\\Users\\Andrew\\Desktop\\4930.002\\cranfieldDocs"); //directed to document directory
		ArrayList<String> stopWords = loadStopWords();
		/* Create document mapping to term frequencies */
		HashMap<String, ArrayList<Node>> docMap = parseFiles(folder, stopWords); //map of documents to list of nodes with corresponding frequencies altered by max tf in doc d_i
		/* Create inverse index from docMap */
		HashMap<String, HashSet<String>> invIndex = createInvIndex(docMap);
		/* Update docMap tf to tf-idf */
		docMap = updateTF(docMap, invIndex);
		/* Read Relevance List */
		Scanner relevance = new Scanner(new File("C:\\Users\\Andrew\\Desktop\\4930.002\\relevance.txt"));
		HashMap<Integer, ArrayList<String>> relevanceList = new HashMap<Integer, ArrayList<String>>();
		while(relevance.hasNextLine()){
			int qNum = relevance.nextInt();
			ArrayList<String> tList = new ArrayList<String>();
			if(relevanceList.containsKey(qNum))
				tList = relevanceList.get(qNum);
			tList.add(relevance.next());
			relevanceList.put(qNum, tList);
		}
		relevance.close();
		
		/* Read query */
		Scanner queries = new Scanner(new File("C:\\Users\\Andrew\\Desktop\\4930.002\\queries.txt"));
		int queryNum = 0;
		while(queries.hasNextLine()){
			queryNum++;
			/* Create tf vector of query */
			Porter stem = new Porter(); //Porter stemmer instantiation
			String query = queries.nextLine(); //get the query
			String[] tokens = tokenizer(query); //tokenize it
			ArrayList<Node> tokenList = new ArrayList<Node>(); //token list of tokens
			for(String token : tokens){ //for each token our tokenizer returned
				if(token.length() > 0){
					token = stem.stripAffixes(token); //strip the affixes
					if(stopWords.indexOf(token) == -1){ //check if stopword
						boolean maybe = false; //if not found
						for(int i=0; i<tokenList.size() && !maybe; i++){ //parse token list for already found tokens
							if(tokenList.get(i).getToken().compareToIgnoreCase(token) == 0){ //we found it
								Node superTempNode = tokenList.get(i); //get that token's node
								superTempNode.incrementTf(); //increment it
								tokenList.set(i, superTempNode); //set the updated node back in place
								maybe=true; //we found it
							} //end if we didn't find it
						} //end for we didn't find it or we did, idk
						if(!maybe){ //we didn't find it
							Node superTempNode = new Node(token, 1.0); //make a new node
							tokenList.add(superTempNode); //put it at the back
						} //end we didn't find it but now its there
					} //end stopword check
				} 
			} //end token array
			//update term frequencies for query
			double theMax = getMax(tokenList); //get the max term frequency
			for(int i=0; i<tokenList.size(); i++){ //parse through token list
				Node tNode = tokenList.get(i); //get each node 
				tNode.setTF(tNode.getTermFrequency() / theMax); //update tf
				tokenList.set(i, tNode); //set the updated node to the place where the old node was
			} //end token list parse
			//tokenList contains the list of tokens from the query
			
			/* Combine docs from terms matching in invIndex */
			HashSet<String> docList = new HashSet<String>(); //document list of all documents containing query tokens
			for(int i=0; i<tokenList.size(); i++){ //parse through query
				Node tNode = tokenList.get(i); //get each token node
				if(invIndex.containsKey(tNode.getToken())){ //look before we leap (null pointer exceptions are pain)
					HashSet<String> termDocList = invIndex.get(tNode.getToken()); //get the hashset of documents on that token
					docList.addAll(termDocList); //union the sets
				} //end key check
			} //end query parse
			//docList contains the documents that contain terms from the tokenList
			
			/* Cosine similarity function */
			Iterator<String> dI = docList.iterator();
			List<Node> outcomes = new ArrayList<Node>();
			double numerator = 0, ais = 0, bis = 0; //top summation, query token tf, document token tfidf
			while(dI.hasNext()){ //while we have documents that contain the terms
				String document = dI.next(); //get the next document
				ArrayList<Node> termVector = docMap.get(document); //get the termVector of the document
				for(Node tokenNode : tokenList){ //for nodes in the query
					String token = tokenNode.getToken(); //get the token of the node from the query
					boolean found = false; //we haven't found it
					for(int i=0; i<termVector.size() && !found; i++){ //search the document term vector
						if(termVector.get(i).getToken().compareToIgnoreCase(token) == 0){ //compare the terms
							numerator += termVector.get(i).getTermFrequency()*tokenNode.getTermFrequency(); //Ai*Bi
							ais += Math.pow(tokenNode.getTermFrequency(),2); //Ai^2
							bis += Math.pow(termVector.get(i).getTermFrequency(), 2); //Bi^2
							found = true; //we found it
						} //end if term has been seen in corpus
					} //end parse of termVector search for tokens
				} // end query while
				double denominator = Math.sqrt(ais*bis); //compute the denominator
				/* Store outcomes */
				Node tNode = new Node(document, (numerator / denominator)); //create a node
				outcomes.add(tNode); //outcomes needs to be destroyed on new query read, find permenant storage
			} //end document that contain query terms list 
			
			Collections.sort(outcomes);
			
			ArrayList<String> relevancies = relevanceList.get(queryNum);
			
		} //end queries
		queries.close();
	}
}
