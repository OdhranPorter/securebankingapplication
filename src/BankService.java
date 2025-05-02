import java.sql.SQLException;

public class BankService {
    // underlying user data access object
    private final User user;

    public BankService(User user) {
        // initialize service with user dao
        this.user = user;
    }

    public boolean createAccount(String username, String password) throws ValidationException, SQLException {
        // validate input username
        Validation.validateUsername(username);
        // validate input password
        Validation.validatePassword(password);
        // create new user record
        return user.createUser(username, password);
    }

    public int login(String username, String password) throws ValidationException, SQLException {
        // validate input username
        Validation.validateUsername(username);
        // validate input password
        Validation.validatePassword(password);
        // perform login and return user id
        return user.login(username, password);
    }

    public double getBalance(int userId) throws SQLException {
        // retrieve user balance
        return user.getBalance(userId);
    }

    public double deposit(int userId, double amount) throws ValidationException, SQLException {
        // validate deposit amount
        Validation.validateAmount(amount);
        // calculate new balance
        double newBalance = user.getBalance(userId) + amount;
        // update stored balance
        user.updateBalance(userId, newBalance);
        // return updated balance
        return newBalance;
    }

    public double withdraw(int userId, double amount) throws ValidationException, SQLException {
        // validate withdrawal amount
        Validation.validateAmount(amount);
        // get current balance
        double current = user.getBalance(userId);
        // check sufficient funds
        if (amount > current) throw new ValidationException("Insufficient funds");
        // calculate new balance
        double newBalance = current - amount;
        // update stored balance
        user.updateBalance(userId, newBalance);
        // return updated balance
        return newBalance;
    }
}
