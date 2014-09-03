package com.shaneisrael.st.upload;

public interface UploadListener
{
    public void onUploadSuccess(String content);

    public void onUploadFail(int httpStatus, String reason);
}
