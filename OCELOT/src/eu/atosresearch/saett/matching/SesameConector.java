package eu.atosresearch.saett.matching;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.openrdf.query.BindingSet;
import org.openrdf.query.MalformedQueryException;
import org.openrdf.query.QueryEvaluationException;
import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.Repository;
import org.openrdf.repository.RepositoryConnection;
import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.http.HTTPRepository;

public class SesameConector implements RemoteKB {
	
	public Repository myRepository;
	public RepositoryConnection con;
	
	public SesameConector(String url,String repository){
		myRepository = new HTTPRepository(url, repository);
		try {
			myRepository.initialize();
		} catch (RepositoryException e) {
			throw new RuntimeException("Repository did not initialize ", e);
		}		
	}
	
	
	@Override
	public Vector<HashMap<String, String>> executeSPARQL(String sparql) {
		RepositoryConnection con;
		TupleQueryResult graphResult = null;
		Set<BindingSet> statements = new HashSet<BindingSet>();
		try {
			con = myRepository.getConnection();
			try {
				graphResult = con
				.prepareTupleQuery(QueryLanguage.SPARQL, sparql)
				.evaluate();

				while (graphResult.hasNext()) {
					statements.add(graphResult.next());
				}
			} catch (QueryEvaluationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedQueryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				con.close();
			}
		} catch (RepositoryException e) {
			e.printStackTrace();
		}
		
		Vector<HashMap<String, String>> result=new Vector<HashMap<String,String>>();
		
		for(BindingSet b:statements){
			HashMap<String, String> row=new HashMap<String, String>();
			for(String key:b.getBindingNames()){
				if(b.getValue(key)!=null){
					row.put(key, b.getValue(key).stringValue());
				}
			}
			result.add(row);
		}
		
		return result;		
	}

}
