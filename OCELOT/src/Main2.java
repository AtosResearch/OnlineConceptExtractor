import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Vector;

import org.apache.lucene.queryParser.ParseException;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import eu.atosresearch.saett.conceptextraction.ConceptRating;
import eu.atosresearch.saett.feeder.Filter;
import eu.atosresearch.saett.feeder.KDEDocSource;
import eu.atosresearch.saett.feeder.MultiXMLSource;
import eu.atosresearch.saett.feeder.RemoveEmoticonsFilter;
import eu.atosresearch.saett.feeder.RemoveExtraSpacesFilter;
import eu.atosresearch.saett.feeder.RemoveTextFilter;
import eu.atosresearch.saett.feeder.RemoveURLFilter;
import eu.atosresearch.saett.feeder.RemoveXMLTagsFilter;
import eu.atosresearch.saett.feeder.Source;
import eu.atosresearch.saett.feeder.TextSelectionFilter;
import eu.atosresearch.saett.matching.JENAConector;
import eu.atosresearch.saett.matching.RemoteKB;
import eu.atosresearch.saett.matching.SemanticMatcher;
import eu.atosresearch.saett.matching.SesameConector;
import eu.atosresearch.saett.nlp.Document;
import eu.atosresearch.saett.nlp.StanfordNLP;
import eu.atosresearch.saett.nlp.Token;


public class Main2 {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		// TODO Auto-generated method stub
		new Main2().execute();

	}
	
	public void execute() throws FileNotFoundException, IOException, ParseException{
		System.out.println("Reading properties file");
		Properties prop=new Properties();
		prop.load(new FileInputStream("saett.properties"));
		
		Vector<Source> sources=new Vector<Source>();
		int i=1;
		
		System.out.println("Input sources configuration");
		while(prop.getProperty("saett.input."+i+".url")!=null){
			String url=prop.getProperty("saett.input."+i+".url");
			String type=prop.getProperty("saett.input."+i+".type");
			if(type.equals("MultiXMLSource")){
				System.out.println("Adding a MultiXMLSource source. URL:"+url);
				MultiXMLSource s=new MultiXMLSource(url);
				int j=1;
				while(prop.getProperty("saett.input."+i+".tag."+j)!=null){
					String[] tagr=prop.getProperty("saett.input."+i+".tag."+j).split(",");
					System.out.println("Use tags:"+tagr[0]+","+tagr[1]+","+tagr[2]);
					s.addTextTag(tagr[0], tagr[1], tagr[2]);
					j++;
				}
				sources.add(s);
			}else if(type.equals("KDEDocSource")){
				KDEDocSource s=new KDEDocSource(url);
				sources.add(s);
			}
			i++;
		}
		System.out.println(sources.size()+" sources loaded");
		System.out.println("Loading Filters");
		Vector<Filter> filters=new Vector<Filter>();
		
		i=1;
		while(prop.getProperty("saett.filters."+i+".type")!=null){
			String type=prop.getProperty("saett.filters."+i+".type");
			if(type.equals("TextSelectionFilter")){
				TextSelectionFilter fil=new TextSelectionFilter();
				fil.setFrom(prop.getProperty("saett.filters."+i+".from"));
				fil.setTo(prop.getProperty("saett.filters."+i+".to"));
				filters.add(fil);
				System.out.println("Added TextSelectionFilter - From:"+prop.getProperty("saett.filters."+i+".from")+", To:"+prop.getProperty("saett.filters."+i+".to"));
			}else if(type.equals("RemoveTextFilter")){
				RemoveTextFilter fil=new RemoveTextFilter(RemoveTextFilter.TO,prop.getProperty("saett.filters."+i+".to"));
				filters.add(fil);
				System.out.println("Added RemoveTextFilter - To:"+prop.getProperty("saett.filters."+i+".to"));
			}else if(type.equals("RemoveEmoticonsFilter")){
				filters.add(new RemoveEmoticonsFilter());
				System.out.println("Added RemoveEmoticonsFilter");
			}else if(type.equals("RemoveExtraSpacesFilter")){
				filters.add(new RemoveExtraSpacesFilter());
				System.out.println("Added RemoveExtraSpacesFilter");
			}else if(type.equals("RemoveURLFilter")){
				filters.add(new RemoveURLFilter());
				System.out.println("Added RemoveURLFilter");
			}else if(type.equals("RemoveXMLTagsFilter")){
				filters.add(new RemoveXMLTagsFilter());
				System.out.println("Added RemoveXMLTagsFilter");
			}
			i++;
		}
		System.out.println(filters.size()+" filters loaded");
		
		
		System.out.println("Loading Semantic Matcher configuration");
		
		SemanticMatcher sm;
		RemoteKB kb=null;
		if(prop.getProperty("saett.semanticmacher.type").equals("JENA")){
			kb=new JENAConector(prop.getProperty("saett.semanticmacher.endpoint"));
			System.out.println("Added SPARQL Endpoint - URL:"+prop.getProperty("saett.semanticmacher.endpoint"));
		}else if(prop.getProperty("saett.semanticmacher.type").equals("SESAME")){
			kb=new SesameConector(prop.getProperty("saett.semanticmacher.endpoint"), prop.getProperty("saett.semanticmacher.repository"));
			System.out.println("Added SPARQL Endpoint - URL:"+prop.getProperty("saett.semanticmacher.endpoint"));
		}
		sm=new SemanticMatcher(kb, prop.getProperty("saett.semanticmacher.indexfile"));
		i=1;
		while(prop.getProperty("saett.semanticmacher.filter."+i+".relation")!=null){
			String relation=prop.getProperty("saett.semanticmacher.filter."+i+".relation");
			String[] values=prop.getProperty("saett.semanticmacher.filter."+i+".domains").split(",");
			for(int j=0;j<values.length;j++){
				//System.out.println("Add Semantic domain:"+relation+","+values[j]);
				sm.addFilter(relation, values[j]);
			}
			i++;
		}
		
		String[] text=prop.getProperty("saett.semanticmacher.textrelations").split(",");
		for(i=0;i<text.length;i++){
			//System.out.println("Add Text Fields: "+text[i]);
			sm.addTextRelation(text[i]);
		}
		
		if(prop.getProperty("saett.semanticmacher.createindex").equals("1")){
			System.out.println("Indexing semantic repository");
			sm.index();
		}
		
		System.out.println("Loading documents");
		
		LinkedList<Document> docs=new LinkedList<Document>();
		for (Source s:sources){
			for(Filter f:filters){
				s.addFilter(f);
			}
			s.extract();
			docs.addAll(s.getDocs());
		}
		System.out.println(docs.size()+" documents loaded");
		
		ConceptRating cr=new ConceptRating(docs);
		if(prop.getProperty("saett.nlp").equals("StanfordNLP")){
			System.out.println("Used NLP framework: StanfordNLP");
			cr.setNLPTool(new StanfordNLP(prop));
		}
		
		String[] postags=prop.getProperty("saett.nlp.POSTag").split(",");
		for(i=0;i<postags.length;i++){
			if(postags[i].equals("Noun")){
				System.out.println("Selected PoS Tags:"+prop.getProperty("saett.nlp.POSTag"));
				cr.addSelectedPOSTag(Token.POS_NOUN);
			}
		}
		
		cr.setSemanticMatcher(sm);
		System.out.println("Extracting Key terms (PoS Tagging)");
		cr.calculate();
		System.out.println("Semantic Enrichment");
		cr.match();
		System.out.println("Generating output file: "+prop.getProperty("saett.output.file"));
		cr.writeXML(new PrintStream(prop.getProperty("saett.output.file")),prop.getProperty("saett.output.lemma").equals("1"));
		
	}

}
