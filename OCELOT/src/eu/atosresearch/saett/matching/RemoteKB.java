package eu.atosresearch.saett.matching;

import java.util.HashMap;
import java.util.Vector;


public interface RemoteKB {
	public Vector<HashMap<String, String>> executeSPARQL(String sparql);
}
