package cn.acewill.mobile.pos.printer;

import android.graphics.Bitmap;

/**
 * Created by Acewill on 2016/8/22.
 */
public class BitmapRow implements Row{

    private Bitmap bitmap;

    public BitmapRow(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    @Override
    public int getScaleWidth() {
        return 1;
    }
}
