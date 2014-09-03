package com.shaneisrael.st.imgur;

public interface ImgurResponseListener
{
    public void onImgurResponseSuccess(ImgurImage uploadedImage);

    public void onImgurResponseFail(ImgurResponse response);
}
