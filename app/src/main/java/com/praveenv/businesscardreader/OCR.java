package com.praveenv.businesscardreader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.util.Log;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

class OCR {

    private Context context;

    OCR(Context context) {
        this.context = context;
        setUpTesseractData();
    }

    void setUpTesseractData(){
        try{
            File dir = context.getExternalFilesDir("/tessdata");
            if(!dir.exists()){
                if (!dir.mkdir()) {
                    Toast.makeText(context.getApplicationContext(), "The folder " + dir.getPath() + "was not created", Toast.LENGTH_SHORT).show();
                }
            }
            String fileList[] = context.getAssets().list("");
            for(String fileName : fileList){
                String pathToDataFile = dir + "/" + fileName;
                if(!(new File(pathToDataFile)).exists()){
                    InputStream in = context.getAssets().open(fileName);
                    OutputStream out = new FileOutputStream(pathToDataFile);
                    byte [] buff = new byte[1024];
                    int len ;
                    while(( len = in.read(buff)) > 0){
                        out.write(buff,0,len);
                    }
                    in.close();
                    out.close();
                }
            }
        } catch (Exception e) {
            Log.e(MainActivity.class.getSimpleName(), e.getMessage());
        }
    }

    String getText(Bitmap bitmap){
        TessBaseAPI tessBaseAPI = new TessBaseAPI();
        tessBaseAPI.init(context.getExternalFilesDir("/").getPath() + "/", "eng");
        tessBaseAPI.setImage(toGrayscale(bitmap));
        String retStr = tessBaseAPI.getUTF8Text();
        tessBaseAPI.end();
        return retStr;
    }

    private Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

}
