package exception;

public class SunriseException extends Exception {
    private final String field;
    private final String value;
    private final int statusCode;

    public SunriseException(int statusCode, String field, String value, String message) {
        super(message);
        this.field = field;
        this.value = value;
        this.statusCode = statusCode;
    }

    public String getField() {
        return field;
    }
    public String getValue() {
        return value;
    }
    public int getStatusCode() {
        return statusCode;
    }
}
