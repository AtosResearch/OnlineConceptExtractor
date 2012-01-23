package eu.atosresearch.saett.conceptextraction;

import java.io.Serializable;
import java.util.Vector;

import eu.atosresearch.saett.nlp.Token;

public class Concept implements Comparable<Concept>,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Token token;
	private int count;
	Vector<String> semanticConcepts;
	
	public Concept(Token token){
		this.token=token;
		count=1;
		semanticConcepts=new Vector<String>();
	}
	
	public void addCount(){
		count++;
	}
	
	public void addSemanticConcept(String uri){
		if(!semanticConcepts.contains(uri))
			semanticConcepts.add(uri);
	}

	public Token getToken() {
		return token;
	}

	@Override
	public int compareTo(Concept o) {
		if(o.getToken().getText().toLowerCase().equals(token.getText().toLowerCase()))
			return 0;
		else 
			if(o.getCount()>count)
				return -1;
			else
				return 1;
	}

	public int getCount() {
		return count;
	}

	public Vector<String> getSemanticConcepts() {
		return semanticConcepts;
	}
	

}
