package eu.atosresearch.saett.feeder;

import java.util.LinkedList;

import eu.atosresearch.saett.nlp.Document;

public abstract class Source {
	protected LinkedList<Document> docs;
	protected LinkedList<Filter> filters;

	public Source(){
		filters=new LinkedList<Filter>();
	}

	public abstract void extract();

	public LinkedList<Document> getDocs() {
		return docs;
	}

	public void addFilter(Filter filter){
		filters.add(filter);
	}

	public String applyFilters(String text){
		if(text!=null){
			for(Filter fil:filters){
				text=fil.filter(text);
			}
			return text;
		}
		return null;
	}
}
