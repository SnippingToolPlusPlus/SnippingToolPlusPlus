package com.shaneisrael.st.imgur;

public class ImgurException extends Exception
{
    private static final long serialVersionUID = 3859834339072893490L;

    private int httpStatusCode;

    public ImgurException(String message, int httpStatusCode)
    {
        super(message);
        this.httpStatusCode = httpStatusCode;
    }

    public int getHttpStatusCode()
    {
        return httpStatusCode;
    }
}
