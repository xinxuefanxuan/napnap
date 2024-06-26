package com.work37.napnap.service;

import static android.app.Activity.RESULT_OK;

import static androidx.core.app.ActivityCompat.startActivityForResult;

import android.app.Activity;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.view.View;

import com.work37.napnap.GalleryActivity;
import com.work37.napnap.R;

import android.app.Activity;
import android.content.Intent;
import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.net.Uri;
import android.widget.Toast;

public class CustomKeyboardService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {
    public static final int GALLERY_REQUEST_CODE = 100; // 请求码常量

    private KeyboardView keyboardView;
    private Keyboard keyboard;

    public static CustomKeyboardService instance; // 当前服务实例

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public View onCreateInputView() {
        keyboardView = (KeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
        keyboard = new Keyboard(this, R.xml.keyboard);
        keyboardView.setKeyboard(keyboard);
        keyboardView.setOnKeyboardActionListener(this);
        return keyboardView;
    }

    @Override
    public void onKey(int primaryCode, int[] keyCodes) {
        if (primaryCode == -1) {
            // Handle custom key action here (open gallery)
            openGallery();
        } else if (primaryCode == -2) {
            // Handle shift key (optional)
        } else if (primaryCode == -4) {
            // Handle enter key
            getCurrentInputConnection().commitText("\n", 1);
        } else if (primaryCode == -5) {
            // Handle delete key
            getCurrentInputConnection().deleteSurroundingText(1, 0);
        } else {
            getCurrentInputConnection().commitText(Character.toString((char) primaryCode), 1);
        }
    }

    @Override
    public void onPress(int primaryCode) { }

    @Override
    public void onRelease(int primaryCode) { }

    @Override
    public void onText(CharSequence text) { }

    @Override
    public void swipeLeft() { }

    @Override
    public void swipeRight() { }

    @Override
    public void swipeDown() { }

    @Override
    public void swipeUp() { }

    private void openGallery() {
        Intent intent = new Intent(this, GalleryActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    public void handleGalleryResult(Uri imageUri) {
        // 实现上传图片到服务器的逻辑，并获得图片的 URL
        // 上传成功后，将 URL 添加到评论输入框
        String imageUrl = "http://yourserver.com/path/to/image.jpg"; // 假设这是上传后的 URL
        getCurrentInputConnection().commitText(imageUrl, 1);
    }
}


