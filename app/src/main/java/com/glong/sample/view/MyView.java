package com.glong.sample.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.glong.sample.R;

/**
 * Created by Garrett on 2018/12/7.
 * contact me krouky@outlook.com
 */
public class MyView extends View {
    private Paint paint = new Paint();
    BitmapShader shader;
    Matrix matrix;

    public MyView(Context context) {
        this(context, null);
    }

    public MyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Bitmap paperRes = BitmapFactory.decodeResource(getResources(), R.drawable.paper);
        shader = new BitmapShader(paperRes, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        matrix = new Matrix();

        shader.setLocalMatrix(matrix);
        paint.setShader(shader);
        paint.setAlpha(120);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SCREEN));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawPaint(paint);
    }

    public void setPaperIntensity(float noiseIntensity) {
        paint.setAlpha((int) (255 * noiseIntensity));
        postInvalidate();
    }
}
