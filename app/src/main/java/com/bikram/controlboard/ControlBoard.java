package com.bikram.controlboard;

import android.inputmethodservice.InputMethodService;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;
import android.os.SystemClock;

public class ControlBoard extends InputMethodService {
 
     private View keyboardView;
     
     int ctrlMask = KeyEvent.META_CTRL_MASK;
     int altMask = KeyEvent.META_ALT_MASK;
     int shiftMask = KeyEvent.META_SHIFT_MASK;
     
     private int metaState = 0;
     
     boolean ctrlPressed = false;
     boolean altPressed = false;
     boolean shiftPressed = false;
 
     @Override
     public View onCreateInputView() {
         keyboardView = getLayoutInflater().inflate(R.layout.keyboard_view, null);
         return keyboardView;
     }
 

    public void onKeyClick(View pressedKey) {
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection == null) return;
        
        String keyType = (String) pressedKey.getTag();
        long now = System.currentTimeMillis();
        
        switch(keyType) {
            case "CTRL":
                ctrlPressed = !ctrlPressed;
                break;
            case "ALT":
                altPressed = !altPressed;
                break;
            case "SHIFT":
                shiftPressed = !shiftPressed;
                break;
                
            default:
                updateMetaState();
                try {
                    inputConnection.sendKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_DOWN, KeyEvent.class.getField("KEYCODE_" + keyType).getInt(null), 0, metaState));
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    
                }
                metaState = 0;
            }
    }
    
    void updateMetaState() {
        
        String metaSequence = boolToIntString(ctrlPressed) + boolToIntString(altPressed) + boolToIntString(shiftPressed);

        switch (metaSequence) {
            case "000":
                metaState = 0;
                break;
            case "100":
                metaState = ctrlMask;
                break;
            case "010":
                metaState = altMask;
                break;
            case "001":
                metaState = shiftMask;
                break;
            case "110":
                metaState = ctrlMask | altMask;
                break;
            case "101":
                metaState = ctrlMask | shiftMask;
                break;
            case "011":
                metaState = altMask | shiftMask;
                break;
            case "111":
                metaState = ctrlMask | altMask | shiftMask;
                break;
        }
        
    }
    
    String boolToIntString(boolean b) {
        return b ? "1" : "0";
    }

 }

