package eu.atosresearch.saett.feeder;

public class RemoveTextFilter implements Filter {
	public final static int FROM=0;
	public final static int TO=1;
	
	private String limit;
	private int type;
	
	public RemoveTextFilter(int type,String limit){
		this.type=type;
		this.limit=limit;
	}

	@Override
	public String filter(String input){
		int index=input.indexOf(limit);
		switch (type) {
		case FROM:
			if(index>-1)
				input=input.substring(0,index-1);			
			break;
		case TO:
			if(index>-1)
				input=input.substring(index+limit.length()+1);			
			break;
		}
		return input;
	}
	
}
