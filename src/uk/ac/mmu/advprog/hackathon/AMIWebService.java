package uk.ac.mmu.advprog.hackathon;

import static spark.Spark.get;
import static spark.Spark.port;
import java.io.*;
import java.util.ArrayList;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import spark.Request;
import spark.Response;
import spark.Route;

/**
 * AMIWebService handles the setting up and starting of the web service, and displays the respective results through each route
 * @author Daniel Lord (12075327)
 */
public class AMIWebService {

	/**
	 * Main program entry point, starts the web service
	 * @param args not used
	 */
	public static void main(String[] args) {		
		port(8088);
		
		get("/test", new Route() {
			@Override
			public Object handle(Request request, Response response) throws Exception {
				try (DB db = new DB()) {
					return "Number of Entries: " + db.getNumberOfEntries();
				}
			}			
		});
		
		get("/lastsignal", new Route() {
			@Override
			public Object handle(Request arg0, Response arg1) throws Exception {
				String signalid = arg0.queryParams("signal_id");
				try (DB db = new DB()) {
					return "Last Signal Displayed: " + db.getLastSignalDisplayed(signalid);
				}
			}
		});
		
		get("/frequentlyused", new Route() {
			@Override
			public Object handle(Request arg0, Response arg1) throws Exception {
				arg1.type("application/xml");
				String motorway = arg0.queryParams("motorway");
				try (DB db = new DB()) {
					ArrayList<FrequentlyUsedClass> results = db.getFrequentlyUsed(motorway);
					return frequentlyUsedXML(results);
				}
			}
		});
		
		get("/signalgroups", new Route() {
			@Override
			public Object handle(Request arg0, Response arg1) throws Exception {
				arg1.type("application/json");
				try (DB db = new DB()) {
					ArrayList<String> results = db.getSignalGroups();
					return signalGroupsJSON(results);
				}	
			}
		});
		
		get("/signalsbygrouptime", new Route() {
			@Override
			public Object handle(Request arg0, Response arg1) throws Exception {
				arg1.type("application/json");
				String group = arg0.queryParams("group");
				String time = arg0.queryParams("time");
				try (DB db = new DB()) {
					ArrayList<SignalsByGroupTimeClass> results = db.getSignalsByGroupTime(group, time);
					return signalsByGroupTimeJSON(results);
				}
			}	
		});
		
		System.out.println("Server running!");
	}
	
	/**
	 * Returns the formatted XML Writer to be displayed in the /frequentlyused Route on the web service
	 * @param results The results from the FrequentlyUsedClass ArrayList, to be included in the XML Writer
	 * @return Formatted XML Writer to be displayed on web service
	 */
	public static Writer frequentlyUsedXML(ArrayList<FrequentlyUsedClass> results) {
		try {
			DocumentBuilder docb = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document xml = docb.newDocument();
			//Create the XML tree
			Element root = xml.createElement("SignalTrends");
			xml.appendChild(root);
			for (int i=0; i<results.size(); i++) {
				Element signals = xml.createElement("Signal");
				root.appendChild(signals);
				Element value = xml.createElement("Value");
				signals.appendChild(value).appendChild(xml.createTextNode(results.get(i).getSignalValue()));
				Element frequency = xml.createElement("Frequency");
				String freqStr = String.valueOf(results.get(i).getSignalValueCount());
				signals.appendChild(frequency).appendChild(xml.createTextNode(freqStr));
			}
			//Transformer to String
			Transformer transformer = TransformerFactory.newInstance().newTransformer();
			Writer output = new StringWriter();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(new DOMSource(xml), new StreamResult(output));
			
			return output;
		}
		catch (ParserConfigurationException | TransformerException pcte) {
			pcte.printStackTrace();
		}
		return null;
	}
	
	/**
	 * Returns the formatted JSON String to be displayed on the /signalgroups Route on the web service
	 * @param results The results from the String ArrayList, to be included in the JSON String
	 * @return Formatted JSON String to be displayed on the web service
	 */
	public static String signalGroupsJSON(ArrayList<String> results) {
		//Create JSON tree
		JSONArray signals = new JSONArray();
		for (int i=0; i<results.size(); i++) {
			signals.put(results.get(i));	
		}
		return signals.toString(4);	//returns the JSONArray signals toString, with indentation
	}
	
	/**
	 * Returns the formatted JSON String to be displayed on the /signalsbygrouptime Route on the web service
	 * @param results The results from the SignalsByGroupTimeClass ArrayList, to be included in the JSON String
	 * @return Formatted JSON String to be displayed on the web service
	 */
	public static String signalsByGroupTimeJSON(ArrayList<SignalsByGroupTimeClass> results) {
		//Create JSON tree
		JSONObject root = new JSONObject();
		for (int i=0; i<results.size(); i++) {
			JSONObject test = new JSONObject();
			test.put("date_set", results.get(i).getDatetime());
			test.put("value", results.get(i).getSignalValue());
			root.put(results.get(i).getSignalId(), test);
		}
		return root.toString(4);	//returns the JSONObject root toString, with indentation
	}

}
