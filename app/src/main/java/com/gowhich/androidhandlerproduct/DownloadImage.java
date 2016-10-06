package com.gowhich.androidhandlerproduct;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.net.URL;

/**
 * Created by durban126 on 16/10/6.
 */

public class DownloadImage {
    private String imagePath;
    public DownloadImage(String imagePath){
        this.imagePath = imagePath;
    }

    public void loadImage(final ImageCallback callback){
        final Handler handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                System.out.println("handle = " + msg.toString());
                callback.getDrawable((Drawable) msg.obj);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Drawable drawable = Drawable.createFromStream(new URL(imagePath).openStream(), "");
                    Message message = Message.obtain();
                    message.obj = drawable;
                    handler.sendMessage(message);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }

    public interface ImageCallback{
        public void getDrawable(Drawable drawable);
    }
}
