package com.bikram.controlboard;

import android.content.Context;
import android.widget.Button;
import android.util.AttributeSet;

public class KeyButton extends Button {
    public KeyButton(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    @Override
    protected void onAttachedToWindow() {
        setOnTouchListener(ControlBoard.self);
        super.onAttachedToWindow();
    }
}