package eu.atosresearch.saett.matching;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.Field.Index;
import org.apache.lucene.document.Field.Store;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexReader.FieldOption;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import eu.atosresearch.saett.conceptextraction.Concept;


public class CopyOfSemanticMatcher {
	private RemoteKB kb;
	private HashMap<String, String> filters;
	private Vector<String> textRelation;
	private String index;
	private IndexSearcher searcher;

	public final static double MAX_FILTERS=15.0;


	public CopyOfSemanticMatcher(RemoteKB kb,String index){
		this.kb=kb;
		filters=new HashMap<String, String>();
		textRelation=new Vector<String>();
		this.index=index;
	}

	public void addFilter(String predicate, String object){
		if(filters.containsKey(predicate))
			filters.put(predicate,filters.get(predicate)+",<"+object+">");
		else
			filters.put(predicate,"<"+object+">");
	}


	public void addTextRelation(String predicate){
		textRelation.add(predicate);
	}

	public void index() throws CorruptIndexException, IOException{
		HashMap<String, String> included=new HashMap<String, String>();
		IndexWriter iw=new IndexWriter(FSDirectory.open(new File(index)),new StandardAnalyzer(Version.LUCENE_33),true,IndexWriter.MaxFieldLength.UNLIMITED);
		String start="select distinct ?x ";
		for(int i=0;i<textRelation.size();i++){
			start+="?x"+i+" ";
		}
		start+="{";
		String filter="";
		int t=0;
		for(String rel:filters.keySet()){
			if(t>0)
				filter+=" UNION ";
			filter+="{?x <"+rel+"> ?t"+t+".FILTER(?t"+t+" in("+filters.get(rel)+"))}";
			t++;
		}

		int i=0;
		String ending="";
		for(String txt:textRelation){
			ending+=" OPTIONAL {?x <"+txt+"> ?x"+i+".";
			//
			ending+="FILTER ( lang(?x"+i+") = \"en\")}";
			i++;
		}

		ending+="}";

		int offset=0;
		int limit=1000;


		String query=start+filter+ending+" LIMIT "+limit+" OFFSET "+offset;

		Vector<HashMap<String, String>> res=kb.executeSPARQL(query);
		while(res.size()>0){
			for(HashMap<String, String> data: res){
				String txt="";
				for(int j=0;j<textRelation.size();j++){
					String value=data.get("x"+j);
					if(value!=null && !value.equals("@")){
						value=value.split("@")[0];
						value=value.replaceAll("_", " ").trim();
						txt+=" %% "+value;
					}
				}
				if(included.containsKey(data.get("x")))
					included.put(data.get("x"), included.get(data.get("x"))+" %% "+txt);
				else
					included.put(data.get("x"), txt);
			}

			offset+=limit;
			query=start+filter+ending+" LIMIT "+limit+" OFFSET "+offset;
			res=kb.executeSPARQL(query);
		}
		
		int max_fields=-1;
		
		for(String key:included.keySet()){
			String text=included.get(key);
			String[] parts=text.split(" %% ");
			if(parts.length>max_fields)
				max_fields=parts.length;
		}

		for(String key:included.keySet()){
			if(!included.get(key).trim().equals("")){
				Document doc=new Document();
				doc.add(new Field("URI",key,Store.YES,Index.NO));
				String txt=included.get(key);
				String[] parts=txt.split(" %% ");
				for(int j=0;j<parts.length;j++){
					doc.add(new Field("X"+j,parts[j],Store.YES,Index.ANALYZED));
				}
				doc.add(new Field("txt",included.get(key),Store.YES,Index.ANALYZED));
				iw.addDocument(doc);
			}
		}
		iw.close();
	}

	public void match(Concept concept,boolean lemma,int max_matches) throws CorruptIndexException, IOException, ParseException{
		if(searcher==null)
			searcher = new IndexSearcher(FSDirectory.open(new File(index)), true);
		
		String[] fields=new String[searcher.getIndexReader().getFieldNames(FieldOption.ALL).size()-2];
		for(int i=0;i<fields.length-2;i++){
			fields[i]="X"+i;
		}
		
		System.out.println(fields.length);

		String c;
		if(lemma)
			c=concept.getToken().getLemma();
		else
			c=concept.getToken().getText();

		c=c.replaceAll("\\+", "");
		c=c.replaceAll("\\?", "");
		c=c.replaceAll("!", "");
		c=c.replaceAll("\\^", "");
		c=c.replaceAll(":", "");
		c=c.replaceAll("\\}", "");
		c=c.replaceAll("\\{", "");
		c=c.replaceAll("\\[", "");
		c=c.replaceAll("\\]", "");
		c=c.replaceAll("\\*", "");
		c=c.replaceAll("\\\\", "");
		c=c.replaceAll("~", "");
		if(c.equals("OR"))
			c="";

		if(!c.equals("")){
			Query q=new MultiFieldQueryParser(Version.LUCENE_33,fields, new StandardAnalyzer(Version.LUCENE_33)).parse(c);
			TopScoreDocCollector collector = TopScoreDocCollector.create(max_matches, true);
			searcher.search(q, collector);
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
			for(int i=0;i<hits.length;i++){
				for(int j=0;j<fields.length;j++)
					System.out.println("		=>X"+j+":"+searcher.doc(hits[i].doc).get("X"+j));				
				concept.addSemanticConcept(searcher.doc(hits[i].doc).get("URI"));
			}
		}
	}

	public Vector<String> search(String query,int max_matches) throws CorruptIndexException, IOException, ParseException{
		if(searcher==null)
			searcher = new IndexSearcher(FSDirectory.open(new File(index)), true);
				
		Vector<String> v=new Vector<String>();
		
		String[] fields=new String[searcher.getIndexReader().getFieldNames(FieldOption.ALL).size()-2];
		for(int i=0;i<fields.length-2;i++){
			fields[i]="X"+i;
		}
		
		Query q=new MultiFieldQueryParser(Version.LUCENE_33,fields, new StandardAnalyzer(Version.LUCENE_33)).parse(query);
		TopScoreDocCollector collector = TopScoreDocCollector.create(max_matches, true);
		searcher.search(q, collector);
		ScoreDoc[] hits = collector.topDocs().scoreDocs;
		for(int i=0;i<hits.length;i++){
			v.add(searcher.doc(hits[i].doc).get("URI"));
		}		
		return v;
	}

}
