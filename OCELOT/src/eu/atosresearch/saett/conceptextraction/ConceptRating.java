package eu.atosresearch.saett.conceptextraction;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;

import eu.atosresearch.saett.matching.SemanticMatcher;
import eu.atosresearch.saett.nlp.Document;
import eu.atosresearch.saett.nlp.NLPTool;
import eu.atosresearch.saett.nlp.Token;

public class ConceptRating {
	private LinkedList<Document> documents;
	private Vector<Integer> selectedPOSTag;
	private NLPTool nlptool;
	private SemanticMatcher sm;
	private Vector<Concept> ratingByToken;
	private Vector<Concept> ratingByLemma;
	
	
	
	public ConceptRating(LinkedList<Document> documents){
		this.documents=documents;
		selectedPOSTag=new Vector<Integer>();
	}
	
	public void setNLPTool(NLPTool nlpTool){
		this.nlptool=nlpTool;
	}
	
	public void addSelectedPOSTag(int pos){
		selectedPOSTag.add(pos);
	}
	
	public void setSemanticMatcher(SemanticMatcher sm){
		this.sm=sm;
	}
	
	public void calculate(){
		HashMap<String, Concept>byLemma=new HashMap<String, Concept>();
		HashMap<String, Concept>byToken=new HashMap<String, Concept>();

		for(Document doc:documents){
			nlptool.setTarget(doc);
			nlptool.process();
			for (int pos:selectedPOSTag){
				LinkedList<Token> tokens=doc.extractByPOS(pos);
				for(Token token:tokens){
					String keytoken=token.getPOSTag().charAt(0)+"#"+token.getText().toLowerCase();
					String keylemma=token.getPOSTag().charAt(0)+"#"+token.getLemma().toLowerCase();
						
					if(byToken.containsKey(keytoken)){
						byToken.get(keytoken).addCount();
					}else{
						byToken.put(keytoken, new Concept(token));
					}
					
					if(byLemma.containsKey(keylemma)){
						byLemma.get(keylemma).addCount();
					}else{
						byLemma.put(keylemma, new Concept(token));
					}
				}
			}
		}
		ratingByToken=sortHashMapByValuesD(byToken);
		ratingByLemma=sortHashMapByValuesD(byLemma);		
	}
	
	public void match() throws CorruptIndexException, IOException, ParseException{
		for(Concept c:ratingByToken){
			sm.match(c, false, 10);
		}
		for(Concept c:ratingByLemma){
			sm.match(c, true, 10);
		}				
	}
	
	public Vector<Concept> sortHashMapByValuesD(HashMap<String, Concept> passedMap) {
	    List<String> mapKeys = new ArrayList<String>(passedMap.keySet());
	    List<Concept> mapValues = new ArrayList<Concept>(passedMap.values());
	    Collections.sort(mapValues);
	    Collections.sort(mapKeys);
	    Collections.reverse(mapValues);
	        
	    LinkedHashMap<String, Concept> sortedMap = 
	        new LinkedHashMap<String,Concept>();
	    
	    Iterator<Concept> valueIt = mapValues.iterator();
	    while (valueIt.hasNext()) {
	        Concept val = valueIt.next();
	        Iterator<String> keyIt = mapKeys.iterator();
	        
	        while (keyIt.hasNext()) {
	            Object key = keyIt.next();
	            Concept comp1 = passedMap.get(key);
	            Concept comp2 = val;
	            
	            if (comp1.equals(comp2)){
	                passedMap.remove(key);
	                mapKeys.remove(key);
	                sortedMap.put((String)key, (Concept)val);
	                break;
	            }

	        }
	    }
	    Vector<Concept> rst=new Vector<Concept>();
	    
	    for(String key:sortedMap.keySet()){
	    	rst.add(sortedMap.get(key));
	    }
	    return rst;
	}

	public Vector<Concept> getRatingByToken() {
		return ratingByToken;
	}

	public Vector<Concept> getRatingByLemma() {
		return ratingByLemma;
	}
	
	
	public void writeXML(PrintStream ps, boolean byLemma){
		ps.println("<?xml version=\"1.0\"?>");
		ps.println("<concepts>");
		Vector<Concept> list;
		if(byLemma)
			list=ratingByLemma;
		else
			list=ratingByToken;
		for(Concept c:list){
			ps.println("<concept token='"+c.getToken().getText()+"' lemma='"+c.getToken().getLemma()+"' occurrences='"+c.getCount()+"'>");
			if(c.getSemanticConcepts().size()>0)
				//ps.println(c.getToken().getText()+" "+c.getSemanticConcepts().get(0));
				for(String txt:c.getSemanticConcepts()){
					ps.println("<match>"+txt+"</match>");
				}
			ps.println("</concept>");
		}		
		ps.println("</concepts>");
	}
}
