package eu.atosresearch.saett.feeder;

import java.io.File;
import java.util.LinkedList;
import java.util.Vector;

import eu.atosresearch.saett.nlp.Document;
import eu.atosresearch.saett.util.XMLParser;

public class MultiXMLSource extends Source {
	private String directory;
	private Vector<String> textTags;
	private Vector<String> textAttr;
	private Vector<String> textValues;

	public MultiXMLSource(String directory){
		super();
		this.directory=directory;
	}

	public void addTextTag(String tag){
		if(textTags==null)
			textTags=new Vector<String>();
		if(textAttr==null)
			textAttr=new Vector<String>();
		if(textValues==null)
			textValues=new Vector<String>();
		
		textTags.add(tag);
		textAttr.add(null);
		textValues.add(null);
	}
	
	public void addTextTag(String tag,String attribute,String value){
		if(textTags==null)
			textTags=new Vector<String>();
		if(textAttr==null)
			textAttr=new Vector<String>();
		if(textValues==null)
			textValues=new Vector<String>();		
		textTags.add(tag);
		textAttr.add(attribute);
		textValues.add(value);
	}

	@Override
	public void extract() {
		docs=new LinkedList<Document>();
		// TODO Auto-generated method stub
		File folder = new File(directory);
		File[] listOfFiles = folder.listFiles();
		try{
			for (int i = 0; i < listOfFiles.length; i++) {
				if (listOfFiles[i].isFile() && listOfFiles[i].getName().indexOf(".xml")>-1) {
					XMLParser parser=new XMLParser(listOfFiles[i]);
					int j=0;
					String text="";
					for(String tag:textTags){
						if(textAttr.get(j)==null){
							text+=applyFilters(parser.getTextValue(tag))+" ";
						}else{
							text+=applyFilters(parser.getTextValue(tag, textAttr.get(j), textValues.get(j)))+" ";
						}
						j++;
					}
					docs.add(new Document(text));
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
