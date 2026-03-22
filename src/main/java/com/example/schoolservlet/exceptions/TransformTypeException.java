package com.example.schoolservlet.exceptions;

/**
 * Exception thrown when a value cannot be converted to a specified type.
 *
 * <p>This exception extends ValidationException and provides a message indicating
 * the value that failed conversion and the target type.</p>
 *
 * @see ValidationException
 */
public class TransformTypeException extends ValidationException{
    /**
     * Creates a new exception indicating that a value could not be converted
     * to the target representation/type.
     *
     * @param value the source value that failed conversion
     * @param to the target type/representation
     */
    public TransformTypeException(String value, String to){
        super(String.format("Não foi possível converter %s para a forma de %s", value, to));
    }
}