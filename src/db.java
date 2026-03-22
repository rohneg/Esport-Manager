import java.sql.*;

public class db {
    private static final String URL  = "jdbc:mysql://localhost:3306/esports_db";
    private static final String USER = "root";
    private static final String PASS = "Admin@12345$";

    public static Connection getConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(URL, USER, PASS);
        } catch (Exception e) {
            System.out.println("DB Error: " + e.getMessage());
            return null;
        }
    }
}