package org.cmaaio.common;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.widget.ImageView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.cmaaio.ssl.EasySSLSocketFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.RejectedExecutionException;


/**
 * ImageView extended class allowing easy downloading
 * of remote images
 */
public class RemoteImageView extends ImageView {

    private ImageCache imageCache = new ImageCache();

    private String mUrl;

    public RemoteImageView(Context context) {
        super(context);
    }

    public RemoteImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setDefaultImage(int resId) {
        this.setImageResource(resId);
    }

    public void setImageUrl(String url) {
        if (url == null)
            return;

        mUrl = url;

        if (isCached(url))
            return;

        startDownload(url);
    }

    public boolean isCached(String url) {
        Bitmap cacheBmp = imageCache.getImageFromCache(url);
        if (cacheBmp != null) {
            this.setImageBitmap(cacheBmp);
            return true;
        }

        return false;
    }

    private void startDownload(String url) {
        try {
            new DownloadTask().execute(url);
        } catch (RejectedExecutionException e) {
            //����RejectedExecutionExceptionͬʱ���ص�ͼƬ�����³������
        }
    }


    class DownloadTask extends AsyncTask<String, Void, String> {
        private String imageUrl;

        @Override
        protected String doInBackground(String... params) {
            imageUrl = params[0];
            InputStream is = null;

            try {
                HttpClient httpclient = null;
                if (imageUrl.contains("https://")) {
                    HttpParams par = new BasicHttpParams();
                    SchemeRegistry schemeRegistry = new SchemeRegistry();
                    schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
                    ClientConnectionManager connManager = new ThreadSafeClientConnManager(par, schemeRegistry);
                    httpclient = new DefaultHttpClient(connManager, par);
                } else
                    httpclient = new DefaultHttpClient();
                HttpGet httpget = new HttpGet(imageUrl);
                HttpResponse response = httpclient.execute(httpget);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                if (is != null)
                    imageCache.saveImageToCache(imageUrl, is);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            }

            return imageUrl;
        }

        @Override
        protected void onPostExecute(String result) {
            Bitmap bmp = imageCache.getImageFromCache(result);
            if (bmp != null) {
                if (result.equals(RemoteImageView.this.mUrl))//���table���û����ճɵ�����ʾ����
                    RemoteImageView.this.setImageBitmap(bmp);
            } else {
                //����ʧ��
            }
            super.onPostExecute(result);
        }

    }

}
