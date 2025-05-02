// handles jdbc driver loading and connection acquisition
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // database url with parameters
    private static final String URL =
        "jdbc:mysql://localhost:3306/banking_app"
      + "?useSSL=false"
      + "&allowPublicKeyRetrieval=true"
      + "&serverTimezone=UTC";
    // database user credentials
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    static {
        // load jdbc driver on class load
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("jdbc driver loaded successfully");
        } catch (ClassNotFoundException e) {
            System.err.println("error loading jdbc driver " + e.getMessage());
        }
    }

    private DBConnection() {
        // prevent instantiation of utility class
    }

    public static Connection getConnection() throws SQLException {
        // obtain and return database connection
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
