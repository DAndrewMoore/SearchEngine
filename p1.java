import java.io.*;
import java.util.*;

class p1{
	
	public static String[] tokenizer(String doc){
		doc = doc.replaceAll("\'","");
		doc = doc.replaceAll("\\p{Punct}", " ");
		String[] tDoc = doc.split("[\\s+\\n+]");
		return tDoc;
	}

	public static void main(String args[]) throws IOException{
		File folder = new File("C:\\Users\\Andrew\\Desktop\\4930.002\\citeseer"); //directed to document directory
		File[] listOfFiles = folder.listFiles(); //get the files in the directory
		HashMap<String, Integer> dictionary = new HashMap<String, Integer>(); //othe dictionary hashmap
		int numWords = 0; //total word counter
		
		for(File file : listOfFiles){ //parse through the files
			if(file != null && file.isFile()){ //makes sure it is a file
				Scanner blah = new Scanner(file); //set up object to read the file
				while(blah.hasNext()){ //parse the file
						String doc = blah.next(); //get the next token (auto-ignores whitespace)
						String[] tDoc = tokenizer(doc); //if our token was actually two or more tokens
						for(String meh : tDoc){ //parse through the retrieved token(s)
							int tf = dictionary.getOrDefault(meh, new Integer(0)); //get the term frequency or 0 if it hasn't been added
							tf++; //update term frequency
							dictionary.put(meh, tf); //put the new or updated term into the hashmap
							numWords++; //increase the number of words parsed
						} //end token array parser
				} //end word parser for certain file
				blah.close();
			} //end check if file
		} //end directory parser
		//dictionary should be completely populated by now with accurate term frequencies
		
		Set<String> sset = dictionary.keySet(); //get the tokens found
		Iterator<String> it = sset.iterator(); //create an iterator over those words
		ArrayList<Node> dict = new ArrayList<Node>();
		while(it.hasNext()){ //parse through the words
			String word = it.next(); //get the next word
			Node temp = new Node(word, dictionary.get(word));
			dict.add(temp);
		}
		
		Collections.sort(dict);
		
		PrintWriter pw = new PrintWriter(new File("C:\\Users\\Andrew\\Desktop\\4930.002\\results.txt"));
		
		for(int i=0; i<dict.size(); i++)
			pw.write(dict.get(i).getToken()+" - "+dict.get(i).getTermFrequency());
		
		System.out.println("Total words encountered => "+numWords);
		System.out.println("Total vocabulary size => "+dict.size());
		System.out.println("\nTop 20 words");
		for(int i=dict.size()-1, a=1; i>dict.size()-21; i--, a++)
			System.out.println(a+" "+dict.get(i).getToken());
	}
}
