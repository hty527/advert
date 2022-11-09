package com.platform.lib.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.widget.ImageView;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * created by hty
 * 2022/11/9
 * Desc:图片下载+渲染
 */
public class ImageLoader extends AsyncTask<Object,Integer,Object> {

    private String mUrl;
    private ImageView mTargetImage;
    private int loadPlaceholder,errorPlaceholder;

    public void setLoadPlaceholder(int loadPlaceholder) {
        this.loadPlaceholder = loadPlaceholder;
    }

    public void setErrorPlaceholder(int errorPlaceholder) {
        this.errorPlaceholder = errorPlaceholder;
    }

    public void displayImage(ImageView imageView, Object url) {
        if(null==imageView) return;
        if(null==url) return;
        if(url instanceof Integer){
            imageView.setImageResource((Integer) url);
            return;
        }
        if(url instanceof String){
            this.mTargetImage =imageView;
            this.mUrl= (String) url;
        }
        execute();
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
//        MideaUtils.getInstance().log(TAG,"onPreExecute-->");
        if(null!= mTargetImage &&loadPlaceholder>0){
            mTargetImage.setImageResource(loadPlaceholder);
        }
    }

    @Override
    protected Object doInBackground(Object... objects) {
        if(TextUtils.isEmpty(mUrl)){
            return null;
        }
        try {
            URL url = new URL(mUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);//使用URL进行输入
            connection.setUseCaches(false);//忽略缓存
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.connect();//开始链接
            //获取文件总长度
            int contentLength = connection.getContentLength();
            int responseCode = connection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK&&contentLength>0){
                InputStream inputStream= connection.getInputStream();
                if(null!=inputStream){
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inPreferredConfig = Bitmap.Config.RGB_565;//Bitmap压缩
                    Bitmap bitmap = BitmapFactory.decodeStream(inputStream,null,options);
                    return bitmap;
                }
            }
        } catch (Throwable e) {

        }
        return null;
    }

    @Override
    protected void onPostExecute(Object bitmap) {
        super.onPostExecute(bitmap);
        if(null!= mTargetImage){
            if(null!=bitmap){
                if(bitmap instanceof Bitmap){
                    mTargetImage.setImageBitmap((Bitmap) bitmap);
                }else if(bitmap instanceof Drawable){
                    mTargetImage.setImageDrawable((Drawable) bitmap);
                }else{
                    if(errorPlaceholder>0) mTargetImage.setImageResource(errorPlaceholder);
                }
                //bitmap.recycle();
            }else{
                if(errorPlaceholder>0) mTargetImage.setImageResource(errorPlaceholder);
            }
        }
    }
}