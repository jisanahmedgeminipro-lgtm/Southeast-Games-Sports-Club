package bd.edu.seu.gamesclub.util;

import java.security.SecureRandom;

/**
 * Generates cryptographically-strong numeric one-time passwords.
 */
public final class OtpGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    private OtpGenerator() {
    }

    /**
     * @param length number of digits
     * @return a zero-padded numeric OTP of the requested length
     */
    public static String generate(int length) {
        int bound = (int) Math.pow(10, length);
        int code = RANDOM.nextInt(bound);
        return String.format("%0" + length + "d", code);
    }
}
