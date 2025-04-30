import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class User {
    public boolean createUser(String username, String password) throws SQLException {
        byte[] salt = Password.generateSalt();
        byte[] hash = Password.hashPassword(password.toCharArray(), salt);
        String sql = "INSERT INTO users (username, password, salt) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, Password.toHex(hash));
            ps.setString(3, Password.toHex(salt));
            return ps.executeUpdate() == 1;
        }
    }

    public int login(String username, String password) throws SQLException {
        String sql = "SELECT id, password, salt FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHashHex = rs.getString("password");
                    String saltHex = rs.getString("salt");
                    byte[] salt = Password.fromHex(saltHex);
                    byte[] hash = Password.hashPassword(password.toCharArray(), salt);
                    if (Password.toHex(hash).equals(storedHashHex)) {
                        return rs.getInt("id");
                    }
                }
            }
        }
        return -1;
    }

    public double getBalance(int userId) throws SQLException {
        String sql = "SELECT balance FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("balance");
                }
            }
        }
        return 0.0;
    }

    public boolean updateBalance(int userId, double newBalance) throws SQLException {
        String sql = "UPDATE users SET balance = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newBalance);
            ps.setInt(2, userId);
            return ps.executeUpdate() == 1;
        }
    }
}