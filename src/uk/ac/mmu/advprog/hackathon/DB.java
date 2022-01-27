package uk.ac.mmu.advprog.hackathon;

import java.sql.*;
import java.util.ArrayList;

/**
 * Handles database access from within the web service
 * @author Daniel Lord (12075327)
 */
public class DB implements AutoCloseable {
	
	//allows us to easily change the database used
	private static final String JDBC_CONNECTION_STRING = "jdbc:sqlite:./data/AMI.db";
	
	//allows us to re-use the connection between queries if desired
	private Connection connection = null;
	
	/**
	 * Creates an instance of the DB object and connects to the database
	 */
	public DB() {
		try {
			connection = DriverManager.getConnection(JDBC_CONNECTION_STRING);
		}
		catch (SQLException sqle) {
			error(sqle);
		}
	}
	
	/**
	 * Returns the number of entries in the database, by counting rows
	 * @return The number of entries in the database, or -1 if empty
	 */
	public int getNumberOfEntries() {
		int result = -1;
		try {
			Statement s = connection.createStatement();
			ResultSet results = s.executeQuery("SELECT COUNT(*) AS count FROM ami_data");
			while(results.next()) { //will only execute once, because SELECT COUNT(*) returns just 1 number
				result = results.getInt(results.findColumn("count"));
			}
		}
		catch (SQLException sqle) {
			error(sqle);
			
		}
		return result;
	}
	
	/**
	 * Returns the signal value from the database using the signalid parameter, taken from the web service
	 * @param signalid The signalid to search in the database
	 * @return The signal value from the database, or display "No results" if signalid parameter is not found in the database
	 */
	public String getLastSignalDisplayed(String signalid) {
		String result = "No results";	//if signalid input doesn't exist in database, result will display "No results"
		try {
			String sqlQuery = "SELECT signal_value FROM ami_data WHERE signal_id = ? AND NOT signal_value = \"OFF\" AND NOT signal_value = \"NR\" AND NOT signal_value = \"BLNK\" ORDER BY datetime DESC LIMIT 1;";
			PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ps.setString(1, signalid);
			ResultSet results = ps.executeQuery();
			while(results.next()) {
				result = result.replace("No results", results.getString("signal_value"));	//replaces the result String with the SQL query ResultSet column "signal_value"
			}
			
		}
		catch (SQLException sqle) {
			error(sqle);
		}
		return result;
	}
	
	/**
	 * Returns an ArrayList of FrequencyUsedClass objects, containing the results from the SQL query from the database
	 * @param motorway The motorway input, taken from the web service, to change in the SQL query to collect the different results from the database
	 * @return The results from the SQL query, organised into an ArrayList of FrequencyUsedClass objects
	 */
	public ArrayList<FrequentlyUsedClass> getFrequentlyUsed(String motorway) {
		ArrayList<FrequentlyUsedClass> signalsArray = new ArrayList<>();
		try {
			String sqlQuery = "SELECT COUNT(signal_value) AS frequency, signal_value FROM ami_data WHERE signal_id LIKE ? GROUP BY signal_value ORDER BY frequency DESC;" ;
			PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ps.setString(1, motorway + "%");
			ResultSet results = ps.executeQuery();
			while(results.next()) {
				signalsArray.add(new FrequentlyUsedClass(results.getString("signal_value"), results.getInt("frequency")));	//collects the ResultSet columns "signal_value" and "frequency" results and create a new object to the ArrayList with the respective parameters
			}
		}
		catch (SQLException sqle) {
			error(sqle);
		}
		return signalsArray;
	}
	
	/**
	 * Returns an ArrayList of String objects, containing the results from the SQL query from the database
	 * @return The results from the SQL query, organised into an ArrayList of String objects
	 */
	public ArrayList<String> getSignalGroups() {
		ArrayList<String> signalGroupArray = new ArrayList<>();
		try {
			Statement s = connection.createStatement();
			ResultSet results = s.executeQuery("SELECT DISTINCT signal_group FROM ami_data;");
			while(results.next()) {
				signalGroupArray.add(new String(results.getString("signal_group")));	//collects the ResultSet columns "signal_group" results and create a new object to the ArrayList with the parameters
			}
		}
		catch (SQLException sqle) {
			error(sqle);
		}
		return signalGroupArray;
	}
	
	/**
	 * Returns an ArrayList of SignalsByGroupTimeClass objects, containing the results from the SQL query from the database
	 * @param signal_group The signal_group input, taken from the web service, to change in the SQL query to collect the different results from the database
	 * @param time The time input, taken from the web service, to change in the SQL query to collect the different results from the database
	 * @return The results from the SQL query, organised into an ArrayList of SignalsByGroupTimeClass objects
	 */
	public ArrayList<SignalsByGroupTimeClass> getSignalsByGroupTime(String signal_group, String time) {
		ArrayList<SignalsByGroupTimeClass> signalsGroupArray = new ArrayList<>();
		try {
			String sqlQuery = "SELECT datetime, signal_id, signal_value FROM ami_data WHERE signal_group = ? AND datetime < ? AND (datetime, signal_id) IN (SELECT MAX(datetime) AS datetime, signal_id FROM ami_data WHERE signal_group = ? AND datetime < ? GROUP BY signal_id) ORDER BY signal_id;";
			PreparedStatement ps = connection.prepareStatement(sqlQuery);
			ps.setString(1, signal_group);
			ps.setString(2,  time);
			ps.setString(3,  signal_group);
			ps.setString(4,  time);
			ResultSet results = ps.executeQuery();
			while(results.next()) {
				signalsGroupArray.add(new SignalsByGroupTimeClass(results.getString("signal_id"), results.getString("datetime"), results.getString("signal_value"))); //collects the ResultSet columns "signal_id", "datetime" and "signal_value" results and create a new object to the ArrayList with the respective parameters
			}
			
		}
		catch (SQLException sqle) {
			error(sqle);
		}
		return signalsGroupArray;
	}
	
	/**
	 * Closes the connection to the database, required by AutoCloseable interface.
	 */
	@Override
	public void close() {
		try {
			if ( !connection.isClosed() ) {
				connection.close();
			}
		}
		catch(SQLException sqle) {
			error(sqle);
		}
	}

	/**
	 * Prints out the details of the SQL error that has occurred, and exits the programme
	 * @param sqle Exception representing the error that occurred
	 */
	private void error(SQLException sqle) {
		System.err.println("Problem Opening Database! " + sqle.getClass().getName());
		sqle.printStackTrace();
		System.exit(1);
	}
}
