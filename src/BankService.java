import java.sql.SQLException;

public class BankService {
    private final User user;

    public BankService(User user) {
        this.user = user;
    }

    public boolean createAccount(String username, String password) throws ValidationException, SQLException {
        Validation.validateUsername(username);
        Validation.validatePassword(password);
        return user.createUser(username, password);
    }

    public int login(String username, String password) throws ValidationException, SQLException {
        Validation.validateUsername(username);
        Validation.validatePassword(password);
        return user.login(username, password);
    }

    public double getBalance(int userId) throws SQLException {
        return user.getBalance(userId);
    }

    public double deposit(int userId, double amount) throws ValidationException, SQLException {
        Validation.validateAmount(amount);
        double newBalance = user.getBalance(userId) + amount;
        user.updateBalance(userId, newBalance);
        return newBalance;
    }

    public double withdraw(int userId, double amount) throws ValidationException, SQLException {
        Validation.validateAmount(amount);
        double current = user.getBalance(userId);
        if (amount > current) throw new ValidationException("Insufficient funds");
        double newBalance = current - amount;
        user.updateBalance(userId, newBalance);
        return newBalance;
    }
}