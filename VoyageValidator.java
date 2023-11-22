package Portfolio2;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class VoyageValidator {

    public static void main(String[] args) {
        Connection connection = null;
        try {
            // Connect to the database
            connection = DriverManager.getConnection("jdbc:sqlite:identifier.sqlite");
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // Part 3: Verify Cargo Capacity
            verifyCargoCapacity(statement);

            // Part 4: Check for Overlapping Voyages
            checkOverlappingVoyages(statement);

        } catch (SQLException e) {
            System.err.println(e.getMessage());
        } finally {
            try {
                if (connection != null)
                    connection.close();
            } catch (SQLException e) {
                System.err.println(e.getMessage());
            }
        }
    }


    private static void verifyCargoCapacity(Statement statement) throws SQLException {
        System.out.println("Checking Cargo Capacities for Voyages where the total is smaller than vessel capacity:");
        ResultSet rs = statement.executeQuery(
                "SELECT v.VoyageID, SUM(s.CargoSize) AS TotalCargo, ves.Capacity " +
                        "FROM Voyage v " +
                        "JOIN Shipment s ON v.VoyageID = s.VoyageID " +
                        "JOIN Vessel ves ON v.VesselID = ves.VesselID " +
                        "GROUP BY v.VoyageID " +
                        "HAVING TotalCargo > ves.Capacity"
        );

        while (rs.next()) {
            System.out.println("Voyage ID: " + rs.getInt("VoyageID") +
                    ", Total Cargo: " + rs.getInt("TotalCargo") +
                    ", Capacity: " + rs.getInt("Capacity"));
        }
    }

    // We want to find voyages that are using the same vessel and have overlapping dates which would be like schedueling two events at the same time.
    private static void checkOverlappingVoyages(Statement statement) throws SQLException {
        System.out.println("\nChecking for Overlapping Voyages (same ship, same date):");
        ResultSet rs = statement.executeQuery(
                "SELECT v1.VesselID, v1.VoyageID AS VoyageID1, v1.StartDate AS StartDate1, v1.EndDate AS EndDate1, " +
                        "v2.VoyageID AS VoyageID2, v2.StartDate AS StartDate2, v2.EndDate AS EndDate2 " +

                        "FROM Voyage v1, Voyage v2 " +
                        "WHERE v1.VesselID = v2.VesselID AND v1.VoyageID <> v2.VoyageID " + // v1.VoyageID <> v2.VoyageID is making sure that we won't compare the same voyage with itself
                        "AND (v1.StartDate BETWEEN v2.StartDate AND v2.EndDate OR v1.EndDate BETWEEN v2.StartDate AND v2.EndDate)" // This is for overlapping dates.
        );

        while (rs.next()) {
            System.out.println("Vessel ID: " + rs.getInt("VesselID") +
                    ", Voyage ID 1: " + rs.getInt("VoyageID1") +
                    ", Start Date: " + rs.getString("StartDate1") +
                    ", End Date: " + rs.getString("EndDate1") +
                    ", Voyage ID 2: " + rs.getInt("VoyageID2") +
                    ", Start Date: " + rs.getString("StartDate2") +
                    ", End Date: " + rs.getString("EndDate2"));
        }
    }
}

