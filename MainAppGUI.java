package Portfolio2;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

// Main GUI class
public class MainAppGUI extends Application {

    // Connect to the database
    private Connection connect() {
        String url = "jdbc:sqlite:identifier.sqlite";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url); // Create a connection to the database
        } catch (SQLException e) {
            System.out.println(e.getMessage()); 
        }
        return conn;
    }

    @Override
    public void start(Stage primaryStage) {
        // Create the main layout
        GridPane grid = new GridPane(); // GridPane is a layout that arranges components in a grid
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10); // Horizontal gap between components
        grid.setVgap(10); // Vertical gap between components
        grid.setPadding(new Insets(25, 25, 25, 25)); // Padding around the grid

        // Adding components
        Label lblDeparturePort = new Label("Departure Port:"); // Label 
        grid.add(lblDeparturePort, 0, 1); // Add the label to the grid

        ComboBox<String> cbDeparturePort = new ComboBox<>(); // ComboBox 
        cbDeparturePort.getItems().addAll("Port A", "Port B", "Port C"); // Add items to the dropdown
        grid.add(cbDeparturePort, 1, 1); // Add the dropdown to the grid

        Label lblArrivalPort = new Label("Arrival Port:"); 
        grid.add(lblArrivalPort, 0, 2); 

        ComboBox<String> cbArrivalPort = new ComboBox<>(); 
        cbArrivalPort.getItems().addAll("Port D", "Port E", "Port F"); 
        grid.add(cbArrivalPort, 1, 2); 

        Label lblDate = new Label("Departure Date:"); 
        grid.add(lblDate, 0, 3); 

        DatePicker dpDate = new DatePicker(); // DatePicker 
        grid.add(dpDate, 1, 3); // Add the date picker to the grid

        Label lblContainers = new Label("Number of Containers:"); 
        grid.add(lblContainers, 0, 4); 

        TextField txtContainers = new TextField(); // TextField 
        grid.add(txtContainers, 1, 4); // Add the text field to the grid

        Button btnSearch = new Button("Search for Vessels"); // Button 
        grid.add(btnSearch, 1, 5); // Add the button to the grid

        Button btnBook = new Button("Book Shipment"); 
        grid.add(btnBook, 1, 6);   

        TextArea txtAreaResults = new TextArea(); // TextArea 
        grid.add(txtAreaResults, 0, 7, 2, 1); // Add the text area to the grid

        // Search Button Event Handler
        btnSearch.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) { // This method is called when the button is clicked
                String departurePort = cbDeparturePort.getValue(); // Get the chosen departure port
                String arrivalPort = cbArrivalPort.getValue(); // Get the chosen arrival port
                LocalDate date = dpDate.getValue(); // Get the chosen date

                // Check if the date is null
                if (departurePort == null || arrivalPort == null || date == null) {
                    showAlert(Alert.AlertType.ERROR, "Search Error", "Please select both departure and arrival ports and a date."); // Show an error message if the departure port, arrival port or date hasn't been chosen.
                    return;
                }

                List<String> availableVoyages = searchAvailableVoyages(departurePort, arrivalPort, date); // Search for available voyages
                txtAreaResults.clear(); // Clear the text area
                for (String voyage : availableVoyages) { // Go through the available voyages
                    txtAreaResults.appendText(voyage + "\n"); // Add the voyage information to the text area
                }
            }
        });

        // Booking Button Event Handler
        btnBook.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent e) { // This method is called when the button is clicked
                try {
                    String departurePort = cbDeparturePort.getValue(); // Get the chosen departure port
                    String arrivalPort = cbArrivalPort.getValue(); // Get the chosen arrival port
                    LocalDate date = dpDate.getValue(); // Get the chosen date
                    int containerCount = Integer.parseInt(txtContainers.getText()); // Get the number of containers

                    if (containerCount <= 0) { // Check if the number of containers is less or equal to 0
                        showAlert(Alert.AlertType.ERROR, "Booking Error", "Number of containers must be greater than 0.");
                        return;
                    }

                    int voyageId = getVoyageId(departurePort, arrivalPort, date); // Get the voyage ID

                    if (voyageId != -1) { // Check if a voyage has been found
                        bookShipment(voyageId, containerCount, date); // Book the shipment
                    } else { // No voyage was found
                        showAlert(Alert.AlertType.ERROR, "Booking Error", "No available voyage found for the given criteria."); // Show an error message
                    }
                } catch (NumberFormatException ex) { // Catch any number format errors
                    showAlert(Alert.AlertType.ERROR, "Input Error", "Invalid number of containers."); // Show an error message
                } catch (SQLException ex) { // Catch database errors
                    showAlert(Alert.AlertType.ERROR, "Database Error", "Error occurred while booking shipment: " + ex.getMessage()); // Show an error message
                }
            }
        });

        // Setting the scene
        Scene scene = new Scene(grid, 400, 475); // Make a scene with the grid as the root 
        primaryStage.setScene(scene); 
        primaryStage.setTitle("Shipment Booking System"); // Make a title 
        primaryStage.show(); // Show the stage
    }

    private int getVoyageId(String departurePort, String arrivalPort, LocalDate date) throws SQLException {
        String sql = "SELECT VoyageID FROM Voyage WHERE DeparturePort = ? AND ArrivalPort = ? AND StartDate <= ? AND EndDate >= ?"; // SQL query to get the voyage ID
        try (Connection conn = this.connect(); // Connect to the database
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // Create a prepared statement
            pstmt.setString(1, departurePort); // Set the first parameter to the departure port
            pstmt.setString(2, arrivalPort); // Set the second parameter to the arrival port
            pstmt.setString(3, date.toString()); // Set the third parameter to the date
            pstmt.setString(4, date.toString()); // Set the fourth parameter to the date

            ResultSet rs = pstmt.executeQuery(); // Execute the query and get the result
            if (rs.next()) { // Check if a voyage has been found
                return rs.getInt("VoyageID"); // Return the voyage ID
            }
        }
        return -1; // Return -1 if the voyage wasn't found
    }

    // Show an alert dialog
    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType); 
        alert.setTitle(title); // Set the title
        alert.setHeaderText(null); // Set the header text
        alert.setContentText(message); // Set the content text
        alert.showAndWait(); // Show the alert dialog
    }

    // Search for available voyages
    private List<String> searchAvailableVoyages(String departurePort, String arrivalPort, LocalDate date) {
        String sql = "SELECT VoyageID, StartDate, EndDate FROM Voyage WHERE DeparturePort = ? AND ArrivalPort = ? AND StartDate <= ? AND EndDate >= ?"; // SQL query to get the available voyages
        List<String> voyages = new ArrayList<>(); // List to store the available voyages

        try (Connection conn = this.connect(); // Connect to the database
             PreparedStatement pstmt  = conn.prepareStatement(sql)){ // Create a prepared statement

            pstmt.setString(1, departurePort); // Set the first parameter to the departure port
            pstmt.setString(2, arrivalPort); // Set the second parameter to the arrival port
            pstmt.setString(3, date.toString()); // Set the third parameter to the date
            pstmt.setString(4, date.toString()); // Set the fourth parameter to the date

            ResultSet rs  = pstmt.executeQuery(); // Execute the query and get the result 
            while (rs.next()) { // Loop through the result set
                String voyageInfo = "Voyage ID: " + ((ResultSet) rs).getInt("VoyageID") + ", Start Date: " + rs.getString("StartDate") + ", End Date: " + rs.getString("EndDate"); // Get the voyage information
                voyages.add(voyageInfo); // Add the voyage information to the list
            }
        } catch (SQLException e) { // Catch any database errors
            System.out.println(e.getMessage()); // Print any errors
        }
        return voyages; // Return the list of available voyages
    }

    private boolean isVoyageCapacityExceeded(int voyageId, int containerCount) throws SQLException { // Check if the voyage capacity is exceeded
        String sql = "SELECT SUM(CargoSize) AS TotalCargo, ves.Capacity " +
                "FROM Shipment s JOIN Voyage v ON s.VoyageID = v.VoyageID " +
                "JOIN Vessel ves ON v.VesselID = ves.VesselID " +
                "WHERE s.VoyageID = ?"; // SQL query to get the total cargo and vessel capacity

        try (Connection conn = this.connect(); // Connect to the database
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // Create a prepared statement
            pstmt.setInt(1, voyageId); // Set the parameter to the voyage ID

            ResultSet rs = pstmt.executeQuery(); // Execute the query and get the result set
            if (rs.next()) {    // Check if a voyage was found
                int totalCargo = rs.getInt("TotalCargo") + containerCount; // Get the total cargo
                int capacity = rs.getInt("Capacity"); // Get the vessel capacity
                return totalCargo > capacity; // Return true if the total cargo is greater than the capacity
            }
        }
        return false; // Return false if no voyage was found
    }

    private boolean isVoyageOverlapping(int voyageId, LocalDate date) throws SQLException { // Check if the voyage is overlapping
        String sql = "SELECT COUNT(*) AS OverlapCount FROM Voyage v1 " +
                "JOIN Voyage v2 ON v1.VesselID = v2.VesselID " +
                "WHERE v1.VoyageID = ? AND v2.VoyageID != ? AND ? BETWEEN v2.StartDate AND v2.EndDate"; // SQL query to check for overlapping voyages

        try (Connection conn = this.connect(); // Connect to the database
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // Create a prepared statement
            pstmt.setInt(1, voyageId); // Set the first parameter to the voyage ID
            pstmt.setInt(2, voyageId); // Set the second parameter to the voyage ID
            pstmt.setString(3, date.toString()); // Set the third parameter to the date

            ResultSet rs = pstmt.executeQuery(); // Execute the query and get the result set
            return rs.getInt("OverlapCount") > 0; // Return true if the overlap count is greater than 0
        }
    }

    private void bookShipment(int voyageId, int containerCount, LocalDate date) throws SQLException {
        if (isVoyageCapacityExceeded(voyageId, containerCount)) { // Check if the voyage capacity is exceeded
            showAlert(Alert.AlertType.ERROR, "Booking Error", "Voyage capacity exceeded. Cannot book shipment.");
            return; // Return if the capacity is exceeded
        }

        if (isVoyageOverlapping(voyageId, date)) { // Check if the voyage is overlapping
            showAlert(Alert.AlertType.ERROR, "Booking Error", "Voyage dates are overlapping with another voyage.");
            return; // Return if the voyage is overlapping
        }

        // Proceed with booking if checks pass
        String sql = "INSERT INTO Shipment (VoyageID, CargoSize) VALUES (?, ?)"; // SQL query to book the shipment
        try (Connection conn = this.connect(); // Connect to the database
             PreparedStatement pstmt = conn.prepareStatement(sql)) { // Create a prepared statement
            pstmt.setInt(1, voyageId); // Set the first parameter to the voyage ID
            pstmt.setInt(2, containerCount); // Set the second parameter to the number of containers
            pstmt.executeUpdate(); // Execute the query
            showAlert(Alert.AlertType.INFORMATION, "Booking", "Shipment booked successfully for Voyage ID: " + voyageId); // Show a success message
        }
    }

    public static void main(String[] args) { // Main method
        launch(args); // Launch the application
    }
