package com.work37.napnap.ui.personality;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.work37.napnap.databinding.ActivityEditProfileBinding;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.PublicActivity;
import com.work37.napnap.global.PublicApplication;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.ui.community.AddPostActivity;
import com.work37.napnap.ui.userlogin_register.User;

import org.json.JSONObject;

import java.io.File;
import java.util.Arrays;

import javax.net.ssl.X509TrustManager;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EditProfileActivity extends PublicActivity {

    private ActivityEditProfileBinding binding;
    private Uri imageUri;
    private ImageView userAvatar;
    private ImageView back;
    private EditText editUserName;
    private EditText editUserProfile;
    private Button editButton;
    private boolean isEditing = false;
    private User currentUser;
    private final ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Intent data = result.getData();
                            if (data != null) {
                                imageUri = data.getData();
                                new UploadImageTask().execute(imageUri);
                            }
                        }
                    });

    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri selectedImageUri;
    private String uploadedImageUrl;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());
        editUserName = binding.editUserName;
        editUserProfile = binding.editUserBio;
        editButton = binding.editButton;
        userAvatar = binding.userAvatar;
        back = binding.backButton;

        // 获取当前用户信息
        currentUser = PublicApplication.getCurrentUser();
        if (currentUser != null) {
            // 设置用户头像、名称和简介
            Glide.with(this).load(currentUser.getUserAvatar()).into(userAvatar);
            editUserName.setText(currentUser.getUserName());
            editUserProfile.setText(currentUser.getProfile().isEmpty() ? "这个人很懒，什么都没有留下" : currentUser.getProfile());
            disableEditing();
        }


//        pickImageLauncher = registerForActivityResult(
//                new ActivityResultContracts.StartActivityForResult(),
//                result -> {
//                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
//                        Uri selectedImageUri = result.getData().getData();
//                        if (selectedImageUri != null) {
//                            userAvatar.setImageURI(selectedImageUri);
//                            this.selectedImageUri = selectedImageUri;
//                            uploadAvatar(selectedImageUri);
//                        }
//                    }
//                });

        userAvatar.setOnClickListener(v -> {
            if (isEditing) {
                openImagePicker();
            }
        });

        back.setOnClickListener(v -> finish());

        editButton.setOnClickListener(v -> {
            if (isEditing) {
                saveProfile();
            } else {
                enterEditMode();
            }
        });
    }

    private class UploadImageTask extends AsyncTask<Uri, Void, String> {
        @Override
        protected String doInBackground(Uri... params) {
            try {
                return uploadImageToServer(params[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        @Override
        protected void onPostExecute(String result) {
            if (result != null) {
//                picturePaths.add(result);
//                addImageToLayout(result);
                uploadedImageUrl = result;
                Toast.makeText(getApplicationContext(), "头像上传成功", Toast.LENGTH_SHORT).show();
                Glide.with(getApplicationContext()).load(result).into(userAvatar);
            } else {
                Toast.makeText(getApplicationContext(), "头像上传失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, 1);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
                openImagePicker();
            } else {
                Toast.makeText(this, "Permission denied to read media images", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //打开相册选择新图片
    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickImageLauncher.launch(intent);
    }

    private String uploadImageToServer(Uri imageUri) throws Exception {
        String url = null;

        // 获取 URI 对应的真实路径
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
        }

        return url;
    }

    private String getRealPathFromURI(Uri uri) {
        String result;
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) { // 来源是 Dropbox 或其他类似的本地文件路径
            result = uri.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }



    private void disableEditing() {
        editUserName.setActivated(false);
        editUserProfile.setActivated(false);
        editUserName.setEnabled(false);
        editUserProfile.setEnabled(false);
        userAvatar.setClickable(false);
        editButton.setText("编辑");
        isEditing = false;
    }

    //进入编辑模式
    private void enterEditMode() {
        editUserName.setActivated(true);
        editUserProfile.setActivated(true);
        editUserName.setEnabled(true);
        editUserProfile.setEnabled(true);
        userAvatar.setClickable(true);
        editButton.setText("保存");
        isEditing = true;
    }


    //获取图图片路径
    private String getPathFromUri(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            String path = cursor.getString(columnIndex);
            cursor.close();
            return path;
        }
        return null;
    }

    private void loadImage(Uri selectedImageUri) {
        // 使用 Glide 或其他图片加载库加载图像到 ImageView
        ImageView imageView = userAvatar;
        Glide.with(this)
                .load(selectedImageUri)
                .into(imageView);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            userAvatar.setImageURI(selectedImageUri);
        }
    }

    private void saveProfile() {
        String newUserName = editUserName.getText().toString().trim();
        String newUserProfile = editUserProfile.getText().toString().trim();

//        // 更新本地用户信息
//        currentUser.setUserName(newUserName);
//        currentUser.setProfile(newUserProfile);

        // 发送更新请求到后端
        sendUpdateRequest();

        // 更新UI状态
        editUserName.setActivated(false);
        editUserProfile.setActivated(false);
        editUserName.setEnabled(false);
        editUserProfile.setEnabled(false);
        userAvatar.setClickable(false);
        editButton.setText("编辑");
        isEditing = false;
    }

    private void sendUpdateRequest() {
        JSONObject json = new JSONObject();
        try {
            json.put("userName", editUserName.getText());
            json.put("userProfile", editUserProfile.getText());
            // 如果有头像更新
            if (uploadedImageUrl != null) {
                json.put("userAvatar", uploadedImageUrl);
            } else {
                json.put("userAvatar", currentUser.getUserAvatar());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        new Thread(() -> {
            OkHttpClient client = new OkHttpClient.Builder()
                    .cookieJar(new PersistentCookieJar(getApplicationContext())).build();
            RequestBody requestBody = RequestBody.create(json.toString(), MediaType.get("application/json; charset=utf-8"));
            Request request = new Request.Builder()
                    .url(UrlConstant.baseUrl + "/api/user/updateUserInfo")
                    .put(requestBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                int code = jsonObject.getInt("code");
                String message = jsonObject.getString("message");
                if (code == 0) {
                    // 更新成功
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "成功更新资料", Toast.LENGTH_SHORT).show();
                        User user = PublicApplication.getCurrentUser();
                        user.setProfile(editUserProfile.getText().toString());
                        user.setUserName(editUserName.getText().toString());
                        if(uploadedImageUrl!=null){
                            user.setUserAvatar(uploadedImageUrl);
                        }
                        PublicApplication.setCurrentUser(user);
                    });
                } else {
                    // 更新失败
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "请求失败", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }

    private void uploadAvatar(Uri imageUri) {
        new Thread(() -> {
            try {
                String imagePath = getPathFromUri(imageUri);
                File imageFile = new File(imagePath);
                OkHttpClient client = new OkHttpClient.Builder()
                        .cookieJar(new PersistentCookieJar(getApplicationContext())).build();
                RequestBody requestBody = new MultipartBody.Builder()
                        .setType(MultipartBody.FORM)
                        .addFormDataPart("file", imageFile.getName(),
                                RequestBody.create(imageFile, MediaType.get("image/*")))
                        .build();
                Request request = new Request.Builder()
                        .url(UrlConstant.baseUrl + "api/image/upload")
                        .post(requestBody)
                        .build();
                Response response = client.newCall(request).execute();
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                int code = jsonObject.getInt("code");
                if (code == 0) {
                    uploadedImageUrl = jsonObject.getString("data");  // 假设返回的数据中包含头像 URL
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "头像上传成功", Toast.LENGTH_SHORT).show();
                    });
                } else {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "头像上传失败", Toast.LENGTH_SHORT).show();
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    Toast.makeText(getApplicationContext(), "头像上传失败", Toast.LENGTH_SHORT).show();
                });
            }
        }).start();
    }
}
