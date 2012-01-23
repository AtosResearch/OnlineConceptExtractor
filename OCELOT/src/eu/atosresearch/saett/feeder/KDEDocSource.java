package eu.atosresearch.saett.feeder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.Vector;

import eu.atosresearch.saett.nlp.Document;

public class KDEDocSource extends Source {

	private String url;

	public KDEDocSource(String url){
		this.url=url;
	}

	@Override
	public void extract() {
		try{
			docs=new LinkedList<Document>();
			Vector<File> data=getFiles(new File(url), "docbook");
			for(File f:data){
				String txt="";
				String line;
				BufferedReader br=new BufferedReader(new FileReader(f));
				while((line=br.readLine())!=null){
					txt+=" "+line;
				}
				docs.add(new Document(applyFilters(txt).replaceAll("\">", "").replaceAll("]>", "")));
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	private Vector<File> getFiles(File d,String ext){
		Vector<File> files=new Vector<File>();
		File[] fls=d.listFiles();
		for(int i=0;i<fls.length;i++){
			if(fls[i].isDirectory()){
				files.addAll(getFiles(fls[i],ext));
			}else{
				if(fls[i].getName().endsWith(ext))
					files.add(fls[i]);
			}
		}
		return files;
	}

}
