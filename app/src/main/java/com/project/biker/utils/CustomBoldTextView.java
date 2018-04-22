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
public class CustomBoldTextView extends TextView {
    private static Typeface mTypeface;

    public CustomBoldTextView(final Context context) {
        this(context, null);
    }

    public CustomBoldTextView(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomBoldTextView(final Context context, final AttributeSet attrs, final int defStyle) {
        super(context, attrs, defStyle);

        if (mTypeface == null) {
            mTypeface = Typeface.createFromAsset(context.getAssets(), "opensansbold.ttf");
        }
        setTypeface(mTypeface);
    }

}
