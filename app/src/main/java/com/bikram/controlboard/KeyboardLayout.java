package com.bikram.controlboard;

import android.content.Context;
import android.widget.LinearLayout;
import android.util.AttributeSet;
import android.view.View;
import android.view.MotionEvent;

public class KeyboardLayout extends LinearLayout {
    public KeyboardLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                return false;
                
            case MotionEvent.ACTION_MOVE:
                float x = event.getRawX(), y = event.getRawY();
                View v = ControlBoard.self.currentPressedKey;
                int[] loc = new int[2];
                v.getLocationOnScreen(loc);
                if ((x > loc[0]) && (y > loc[1]) && (x < loc[0] + v.getWidth()) && (y < (loc[1] + v.getHeight()))) 
                    return false;
                return true;
        }
        
        return false;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getRawX(), y = event.getRawY();
        int countL = getChildCount();
        for (int i = 0; i < countL; i++) {
            LinearLayout ll = (LinearLayout)getChildAt(i);
            int count = ll.getChildCount();
            
            for (int j = 0; j < count; j++) {
                View v = ll.getChildAt(j);
                int[] loc = new int[2];
                v.getLocationOnScreen(loc);
                if ((x > loc[0]) && (y > loc[1]) && (x < loc[0] + v.getWidth()) && (y < (loc[1] + v.getHeight()))) {
                    v.dispatchTouchEvent(event);
                    return true;
                }
            }
        }
        return true;
    }
}