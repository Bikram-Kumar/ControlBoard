package com.bikram.controlboard;

import java.util.HashMap;
import android.inputmethodservice.InputMethodService;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.view.inputmethod.InputConnection;

public class ControlBoard extends InputMethodService {
 
     View mainKeyboardView;
     View symbolsKeyboardView;
     View currentInputView;
     
     int ctrlMask = KeyEvent.META_CTRL_MASK;
     int altMask = KeyEvent.META_ALT_MASK;
     int shiftMask = KeyEvent.META_SHIFT_MASK;
     
     private int metaState = 0;
     
     boolean ctrlPressed = false;
     boolean altPressed = false;
     boolean shiftPressed = false;
     
     boolean ctrlLocked = false;
     boolean altLocked = false;
     boolean shiftLocked = false;
     
     long doubleTapMaxDelay = 200;
     
     // This HashMap holds info about the last meta key pressed
     // Its first entry should be the Key name <String,String>, and second the time <String,long>
     HashMap<String,Object> lastMetaKeyPressedInfo = new HashMap<String,Object>();
     // lastMetaKeyPressedInfo.put("Time", System.currentTimeMillis());
     // lastMetaKeyPressedInfo.put("Key", "ALT/CTRL/whatever");
 
     @Override
     public View onCreateInputView() {
         
         mainKeyboardView = getLayoutInflater().inflate(R.layout.keyboard_view, null);
         symbolsKeyboardView = getLayoutInflater().inflate(R.layout.symbols_keyboard_view, null);
         
         currentInputView = mainKeyboardView;
         return mainKeyboardView;
     }
 

    public void onKeyClick(View pressedKeyView) {
        Button pressedKey = (Button) pressedKeyView;
        InputConnection inputConnection = getCurrentInputConnection();
        if (inputConnection == null) return;
        
        long now = System.currentTimeMillis();
        String keyType = (String) pressedKey.getTag();
        
        if (keyType.contains("SHIFT_")) {
            shiftPressed = !shiftPressed;
            keyType = keyType.substring(6, keyType.length()); // We know the index of "SHIFT_" is 0, so we will extract the substring from index 6
        }
        
        switch(keyType) {
            case "LOAD_MAIN_KEYBOARD":
                
                setInputView(mainKeyboardView);
                currentInputView = mainKeyboardView;
                
                mainKeyboardView.findViewWithTag("CTRL").setActivated(ctrlPressed);
                mainKeyboardView.findViewWithTag("ALT").setActivated(altPressed);
                mainKeyboardView.findViewWithTag("SHIFT").setActivated(shiftPressed);
                
                break;
            case "LOAD_SYMBOLS_KEYBOARD":
                setInputView(symbolsKeyboardView);
                currentInputView = symbolsKeyboardView;
                
                symbolsKeyboardView.findViewWithTag("CTRL").setActivated(ctrlPressed);
                symbolsKeyboardView.findViewWithTag("ALT").setActivated(altPressed);
                symbolsKeyboardView.findViewWithTag("SHIFT").setActivated(shiftPressed);
                break;
            case "CTRL":
                
                if (ctrlLocked) {
                    ctrlLocked = false;
                    ctrlPressed = false;
                    pressedKey.setActivated(false);
                } else {
                    ctrlPressed = !ctrlPressed;
                    pressedKey.setActivated(ctrlPressed);
                }
                
                if (lastMetaKeyPressedInfo.get("Key") == keyType && (now - (long) lastMetaKeyPressedInfo.get("Time") < doubleTapMaxDelay)) {
                    ctrlLocked = true;
                    ctrlPressed = true;
                    pressedKey.setActivated(true);
                } 
                
                lastMetaKeyPressedInfo.put("Key", keyType);
                lastMetaKeyPressedInfo.put("Time", now);
                
                break;
            case "ALT":
                
                if (altLocked) {
                    altLocked = false;
                    altPressed = false;
                    pressedKey.setActivated(false);
                } else {
                    altPressed = !altPressed;
                    pressedKey.setActivated(altPressed);
                }
                
                if (lastMetaKeyPressedInfo.get("Key") == keyType && (now - (long) lastMetaKeyPressedInfo.get("Time") < doubleTapMaxDelay)) {
                    altLocked = true;
                    altPressed = true;
                    pressedKey.setActivated(true);
                } 
                
                lastMetaKeyPressedInfo.put("Key", keyType);
                lastMetaKeyPressedInfo.put("Time", now);
                break;
            case "SHIFT":
                
                if (shiftLocked) {
                    shiftLocked = false;
                    shiftPressed = false;
                    pressedKey.setActivated(false);
                } else {
                    shiftPressed = !shiftPressed;
                    pressedKey.setActivated(shiftPressed);
                }
                
                if (lastMetaKeyPressedInfo.get("Key") == keyType && (now - (long) lastMetaKeyPressedInfo.get("Time") < doubleTapMaxDelay)) {
                    shiftLocked = true;
                    shiftPressed = true;
                    pressedKey.setActivated(true);
                } 
                
                lastMetaKeyPressedInfo.put("Key", keyType);
                lastMetaKeyPressedInfo.put("Time", now);
                break;
                
            default:
            
                updateMetaState();
                try {
                    inputConnection.sendKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_DOWN, KeyEvent.class.getField("KEYCODE_" + keyType).getInt(null), 0, metaState));
                } catch (IllegalAccessException | NoSuchFieldException e) {
                    
                }
                
                if (!ctrlLocked && ctrlPressed) {
                    ctrlPressed = false;
                    currentInputView.findViewWithTag("CTRL").setActivated(false);
                }
                if (!altLocked && altPressed) {
                    altPressed = false;
                    currentInputView.findViewWithTag("ALT").setActivated(false);
                }
                if (!shiftLocked && shiftPressed) {
                    shiftPressed = false;
                    currentInputView.findViewWithTag("SHIFT").setActivated(false);
                }
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

