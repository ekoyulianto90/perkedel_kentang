package com.skripsi.ekoyulianto.biologipedia.CustomFont;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.EditText;

import static java.security.AccessController.getContext;

/**
 * Created by ekoyulianto on 12/30/2016.
 */

public class CustomEditText extends EditText{

    public CustomEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public CustomEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomEditText(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "fonts/SEGOEUISL.TTF");
            setTypeface(tf);
        }
    }
}
