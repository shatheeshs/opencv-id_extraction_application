package io.github.arimac.idextractionlibrary.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class CanvasDraw extends View {
    public Paint paint;
    private Path path;
    private Bitmap imag;

    private CanvasDraw(Context context) {
        super(context);
        init();
    }

    private CanvasDraw(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private CanvasDraw(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (path != null) {
            canvas.drawPath(path, paint);

        }
    }

    public void setPath(Path path) {
        this.path = path;
    }

}
