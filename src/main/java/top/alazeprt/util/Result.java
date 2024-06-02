package top.alazeprt.util;

/**
 * Represents the result of an operation
 *
 * @author alazeprt
 * @version 1.2
 */
public class Result<E> {

    private final ResultType type;
    
    private final E data;

    private final Exception exception;

    /**
     * Constructing Result by result type
     *
     * @param type the result type of the operation
     */
    public Result(ResultType type) {
        this.type = type;
        this.data = null;
        this.exception = null;
    }

    /**
     * Constructing Result by result type and data
     *
     * @param type the result type of the operation
     * @param data the return data of the operation
     */
    public Result(ResultType type, E data) {
        this.type = type;
        this.data = data;
        this.exception = null;
    }

    /**
     * Constructing Result by result type and exception
     *
     * @param type the result type of the operation
     * @param exception the exception to be thrown in the operation
     */
    public Result(ResultType type, Exception exception) {
        this.type = type;
        this.data = null;
        this.exception = exception;
    }

    /**
     * Get the return data
     *
     * @return the return data
     */
    public E getData() {
        return data;
    }

    /**
     * Get the result type
     *
     * @return the result type
     */
    public ResultType getType() {
        return type;
    }

    /**
     * Get the exception
     *
     * @return the exception
     */
    public Exception getException() {
        return exception;
    }

}
