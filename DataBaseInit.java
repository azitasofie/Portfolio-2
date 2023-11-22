package Portfolio2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBaseInit { // This class is used to initialize the database
    public static void main(String[] args) {
        Connection connection = null; 
        try {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite"); // Connect to the database
            Statement statement = connection.createStatement(); 
            statement.setQueryTimeout(30);  // set a timeout to 30 for the program to have time enough to build

            // Execute SQL scripts 
            createTables(statement); 
            insertSampleData(statement); 

        } catch (SQLException e) { // Catch any errors
            System.err.println(e.getMessage()); 
        } finally {
            try { // Try to close the connection
                if (connection != null) { 
                    connection.close(); 
                }
            } catch (SQLException e) { // Catch any errors
                System.err.println(e.getMessage()); 
            }
        }
    }

    private static void createTables(Statement statement) throws SQLException { // This method is used to create the tables
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS Vessel (VesselID INTEGER PRIMARY KEY, Name TEXT NOT NULL, Capacity INTEGER NOT NULL)");
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS Voyage (VoyageID INTEGER PRIMARY KEY, DeparturePort TEXT NOT NULL, ArrivalPort TEXT NOT NULL, StartDate TEXT NOT NULL, EndDate TEXT NOT NULL, VesselID INTEGER, FOREIGN KEY (VesselID) REFERENCES Vessel(VesselID))");
        statement.executeUpdate("CREATE TABLE IF NOT EXISTS Shipment (ShipmentID INTEGER PRIMARY KEY, VoyageID INTEGER, CargoSize INTEGER NOT NULL, FOREIGN KEY (VoyageID) REFERENCES Voyage(VoyageID))");
    }

    private static void insertSampleData(Statement statement) throws SQLException { // This method is used to insert sample data
        statement.executeUpdate("DELETE FROM Shipment"); // Delete all data from the Shipment table
        statement.executeUpdate("DELETE FROM Voyage"); // Delete all data from the Voyage table
        statement.executeUpdate("DELETE FROM Vessel"); // Delete all data from the Vessel table

        // Inserting data into the Vessel table
        statement.executeUpdate("INSERT INTO Vessel (VesselID, Name, Capacity) VALUES (1, 'Vessel A', 1000)");
        statement.executeUpdate("INSERT INTO Vessel (VesselID, Name, Capacity) VALUES (2, 'Vessel B', 1500)");
        statement.executeUpdate("INSERT INTO Vessel (VesselID, Name, Capacity) VALUES (3, 'Vessel C', 800)");

        // Inserting data into the Voyage table
        statement.executeUpdate("INSERT INTO Voyage (VoyageID, DeparturePort, ArrivalPort, StartDate, EndDate, VesselID) VALUES (1, 'Port A', 'Port D', '2023-01-01', '2023-01-10', 1)");
        statement.executeUpdate("INSERT INTO Voyage (VoyageID, DeparturePort, ArrivalPort, StartDate, EndDate, VesselID) VALUES (2, 'Port B', 'Port E', '2023-02-01', '2023-02-10', 2)");
        statement.executeUpdate("INSERT INTO Voyage (VoyageID, DeparturePort, ArrivalPort, StartDate, EndDate, VesselID) VALUES (3, 'Port C', 'Port F', '2023-03-01', '2023-03-10', 3)");
        statement.executeUpdate("INSERT INTO Voyage (VoyageID, DeparturePort, ArrivalPort, StartDate, EndDate, VesselID) VALUES (4, 'Port A', 'Port E', '2023-04-01', '2023-04-10', 1)");
        statement.executeUpdate("INSERT INTO Voyage (VoyageID, DeparturePort, ArrivalPort, StartDate, EndDate, VesselID) VALUES (5, 'Port B', 'Port D', '2023-05-01', '2023-05-10', 2)");
        // statement.executeUpdate("INSERT INTO Voyage (VoyageID, DeparturePort, ArrivalPort, StartDate, EndDate, VesselID) VALUES (6, 'Port C', 'Port E', '2023-03-01', '2023-03-10', 3)"); // This will cause error as the voyage overlaps with Voyage 3 and uses the same vessel as Voyage 3

        // Inserting data into the Shipment table
        statement.executeUpdate("INSERT INTO Shipment (ShipmentID, VoyageID, CargoSize) VALUES (1, 1, 500)");
        statement.executeUpdate("INSERT INTO Shipment (ShipmentID, VoyageID, CargoSize) VALUES (2, 2, 400)");
        statement.executeUpdate("INSERT INTO Shipment (ShipmentID, VoyageID, CargoSize) VALUES (3, 3, 300)");
        statement.executeUpdate("INSERT INTO Shipment (ShipmentID, VoyageID, CargoSize) VALUES (4, 4, 200)");
        statement.executeUpdate("INSERT INTO Shipment (ShipmentID, VoyageID, CargoSize) VALUES (5, 5, 100)");
//        statement.executeUpdate("INSERT INTO Shipment (ShipmentID, VoyageID, CargoSize) VALUES (6, 1, 600)"); // This will cause error as well cause the total cargo size is larger than the vessel capacity for Voyage 1

    }
}
