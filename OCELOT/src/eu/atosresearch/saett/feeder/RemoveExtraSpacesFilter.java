package eu.atosresearch.saett.feeder;

public class RemoveExtraSpacesFilter implements Filter {

	@Override
	public String filter(String input) {
		if(input!=null)
			return input.replaceAll("\t"," ").replaceAll(" {2,}", " ").replaceAll("\n", " ").trim();
		return null;
	}
	
}
