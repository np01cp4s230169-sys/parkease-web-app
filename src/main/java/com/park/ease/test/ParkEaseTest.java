package com.park.ease.test;

import java.sql.Connection;
import java.sql.DriverManager;

public class ParkEaseTest {
    public static void main(String[] args) {
        // Updated URL to match your "parkease" database name
        String url = "jdbc:mysql://localhost:3306/parkease"; 
        String user = "root";
        String pass = ""; // Default for XAMPP/phpMyAdmin

        try {
            // Load the MySQL Driver
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Attempt the connection
            Connection con = DriverManager.getConnection(url, user, pass);
            
            if (con != null) {
                System.out.println("✅ SUCCESS: Connected to the 'parkease' database!");
                con.close(); // Good practice to close test connections
            }
            
        } catch (ClassNotFoundException e) {
            System.out.println("❌ FAILED: Driver not found! Check your pom.xml.");
        } catch (Exception e) {
            System.out.println("❌ FAILED: " + e.getMessage());
        }
    }
}
