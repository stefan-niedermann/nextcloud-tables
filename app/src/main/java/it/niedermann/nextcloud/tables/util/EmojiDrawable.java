package it.niedermann.nextcloud.tables.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.emoji2.widget.EmojiTextView;

public class EmojiDrawable extends Drawable {
    private final String emoji;
    private final Paint paint;

    public EmojiDrawable(@NonNull Context context, @NonNull String emoji) {
        this.emoji = emoji;
        this.paint = new Paint(new EmojiTextView(context).getPaint());
    }


    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawText(emoji, 8, (float) getBounds().height() / 1.4f, paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return switch (paint.getAlpha()) {
            case 0 -> PixelFormat.TRANSPARENT;
            case 255 -> PixelFormat.OPAQUE;
            default -> PixelFormat.TRANSLUCENT;
        };
    }

}