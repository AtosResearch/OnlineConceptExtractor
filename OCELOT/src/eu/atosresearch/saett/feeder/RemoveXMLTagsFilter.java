package eu.atosresearch.saett.feeder;

public class RemoveXMLTagsFilter implements Filter{

	@Override
	public String filter(String input) {
		if(input!=null)
			return input.replaceAll("\\<.*?>","").replaceAll("\\[.*?\\]","").replace("&nbsp;", "");
		return null;
	}

}
