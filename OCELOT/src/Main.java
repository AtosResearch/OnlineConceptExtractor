import java.io.IOException;
import java.util.LinkedList;
import java.util.Properties;

import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.queryParser.ParseException;

import eu.atosresearch.saett.conceptextraction.ConceptRating;
import eu.atosresearch.saett.feeder.KDEDocSource;
import eu.atosresearch.saett.feeder.MultiXMLSource;
import eu.atosresearch.saett.feeder.RemoveEmoticonsFilter;
import eu.atosresearch.saett.feeder.RemoveExtraSpacesFilter;
import eu.atosresearch.saett.feeder.RemoveTextFilter;
import eu.atosresearch.saett.feeder.RemoveURLFilter;
import eu.atosresearch.saett.feeder.RemoveXMLTagsFilter;
import eu.atosresearch.saett.feeder.TextSelectionFilter;
import eu.atosresearch.saett.matching.JENAConector;
import eu.atosresearch.saett.matching.SemanticMatcher;
import eu.atosresearch.saett.matching.SesameConector;
import eu.atosresearch.saett.nlp.Document;
import eu.atosresearch.saett.nlp.StanfordNLP;
import eu.atosresearch.saett.nlp.Token;


public class Main {

	/**
	 * @param args
	 * @throws IOException 
	 * @throws CorruptIndexException 
	 * @throws IOException 
	 * @throws ParseException 
	 * @throws ClassNotFoundException 
	 */
	
	
	public void indexOntology() throws CorruptIndexException, IOException{
		SesameConector ses=new SesameConector("http://localhost:8080/openrdf-sesame", "ALERT");
		SemanticMatcher sm=new SemanticMatcher(ses,"D:\\Documents\\Proyectos\\ALERT\\dev\\indexs\\annotation-ontology");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://www.w3.org/2002/07/owl#Class");
		sm.addTextRelation("http://www.w3.org/2000/01/rdf-schema#label");
		sm.index();		
	}
	
	public void kde(){
		KDEDocSource source=new KDEDocSource("D:\\Documents\\Proyectos\\ALERT\\DAta\\kdeDocs\\kdeDocs");
		source.addFilter(new RemoveEmoticonsFilter());
		source.addFilter(new RemoveExtraSpacesFilter());
		source.addFilter(new RemoveURLFilter());
		source.addFilter(new RemoveXMLTagsFilter());
		source.extract();
		LinkedList<Document> docs=source.getDocs();

		for(Document doc:docs){
			System.out.println("Doc:"+doc.getText());
		}
		System.out.println(docs.size());
	}
	
	public void execute() throws IOException, ParseException, ClassNotFoundException{
		/*
		Properties prop=new Properties();
		prop.load(this.getClass().getResourceAsStream("saett.properties"));
		
		System.out.println("Load data");
		
		MultiXMLSource source=new MultiXMLSource("D:\\Documents\\Proyectos\\ALERT\\DAta\\nabble-backup-Petals-ESB_001");
		source.addTextTag("field", "name", "subject");
		source.addTextTag("field", "name", "message");
		
		TextSelectionFilter tfil=new TextSelectionFilter();
		tfil.setFrom("Content-Transfer-Encoding: 7bit");
		tfil.setTo("-------------------- m2f --------------------");
		source.addFilter(tfil);
		RemoveTextFilter rt=new RemoveTextFilter(RemoveTextFilter.TO, "X-SA-Exim-Scanned:");
		source.addFilter(rt);
			
		source.addFilter(new RemoveEmoticonsFilter());
		source.addFilter(new RemoveExtraSpacesFilter());
		source.addFilter(new RemoveURLFilter());
		source.addFilter(new RemoveXMLTagsFilter());
		
		source.extract();
		
		//===============
		
		MultiXMLSource source2=new MultiXMLSource("D:\\Documents\\Proyectos\\ALERT\\DAta\\nabble-backup-Petals-ESB_002");
		source2.addTextTag("field", "name", "subject");
		source2.addTextTag("field", "name", "message");
		
		source2.addFilter(tfil);
		source2.addFilter(rt);
			
		source2.addFilter(new RemoveEmoticonsFilter());
		source2.addFilter(new RemoveExtraSpacesFilter());
		source2.addFilter(new RemoveURLFilter());
		source2.addFilter(new RemoveXMLTagsFilter());
		source2.extract();
		
		LinkedList<Document> docs=source.getDocs();
		docs.addAll(source2.getDocs());
		
		System.out.println("Rating");
		
		ConceptRating cr=new ConceptRating(docs);
		cr.setNLPTool(new StanfordNLP(prop));
		cr.addSelectedPOSTag(Token.POS_NOUN);
		cr.calculate();
		
		System.out.println("Index");
		*/				
		JENAConector jena=new JENAConector("http://dbpedia.org/sparql");
		SemanticMatcher sm=new SemanticMatcher(jena,"D:\\Documents\\Proyectos\\ALERT\\dev\\indexs\\dbpedia-tech");
				
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/DataModelingLanguages");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/WebServices");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/XML-basedStandards");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/WebServiceSpecifications");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/WebPortals");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/InternetProtocols");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/InternetStandards");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/JavaSpecificationRequests");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/Encodings");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/InternetMailProtocols");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/CompilingTools");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/JavaDevelopmentTools");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/MicrosoftWindows");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/ProxyServers");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/Identifiers");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/WorldWideWebConsortiumStandards");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/OpenFormats");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/ApplicationLayerProtocols");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/WebBrowsers");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/DataSerializationFormats");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/X86-64LinuxDistributions");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/Debian-basedDistributions");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/SoftwareComponents");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/ComputingPlatforms");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/NamingConventions");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/ProgrammingLanguage106898352");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/VirtualMachines");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/JavaLibraries");
		sm.addFilter("http://www.w3.org/1999/02/22-rdf-syntax-ns#type", "http://dbpedia.org/class/yago/Servers");
		
		sm.addFilter("http://purl.org/dc/terms/subject", "http://dbpedia.org/resource/Category:Free_software_programmed_in_Java");
		sm.addFilter("http://purl.org/dc/terms/subject", "http://dbpedia.org/resource/Category:Open_source_database_management_systems");
		sm.addFilter("http://purl.org/dc/terms/subject", "http://dbpedia.org/page/Category:Java_APIs");
		sm.addFilter("http://purl.org/dc/terms/subject", "http://dbpedia.org/resource/Category:Enterprise_application_integration");
		sm.addFilter("http://purl.org/dc/terms/subject", "http://dbpedia.org/resource/Category:Web_service_specifications");
		sm.addFilter("http://purl.org/dc/terms/subject", "http://dbpedia.org/resource/Category:World_Wide_Web_Consortium_standards");
		sm.addFilter("http://purl.org/dc/terms/subject", "http://dbpedia.org/resource/Category:Workflow_technology");

		sm.addTextRelation("http://www.w3.org/2000/01/rdf-schema#label");
		sm.addTextRelation("http://dbpedia.org/property/name");
		sm.addTextRelation("http://www.w3.org/2000/01/rdf-schema#comment");
		sm.index();
		
		/*
		System.out.println("Match");

		cr.setSemanticMatcher(sm);
		cr.match();
		
		cr.writeXML(System.out, false);
		*/
		
	}
	
	public static void main(String[] args) throws IOException, ParseException, ClassNotFoundException {
		// TODO Auto-generated method stub
		new Main().kde();
	}

}
