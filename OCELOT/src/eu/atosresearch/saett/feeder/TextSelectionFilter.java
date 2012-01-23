package eu.atosresearch.saett.feeder;

public class TextSelectionFilter implements Filter {
	private String from;
	private String to;

	public void setFrom(String from) {
		this.from = from;
	}

	public void setTo(String to) {
		this.to = to;
	}

	@Override
	public String filter(String input) {
		if(input!=null){
			int cini=input.indexOf(from);

			if(cini>-1)
				input=input.substring(cini+from.length());
			int cfin=input.indexOf(to);
			if(cfin>-1)
				input=input.substring(0,cfin-1);	
			// TODO Auto-generated method stub
			return input;
		}
		return null;
	}

}
