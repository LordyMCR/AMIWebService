package uk.ac.mmu.advprog.hackathon;

/**
 * SignalsByGroupTimeClass creates a new object to store each result entry from an SQL query in the database, which will then be used to display in JSON format
 * @author Daniel Lord (12075327)
 *
 */

public class SignalsByGroupTimeClass {
	
	String signalId;
	String datetime;
	String signalValue;
	
	/**
	 * Constructs a new object to store each SQL query result row
	 * @param signalId The signal_id column in the database results will be stored in this parameter
	 * @param datetime The datetime column in the database results will be stored in this parameter
	 * @param signalValue The signal_value column in the database results will be stored in this parameter
	 */
	public SignalsByGroupTimeClass(String signalId, String datetime, String signalValue) {
		this.signalId = signalId;
		this.datetime = datetime;
		this.signalValue = signalValue;
	}

	public String getSignalId() {
		return signalId;
	}

	public void setSignalId(String signalId) {
		this.signalId = signalId;
	}

	public String getDatetime() {
		return datetime;
	}

	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}
	
	public String getSignalValue() {
		return signalValue;
	}
	
	public void setSignalValue(String signalValue) {
		this.signalValue = signalValue;
	}
}
