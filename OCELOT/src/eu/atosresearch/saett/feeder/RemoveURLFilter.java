package eu.atosresearch.saett.feeder;

public class RemoveURLFilter implements Filter {

	@Override
	public String filter(String input) {
		String urlpattern="\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)" + 
		"(\\w+:\\w+@)?(([-\\w]+\\.)+(com|org|net|gov" + 
		"|mil|biz|info|mobi|name|aero|jobs|museum" + 
		"|travel|[a-z]{2}))(:[\\d]{1,5})?" + 
		"(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?" + 
		"((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" + 
		"([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)" + 
		"(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?" + 
		"([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*" + 
		"(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b";
		if(input!=null)
			return input.replaceAll(urlpattern, "");
		return null;
	}
	
}
