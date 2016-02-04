
public class Node implements Comparable<Node>{
	private String token;
	private Integer tf;
	
	public Node(String a, Integer b){
		token = a;
		tf = b;
	}
	
	public String getToken(){
		return token;
	}
	
	public Integer getTermFrequency(){
		return tf;
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
