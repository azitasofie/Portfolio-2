package Portfolio2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseViewer { // This class is used to view the database
    public static void main(String[] args) {
        Connection connection = null; 
        try {
            connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite"); // Connect to the database
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30); 

            // Display Vessel Information
            System.out.println("Vessel Information:");
            ResultSet rsVessel = statement.executeQuery("SELECT * FROM Vessel"); // Executing the query
            while (rsVessel.next()) { // Iterate through the results
                System.out.println("Vessel ID = " + rsVessel.getInt("VesselID"));
                System.out.println("Name = " + rsVessel.getString("Name"));
                System.out.println("Capacity = " + rsVessel.getInt("Capacity"));
                System.out.println();
            }

            // Displaying Voyage Information
            System.out.println("Voyage Information:");
            ResultSet rsVoyage = statement.executeQuery("SELECT * FROM Voyage"); 
            while (rsVoyage.next()) { 
                System.out.println("Voyage ID = " + rsVoyage.getInt("VoyageID"));
                System.out.println("Departure Port = " + rsVoyage.getString("DeparturePort"));
                System.out.println("Arrival Port = " + rsVoyage.getString("ArrivalPort"));
                System.out.println("Start Date = " + rsVoyage.getString("StartDate"));
                System.out.println("End Date = " + rsVoyage.getString("EndDate"));
                System.out.println("Vessel ID = " + rsVoyage.getInt("VesselID"));
                System.out.println();
            }

            // Displaying Shipment Information
            System.out.println("Shipment Information:");
            ResultSet rsShipment = statement.executeQuery("SELECT * FROM Shipment");
            while (rsShipment.next()) { 
                System.out.println("Shipment ID = " + rsShipment.getInt("ShipmentID"));
                System.out.println("Voyage ID = " + rsShipment.getInt("VoyageID"));
                System.out.println("Cargo Size = " + rsShipment.getInt("CargoSize"));
                System.out.println();
            }

        } catch (SQLException e) { // Catching any errors
            System.err.println(e.getMessage()); // Print error messages if any errors has been found
        } finally {
            try { // Trying to close the connection
                if (connection != null) { // If the connection is not null
                    connection.close(); // Close the connection
                }
            } catch (SQLException e) { // Catching any errors
                System.err.println(e.getMessage()); // Printing the error message
            }
        }
    }
}
