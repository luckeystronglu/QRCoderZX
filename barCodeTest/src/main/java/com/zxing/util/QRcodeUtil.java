package com.zxing.util;

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.util.DisplayMetrics;

import com.ericssonlabs.R;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

/**
 * @author Ken
 *
 */
public class QRcodeUtil {

	/**
	 * @param context
	 * @param content
	 * @return
	 */
	public static Bitmap btnGenRawCode(Context context, String content) {
        int qrSize = getQrSize(context);

        HashMap<EncodeHintType, Object> hints = new HashMap<EncodeHintType, Object>();

        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);

        try {

            QRCodeWriter writer = new QRCodeWriter();

            BitMatrix matrix = writer.encode(content, BarcodeFormat.QR_CODE, qrSize, qrSize, hints);

            int width = matrix.getWidth();
            int height = matrix.getHeight();
            int[] pixels = new int[width * height];

            // ---------------------------------

            Bitmap bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);

            int ww = bmp.getWidth();
            int hh = bmp.getHeight();

            int[] logoPixels = new int[ww * hh];

            bmp.getPixels(logoPixels, 0, ww, 0, 0, ww, hh);

            bmp.recycle();

            // ---------------------------------


            for (int y = 0; y < height; y++) {  //
                int offset = y * width;
                for (int x = 0; x < width; x++) {
                    pixels[offset + x] = matrix.get(x, y) ? Color.BLACK : Color.WHITE;
                }
            }

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
            pixels = null;
            matrix = null;
            writer = null;


            return bitmap;
        } catch (WriterException e) {
            e.printStackTrace();
        }

        return null;
    }

    private static int getQrSize(Context context) {
        int ret = 128;
        if (context != null) {
            DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
            int w = displayMetrics.widthPixels;
            int h = displayMetrics.heightPixels;
            ret = ((w >= h) ? h : w) >> 1;
        }
        return ret;
    }

    public static void saoCode(){
    	
    }
}
