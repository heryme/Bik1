package com.project.biker.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by Rahul Padaliya on 7/18/2017.
 */
@SuppressLint("AppCompatCustomView")
public class CustomTextView extends TextView {
    private static Typeface mTypeface;

    public CustomTextView(final Context context) {
        this(context, null);
    }

    public CustomTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.getAssets(), "open_sans_regular.ttf");
        }
        setTypeface(mTypeface);
    }

}
