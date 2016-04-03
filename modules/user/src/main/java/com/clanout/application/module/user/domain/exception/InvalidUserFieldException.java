package com.clanout.application.module.user.domain.exception;

public class InvalidUserFieldException extends Exception
{
    private String fieldName;

    public InvalidUserFieldException(String fieldName)
    {
        this.fieldName = fieldName;
    }

    @Override
    public String getMessage()
    {
        return "Invalid " + fieldName;
    }
}
