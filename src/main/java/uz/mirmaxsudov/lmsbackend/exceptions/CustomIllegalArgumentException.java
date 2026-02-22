package uz.mirmaxsudov.lmsbackend.exceptions;

public class CustomIllegalArgumentException extends RuntimeException {
    public CustomIllegalArgumentException(String message) {
        super(message);
    }
}
