package com.example.schoolservlet.exceptions;

public class NotFoundException extends SchoolServletException{
    private String table;
    private String field;
    private String value;
    public NotFoundException(String table, String field, String value){
        super(String.format("Nenhum(a) %s contendo o campo %s de valor %s foi encontrado(a)",table, field, value));
        this.table = table;
        this.field = field;
        this.value = value;
    }

    public String getTable() {
        return table;
    }

    public String getField() {
        return field;
    }

    public String getValue() {
        return value;
    }
}