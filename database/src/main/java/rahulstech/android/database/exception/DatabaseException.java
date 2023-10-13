package rahulstech.android.database.exception;

import android.util.AndroidRuntimeException;

public class DatabaseException extends AndroidRuntimeException {


    public static final int DATATYPE_CONVERSION = 1;

    private final int code;

    public DatabaseException(int code) {
        super();
        this.code = code;
    }

    public DatabaseException(int code, String message) {
        super(message);
        this.code = code;
    }

    public DatabaseException(int code, Exception cause) {
        super(cause);
        this.code = code;
    }
}
