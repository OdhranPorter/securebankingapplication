public class Validation {
    public static void validateUsername(String u) throws ValidationException {
        // check username is not null and has minimum length and allowed chars
        if (u == null || u.length() < 3 || !u.matches("^[a-zA-Z0-9_]+$"))
            throw new ValidationException("Username must be ≥3 chars letters digits _ only");
    }

    public static void validatePassword(String p) throws ValidationException {
        // check password is not null and meets minimum length
        if (p == null || p.length() < 6)
            throw new ValidationException("Password must be ≥6 chars");
    }

    public static void validateAmount(double amt) throws ValidationException {
        // check amount is positive value
        if (amt <= 0) throw new ValidationException("Amount must be positive");
    }
}