/*
 * Name: Daniel Moore
 * Class: 4930.002 - Information Retrieval
 * Date: 2/4/16
 * Description: Node class to link words/terms to their term frequencies and overrides a compareTo method for sorting
 * lowest to highest term frequencies.
 */
public class Node implements Comparable<Node>{
	private String token;
	private Double tf;
	
	public Node(String a, Double b){
		token = a;
		tf = b;
	}
	
	public String getToken(){
		return token;
	}
	
	public Double getTermFrequency(){
		return tf;
	}
	
	public void incrementTf(){
		tf++;
	}
	
	public void setTF(Double b){
		tf = b;
	}
	
	@Override
	public int compareTo(Node arg0) {
		if(tf > arg0.getTermFrequency())
			return 1;
		else if(tf == arg0.getTermFrequency())
			return 0;
		else
			return -1;
	}
}
