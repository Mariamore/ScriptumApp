package com.example.scriptumapp;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

import com.squareup.picasso.Transformation;

public class ImageCircle implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());
        int x = (source.getWidth() - size) / 2;
        int y = (source.getHeight() - size) / 2;

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        paint.setAntiAlias(true);

        // Dibujar el círculo base
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(0xFF787054); // Este color no importa realmente

        float radius = size / 2f;
        float cx = size / 2f;
        float cy = size / 2f;

        // Ajustar el rectángulo para el borde
        RectF rectF = new RectF(0, 0, size, size);
        rectF.inset(5, 5);  // Ajuste para que el borde completo sea visible

        canvas.drawOval(rectF, paint);

        // Dibujar la imagen recortada dentro del círculo
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(squaredBitmap, new Rect(0, 0, size, size), rectF, paint);
        squaredBitmap.recycle();

        // Dibujar el borde del círculo
        paint.setXfermode(null);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(0xFF787054); // Color del borde
        paint.setStrokeWidth(10); // Grosor del borde

        rectF = new RectF(5, 5, size - 5, size - 5); // Ajuste del borde
        canvas.drawOval(rectF, paint);

        return output;
    }

    @Override
    public String key() {
        return "CircularImageWithBorder";
    }
}
