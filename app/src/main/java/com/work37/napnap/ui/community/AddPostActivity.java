package com.work37.napnap.ui.community;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.work37.napnap.R;
import com.work37.napnap.databinding.ActivityAddPostBinding;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.PublicActivity;
import com.work37.napnap.global.UrlConstant;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AddPostActivity extends PublicActivity {
    private ActivityAddPostBinding binding;
    private Uri imageUri;
    //图片地址列表
    private ArrayList<String> picturePaths = new ArrayList<>();
    //标签列表
    private ArrayList<String> selectedTags = new ArrayList<>();
    //标题内容，正文内容
    private EditText editTitle, editContent;
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                imageUri = data.getData();
                                try {
                                    String imageUrl = uploadImageToServer(imageUri);
                                    if (imageUrl != null) {
                                        picturePaths.add(imageUrl);
                                    }
                                } catch (Exception e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    });


    @Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    binding = ActivityAddPostBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    ImageView addImage = binding.ivAddImage;

    editTitle = binding.editTitle;

    editContent = binding.editContent;

    Button butSubmit = binding.btnSubmit;

    checkPermissions();
    // 添加图片按钮点击事件
    addImage.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            pickImageLauncher.launch(intent);
        }
    });

    // 标签按钮点击事件
    setupTagButton(R.id.btn_technology, "技术");
    setupTagButton(R.id.btn_culture, "文化");
    setupTagButton(R.id.btn_entertainment, "娱乐");
    setupTagButton(R.id.btn_food, "美食");
    setupTagButton(R.id.btn_education, "教育");
    setupTagButton(R.id.btn_game, "游戏");
    setupTagButton(R.id.btn_health, "健康");
    setupTagButton(R.id.btn_news, "新闻");
    setupTagButton(R.id.btn_sports, "运动");
    setupTagButton(R.id.btn_travel, "旅游");

    // 提交按钮点击事件
    butSubmit.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            submitPost();
        }
    });
}

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }


//标签按钮
private void setupTagButton(int buttonId, final String tag) {
    Button button = findViewById(buttonId);
    button.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (selectedTags.contains(tag)) {
                selectedTags.remove(tag);
                v.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.default_button_color));
            } else {
                selectedTags.add(tag);
                v.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.selected_button_color));
            }
        }
    });
}

    private String uploadImageToServer(Uri imageUri) throws Exception {
        String url = null;

        // Get the real path from the URI
        String filePath = getRealPathFromURI(imageUri);
        File file = new File(filePath);

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new PersistentCookieJar(getApplicationContext()))
                .build();

        RequestBody requestBody = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", file.getName(),
                        RequestBody.create(file, MediaType.get("image/*")))
                .build();

        Request request = new Request.Builder()
                .url(UrlConstant.baseUrl + "api/image/upload")
                .post(requestBody)
                .build();

        Response response = okHttpClient.newCall(request).execute();
        String responseBody = response.body().string();
        JSONObject jsonObject = new JSONObject(responseBody);
        int code = jsonObject.getInt("code");
        String message = jsonObject.getString("message");
        if (code == 0) {
            url = jsonObject.getString("data");
            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), "头像上传成功", Toast.LENGTH_SHORT).show();
            });
        } else {
            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), "头像上传失败", Toast.LENGTH_SHORT).show();
            });
        }

        return url;
    }

    private String getRealPathFromURI(Uri uri) {
        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }


private void submitPost() {
    try {
        String title = editTitle.getText().toString();
        String content = editContent.getText().toString();

        JSONObject postJson = new JSONObject();
        postJson.put("title", title);
        postJson.put("content", content);
        postJson.put("pictures", picturePaths);
        postJson.put("tag", selectedTags);

        RequestBody requestBody = RequestBody.create(
                postJson.toString(),
                MediaType.get("application/json; charset=utf-8")
        );
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .cookieJar(new PersistentCookieJar(getApplicationContext()))
                .build();
        Request request = new Request.Builder()
                .url(UrlConstant.baseUrl+"api/post/addPost")
                .post(requestBody)
                .build();
        Response response = okHttpClient.newCall(request).execute();
        String responseBody = response.body().string();
        Log.d("aaa",responseBody);
        JSONObject jsonObject = new JSONObject(responseBody);
        int code = jsonObject.getInt("code");
        String message = jsonObject.getString("message");
        if(code==0){
            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), "上传动态成功", Toast.LENGTH_SHORT).show();
            });
        }else{
            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), "上传动态失败", Toast.LENGTH_SHORT).show();
            });
        }
    } catch (Exception e) {
        e.printStackTrace();
        Toast.makeText(this, "提交失败", Toast.LENGTH_SHORT).show();
    }
}
}
