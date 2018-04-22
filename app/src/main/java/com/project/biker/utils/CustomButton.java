package com.project.biker.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * Created by Rahul Padaliya on 7/18/2017.
 */
@SuppressLint("AppCompatCustomView")
public class CustomButton extends Button {
    public CustomButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        // TODO Auto-generated constructor stub
    }
    public CustomButton(Context context) {
        super(context);
        init();
        // TODO Auto-generated constructor stub
    }
    public CustomButton(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
        // TODO Auto-generated constructor stub
    }
    private void init(){
        Typeface font_type=Typeface.createFromAsset(getContext().getAssets(), "open_sans_regular.ttf");
        setTypeface(font_type);
    }
}
