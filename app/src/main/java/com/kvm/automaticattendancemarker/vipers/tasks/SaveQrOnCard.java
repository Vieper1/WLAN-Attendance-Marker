package com.kvm.automaticattendancemarker.vipers.tasks;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Environment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by VIPER GTS on 15-Feb-17.
 */

public class SaveQrOnCard extends AsyncTask<String, Void, Void>
{
    private String stringQR;
    private File file;

    @Override
    protected Void doInBackground(String... strings)
    {
        stringQR = strings[0];
        ////////////////////////////// Encrypt stringQR

        try
        {
            final int xInt = 600;
            final int yInt = 600;

            Writer writer = new QRCodeWriter();
//                String finalString = Uri.encode(stringQR, "utf-8");
            String finalString = stringQR;

            BitMatrix bm = writer.encode(finalString, BarcodeFormat.QR_CODE, xInt, yInt);
            Bitmap tempBitmap = Bitmap.createBitmap(xInt, yInt, Bitmap.Config.ARGB_8888);

            for(int i=0;i<xInt;i++)
            {
                for(int j=0;j<yInt;j++)
                {
                    tempBitmap.setPixel(i, j, bm.get(i, j) ? Color.BLACK : Color.WHITE);
                }
            }

            ////////////////////////////// Set up file

            file = getFileRef();
            if(file == null)
            {
                System.out.println("////////// Error creating file, check storage permissions");
            }

            FileOutputStream out = new FileOutputStream(file);
            tempBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            ////////////////////////////// Set up file
        }
        catch(WriterException |IOException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid)
    {
        System.out.println("////////// Write bitmap to storage Async Task complete!");
    }


    private File getFileRef()
    {
        String filePath =
                Environment.getExternalStorageDirectory()
                        +"/Android/data/"
                        +"com.kvm.automaticattendancemarker"
                        +"/Files";

        File mediaStorageDir = new File(filePath);

        if(!mediaStorageDir.exists())
        {
            if(!mediaStorageDir.mkdirs())
            {
                return null;
            }
        }


        return new File(mediaStorageDir.getPath() + File.separator + "customQR.png");
    }
}
