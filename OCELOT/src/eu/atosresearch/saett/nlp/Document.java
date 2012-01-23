package eu.atosresearch.saett.nlp;

import java.util.LinkedList;

public class Document {
	private String text;
	private LinkedList<Sentence> sentences;
	
	public Document(String text){
		this.text=text;
		sentences=new LinkedList<Sentence>();
	}
	
	public void addSentence(Sentence sentence){
		sentences.add(sentence);
	}

	public String getText() {
		return text;
	}

	public LinkedList<Sentence> getSentences() {
		return sentences;
	}
	
	public LinkedList<Token> extractByPOS(int pos){
		LinkedList<Token> tokens=new LinkedList<Token>();
		for(Sentence sentence:sentences){
			for(Token token:sentence.getTokens()){
				if(pos == Token.POS_NOUN && token.getPOSTag().charAt(0)=='N')
					tokens.add(token);
				if(pos == Token.POS_ADJECTIVE && token.getPOSTag().charAt(0)=='A')
					tokens.add(token);
				if(pos == Token.POS_VERB && token.getPOSTag().charAt(0)=='V')
					tokens.add(token);
			}
		}
		return tokens;
	}
	
}
