package top.alazeprt.util;

public class Result<E> {
    
    private final ResultType type;
    
    private final E data;

    private final Exception exception;
    
    public Result(ResultType type) {
        this.type = type;
        this.data = null;
        this.exception = null;
    }
    
    public Result(ResultType type, E data) {
        this.type = type;
        this.data = data;
        this.exception = null;
    }

    public Result(ResultType type, Exception exception) {
        this.type = type;
        this.data = null;
        this.exception = exception;
    }

    public E getData() {
        return data;
    }

    public ResultType getType() {
        return type;
    }

    public Exception getException() {
        return exception;
    }

}
