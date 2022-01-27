package uk.ac.mmu.advprog.hackathon;

/**
 * FrequentlyUsedClass creates a new object to store each result entry from an SQL query in the database, which will then be used to display in XML format
 * @author Daniel Lord (12075327)
 *
 */
public class FrequentlyUsedClass {
	
	String signalValue;
	int signalValueCount;
	
	/**
	 * Constructs a new object to store each SQL query result row
	 * @param signalValue The signal_value column in the database results will be stored in this parameter
	 * @param signalValueCount The signal_value column is counted in the database results and will be stored in this parameter
	 */
	public FrequentlyUsedClass(String signalValue, int signalValueCount) {
		this.signalValue = signalValue;
		this.signalValueCount = signalValueCount;
	}

	public String getSignalValue() {
		return signalValue;
	}

	public void setSignalValue(String signalValue) {
		this.signalValue = signalValue;
	}

	public int getSignalValueCount() {
		return signalValueCount;
	}

	public void setSignalValueCount(int signalValueCount) {
		this.signalValueCount = signalValueCount;
	}
}
