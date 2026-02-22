package uz.mirmaxsudov.lmsbackend.exceptions;

public class ResendLimitExceededException extends RuntimeException {
    public ResendLimitExceededException(String message) {
        super(message);
    }
}
