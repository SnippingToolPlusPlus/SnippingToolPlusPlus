package com.shaneisrael.st.upload;

public class UploadException extends Exception
{
    private static final long serialVersionUID = -7823592681999762352L;
    private int httpStatusCode;

    public UploadException(String message, int httpStatusCode)
    {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode()
    {
        return httpStatusCode;
    }
}
