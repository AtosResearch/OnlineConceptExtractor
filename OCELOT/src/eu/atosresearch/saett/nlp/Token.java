package eu.atosresearch.saett.nlp;

import java.io.Serializable;

public class Token implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public static int POS_VERB=1;
	public static int POS_NOUN=2;
	public static int POS_ADJECTIVE=2;
	
	String text;
	String lemma;
	String POSTag;
	
	public Token(String text){
		this.text=text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getLemma() {
		return lemma;
	}

	public void setLemma(String lemma) {
		this.lemma = lemma;
	}

	public String getPOSTag() {
		return POSTag;
	}

	public void setPOSTag(String pOSTag) {
		POSTag = pOSTag;
	}
}
