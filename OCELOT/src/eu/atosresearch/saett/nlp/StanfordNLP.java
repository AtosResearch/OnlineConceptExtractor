package eu.atosresearch.saett.nlp;

import java.util.List;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.CoreAnnotations.LemmaAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.PartOfSpeechAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TextAnnotation;
import edu.stanford.nlp.ling.CoreAnnotations.TokensAnnotation;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.util.CoreMap;

public class StanfordNLP implements NLPTool {
	private Document doc;
	private StanfordCoreNLP pipeline;
	
	public StanfordNLP(Properties prop){
		Properties props = new Properties();
		props.put("annotators", "tokenize, ssplit, pos,lemma");
		props.put("pos.model", prop.getProperty("saett.nlp.stanford.posmodel"));
		pipeline = new StanfordCoreNLP(props);		
	}

	@Override
	public void process() {
		// TODO Auto-generated method stub
		Annotation document = new Annotation(doc.getText());
		pipeline.annotate(document);

		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		
		for(CoreMap sentence: sentences) {
			Sentence sen=new Sentence(sentence.get(TextAnnotation.class));
			for (CoreLabel token: sentence.get(TokensAnnotation.class)) {
				Token tok=new Token(token.get(TextAnnotation.class));
				tok.setPOSTag(token.get(PartOfSpeechAnnotation.class));
				tok.setLemma(token.get(LemmaAnnotation.class));
				sen.addToken(tok);
			}
			doc.addSentence(sen);
		}			
	}

	@Override
	public void setTarget(Document doc) {
		// TODO Auto-generated method stub
		this.doc=doc;
	}

}
