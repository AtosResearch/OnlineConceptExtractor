package eu.atosresearch.saett.matching;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class JENAConector implements RemoteKB {

	private String url;
	
	public JENAConector(String url){
		this.url=url;
	}
	
	public Vector<HashMap<String, String>> executeSPARQL(String sparql){
		Query query = QueryFactory.create(sparql);
		QueryExecution qexec = QueryExecutionFactory.sparqlService(this.url, query);
		ResultSet results = qexec.execSelect();
		Vector<HashMap<String, String>> data=new Vector<HashMap<String,String>>();
		while (results.hasNext())
		{
			HashMap<String, String> row=new HashMap<String, String>();	
			QuerySolution soln = results.nextSolution() ;
			Iterator<String> ite=soln.varNames();
			while(ite.hasNext()){
				String var=ite.next();
				String x = soln.get(var).toString();
				row.put(var, x);
			}
			data.add(row);
		}
		return  data;
	}
	
	
}
