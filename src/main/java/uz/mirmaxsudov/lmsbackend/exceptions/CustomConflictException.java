package uz.mirmaxsudov.lmsbackend.exceptions;

public class CustomConflictException extends RuntimeException{
    public CustomConflictException(String message) {
        super(message);
    }
}
