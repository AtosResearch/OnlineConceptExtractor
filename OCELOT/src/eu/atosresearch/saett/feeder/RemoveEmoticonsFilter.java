package eu.atosresearch.saett.feeder;

public class RemoveEmoticonsFilter implements Filter {

	@Override
	public String filter(String input) {
		if(input!=null)
			return input.replaceAll(";\\)|:P|:p|8\\)|:\\(|:-\\)|:-\\(|:D|:s|:S"," ").replaceAll("-|=|_"," ");
		else
			return null;
	}

}
