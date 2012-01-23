package eu.atosresearch.saett.nlp;

import java.util.LinkedList;

public class Sentence {

	private LinkedList<Token> tokens;
	private String text;
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public LinkedList<Token> getTokens() {
		return tokens;
	}

	public Sentence(String text){
		this.text=text;
		tokens=new LinkedList<Token>();
	}
	
	public void addToken(Token token){
		tokens.add(token);
	}
}
