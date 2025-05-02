import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    public boolean createUser(String username, String password) throws SQLException {
        // generate random salt for this user
        byte[] salt = Password.generateSalt();
        // hash the password with salt
        byte[] hash = Password.hashPassword(password.toCharArray(), salt);
        // sql to insert new user record
        String sql = "INSERT INTO users (username, password, salt) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // set username value
            ps.setString(1, username);
            // set hashed password value
            ps.setString(2, Password.toHex(hash));
            // set salt value
            ps.setString(3, Password.toHex(salt));
            // execute insert and return result
            return ps.executeUpdate() == 1;
        }
    }

    public int login(String username, String password) throws SQLException {
        // sql to get stored credentials
        String sql = "SELECT id, password, salt FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // set username value
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // get stored password hash hex
                    String storedHashHex = rs.getString("password");
                    // get stored salt hex
                    String saltHex = rs.getString("salt");
                    // convert hex salt to bytes
                    byte[] salt = Password.fromHex(saltHex);
                    // hash provided password with stored salt
                    byte[] hash = Password.hashPassword(password.toCharArray(), salt);
                    // compare generated hash to stored hash
                    if (Password.toHex(hash).equals(storedHashHex)) {
                        // return user id if match
                        return rs.getInt("id");
                    }
                }
            }
        }
        // return invalid id if login fails
        return -1;
    }

    public double getBalance(int userId) throws SQLException {
        // sql to get user balance
        String sql = "SELECT balance FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // set user id value
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // return balance from record
                    return rs.getDouble("balance");
                }
            }
        }
        // return zero if no record found
        return 0.0;
    }

    public boolean updateBalance(int userId, double newBalance) throws SQLException {
        // sql to update user balance
        String sql = "UPDATE users SET balance = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            // set new balance value
            ps.setDouble(1, newBalance);
            // set user id value
            ps.setInt(2, userId);
            // execute update and return result
            return ps.executeUpdate() == 1;
        }
    }
}
