package com.example.schoolservlet.exceptions;

public class NotFoundException extends SchoolServletException{
    private String table;
    private String field;
    private String value;
    public NotFoundException(String table, String field, String value){
        super(String.format("O valor %s do campo %s não foi encontrado na tabela %s", value, field, table));
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