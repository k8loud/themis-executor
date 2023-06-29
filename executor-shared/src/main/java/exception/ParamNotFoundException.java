package exception;

public class ParamNotFoundException extends RuntimeException {
    public ParamNotFoundException() {
    }

    public ParamNotFoundException(String message) {
        super(message);
    }
}
