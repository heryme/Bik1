package com.project.biker.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RadioButton;

/**
 * Created by Rahul Padaliya on 7/18/2017.
 */
@SuppressLint("AppCompatCustomView")
public class CustomRadioButton extends RadioButton {
    public CustomRadioButton(Context context) {
        super(context);
        init();
    }

    public CustomRadioButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CustomRadioButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init(){
        Typeface font_type=Typeface.createFromAsset(getContext().getAssets(), "open_sans_regular.ttf");
        setTypeface(font_type);
    }
}
