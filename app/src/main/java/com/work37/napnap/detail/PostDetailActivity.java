package com.work37.napnap.detail;

import android.annotation.SuppressLint;
import android.content.ClipDescription;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.os.BuildCompat;
import androidx.core.view.inputmethod.EditorInfoCompat;
import androidx.core.view.inputmethod.InputConnectionCompat;
import androidx.core.view.inputmethod.InputContentInfoCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.work37.napnap.Adaptor.CommentAdapter;
import com.work37.napnap.Adaptor.PostAdaptor;
import com.work37.napnap.Game.PostRequest;
import com.work37.napnap.Game.PostResponse;
import com.work37.napnap.R;
import com.work37.napnap.entity.Comment;
import com.work37.napnap.entity.CommentAndUser;
import com.work37.napnap.entity.CommentUnderPostVO;
import com.work37.napnap.entity.Post;
import com.work37.napnap.global.PersistentCookieJar;
import com.work37.napnap.global.PublicActivity;
import com.work37.napnap.global.UrlConstant;
import com.work37.napnap.ui.search.GameResponse;
import com.work37.napnap.ui.userlogin_register.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import cn.hutool.core.bean.BeanUtil;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import retrofit2.Callback;

public class PostDetailActivity extends PublicActivity {
    private ImageView userAvatar;
    private TextView username;
    private Button followButton;
    private ViewPager viewPager;
    private TextView postTitle;
    private TextView postContent;
    private TextView postTimestamp;
    private ImageButton likeButton;
    private TextView likeCount;
    private ImageButton collectButton;
    private TextView collectCount;
    private RecyclerView commentsRecyclerView;
    private CommentAdapter commentAdapter;

    private Map<Long, User> userCache = new HashMap<>();

    private List<CommentAndUser> list;

    private int currentPage = 1;
    private final int pageSize = 10;
    private boolean isLoading = false;

    private boolean isLastPage = false;

    //是否点赞
    private boolean isLike;
    //是否收藏
    private boolean isCollect;
    private ImageView myAvatar;
    private EditText commentInput;
    //是否关注发贴用户
    private boolean isConcern;

    @SuppressLint("AppCompatCustomView")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        list = new ArrayList<>();
        //获取post对象
        Post post = (Post) getIntent().getSerializableExtra("Post");

        Long userId = post.getUserId();
        Long postId = post.getId();
        String title = post.getTitle();
        String content = post.getContent();
        Date createTime = post.getCreateTime();
        int collectNum = post.getCollectNum();
        int likes = post.getLikes();
        List<String> tag = post.getTag();
        List<String> picture = post.getPictures();
        // 初始化组件
        userAvatar = findViewById(R.id.userAvatar);
        username = findViewById(R.id.username);
        followButton = findViewById(R.id.followButton);
        viewPager = findViewById(R.id.viewPager);
        postTitle = findViewById(R.id.postTitle);
        postContent = findViewById(R.id.postContent);
        postTimestamp = findViewById(R.id.postTimestamp);
        likeButton = findViewById(R.id.likeButton);
        likeCount = findViewById(R.id.likeCount);
        collectButton = findViewById(R.id.bookmarkButton);
        collectCount = findViewById(R.id.bookmarkCount);
        myAvatar = findViewById(R.id.myAvatar);
        commentsRecyclerView = findViewById(R.id.commentsRecyclerView);
        commentInput = findViewById(R.id.commentInput);

        //这个代码很关键，但是我也不知道他是干什么的
        commentInput = new EditText(this) {
            @Override
            public InputConnection onCreateInputConnection(EditorInfo editorInfo) {
                final InputConnection ic = super.onCreateInputConnection(editorInfo);
                EditorInfoCompat.setContentMimeTypes(editorInfo,
                        new String [] {"image/png"});

                final InputConnectionCompat.OnCommitContentListener callback =
                        new InputConnectionCompat.OnCommitContentListener() {
                            @Override
                            public boolean onCommitContent(InputContentInfoCompat inputContentInfo,
                                                           int flags, Bundle opts) {
                                // read and display inputContentInfo asynchronously
                                if (BuildCompat.isAtLeastNMR1() && (flags &
                                        InputConnectionCompat.INPUT_CONTENT_GRANT_READ_URI_PERMISSION) != 0) {
                                    try {
                                        inputContentInfo.requestPermission();
                                    }
                                    catch (Exception e) {
                                        return false; // return false if failed
                                    }
                                }

                                // read and display inputContentInfo asynchronously.
                                // call inputContentInfo.releasePermission() as needed.

                                return true;  // return true if succeeded
                            }
                        };
                return InputConnectionCompat.createWrapper(ic, editorInfo, callback);
            }
            public void onStartInputView(EditorInfo info, boolean restarting) {
                String[] mimeTypes = EditorInfoCompat.getContentMimeTypes(info);

                boolean gifSupported = false;
                for (String mimeType : mimeTypes) {
                    if (ClipDescription.compareMimeTypes(mimeType, "image/gif")) {
                        gifSupported = true;
                    }
                }

                if (gifSupported) {
                    // the target editor supports GIFs. enable corresponding content
                } else {
                    // the target editor does not support GIFs. disable corresponding content
                }
            }
        };

        // Initialize RecyclerView
        commentsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        commentAdapter = new CommentAdapter(list, getApplicationContext());
        commentsRecyclerView.setAdapter(commentAdapter);


        //将一些开始就可以初始化的初始化
        postTitle.setText(title);
        postContent.setText(content);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
        String formattedDate = sdf.format(createTime);
        postTimestamp.setText(formattedDate);

        likeCount.setText(String.valueOf(likes));

        collectCount.setText(String.valueOf(collectNum));

        //对点赞按钮和收藏按钮进行初始化，判断是否之前就已经点赞过或者收藏过
        //对关注按钮也是，首先要获取该用户之前是否已经关注过他
        CompletableFuture<Void> voidCompletableFuture1 = CompletableFuture.runAsync(()->loadLikeAsync(postId));
        try {
            voidCompletableFuture1.get();
            updateLikeButton();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

        CompletableFuture<Void> voidCompletableFuture2 = CompletableFuture.runAsync(()->loadCollectAsync(postId));
        try {
            voidCompletableFuture2.get();
            updatecollectButton();
        }catch (Exception e) {
            throw new RuntimeException(e);
        }

        //获取帖子下面的评论
        loadComments(postId);

        // 获取发这个帖子的用户的信息
        CompletableFuture<Void> completableFuture = CompletableFuture.runAsync(() -> loadUserAsync(userId));
        try {
            completableFuture.get();
            User user = userCache.get(userId);
            if (user != null) {
                if (user.getUserAvatar() != null) {
                    Glide.with(this).load(user.getUserAvatar())
                            .into(userAvatar);
                    Glide.with(this).load(user.getUserAvatar())
                            .into(myAvatar);
                }else{
                    userAvatar.setVisibility(View.GONE);
                }
                username.setText(user.getUserName());
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        loadUserById(userId);

        commentsRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == commentAdapter.getItemCount() - 1 && !isLoading && !isLastPage) {
                    // Load more comments when reaching the bottom
                    currentPage++;
                    loadComments(postId);
                }
            }
        });


        // Handle back button
        findViewById(R.id.backButton).setOnClickListener(v -> finish());

        // Handle follow button click
        followButton.setOnClickListener(v -> followUser());

        // Handle like button click
        likeButton.setOnClickListener(v -> likeOrUnlikePost(postId));

        // Handle bookmark button click
        collectButton.setOnClickListener(v -> CollectOrunCollectPost(postId));

        // Handle post comment button click
        findViewById(R.id.postCommentButton).setOnClickListener(v -> postComment(postId));
    }


    private void loadComments(Long postId) {
        isLoading = true;
        new Thread(() -> {
            try {
                PostRequest postRequest = new PostRequest(currentPage, pageSize, postId, "");
                Gson gson = new Gson();
                String json = gson.toJson(postRequest);
                RequestBody requestBody = RequestBody.create(
                        json,
                        MediaType.get("application/json; charset=utf-8")
                );
                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .cookieJar(new PersistentCookieJar(getApplicationContext()))
                        .build();
                Request request = new Request.Builder()
                        .url(UrlConstant.baseUrl + "api/comment/listAllCommentUnderPost")
                        .post(requestBody)
                        .build();
                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();
                Log.d("aaa", responseBody);
                JSONObject jsonObject = new JSONObject(responseBody);
                int code = jsonObject.getInt("code");
                String message = jsonObject.getString("message");
                PostResponse postResponse = gson.fromJson(responseBody, PostResponse.class);
                if (code == 0) {
                    runOnUiThread(() -> {
                        List<CommentUnderPostVO> records = postResponse.getCommentData().getRecords();
//                        List<CommentUnderPostVO> commentList = getCommentList(records);
                        List<CommentAndUser> commentList = getCommentList(records);
                        new Handler(Looper.getMainLooper()).post(() -> {
                            list.addAll(commentList);
                            commentAdapter.notifyDataSetChanged();
                            isLoading = false;
                            if (records.size() < pageSize) {
                                isLastPage = true;
                            } else {
                                isLoading = false;
                            }
                        });
                    });
                } else {
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(this, "获取评论失败", Toast.LENGTH_SHORT).show();
            }
        }).start();

    }

    /**
     * 遍历第一层评论，将第一层评论的子评论递归处理
     *
     * @param records
     * @return
     */
//    private List<CommentAndUser> getCommentList(List<CommentUnderPostVO> records) {
//        List<CommentAndUser> commentList = new ArrayList<>();
//        for (CommentUnderPostVO record : records) {
//            // 收集该评论下的所有评论
//            CommentAndUser commentAndUser = copyCommentUnderPost(record);
//            List<CommentUnderPostVO> convertList = new ArrayList<>();
//            convertCommentList(convertList, record.getReplies());
//            List<CommentAndUser> collectConvertList = convertList.stream().map(this::copyCommentUnderPost).collect(Collectors.toList());
//            commentAndUser.setCommentAndUserList(collectConvertList);
////            record.setReplies(convertList);
////            BeanUtil.copyProperties(record, commentAndUser);
//            // 请求用户数据
//            loadUser(commentAndUser);
//            if(userCache.containsKey(commentAndUser.getuId())&&userCache.get(commentAndUser.getuId())!=null){
//                postUserToVO(userCache.get(commentAndUser.getuId()),commentAndUser);
//            }
//            commentList.add(commentAndUser);
//        }
//        return commentList;
//    }
    private List<CommentAndUser> getCommentList(List<CommentUnderPostVO> records) {
        List<CommentAndUser> commentList = new ArrayList<>();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        for (CommentUnderPostVO record : records) {
            CommentAndUser commentAndUser = copyCommentUnderPost(record);
            futures.add(loadUserAsync(commentAndUser));

            List<CommentUnderPostVO> convertList = new ArrayList<>();
            convertCommentList(convertList, record.getReplies());
            List<CommentAndUser> collectConvertList = convertList.stream()
                    .map(this::copyCommentUnderPost)
                    .collect(Collectors.toList());

            for (CommentAndUser reply : collectConvertList) {
                futures.add(loadUserAsync(reply));
            }

            commentAndUser.setCommentAndUserList(collectConvertList);
            commentList.add(commentAndUser);
        }

        // 等待所有用户数据加载完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        return commentList;
    }

    private CompletableFuture<Void> loadUserAsync(CommentAndUser commentAndUser) {
        return CompletableFuture.runAsync(() -> loadUser(commentAndUser));
    }

    /**
     * 拷贝对象信息
     *
     * @return
     */
    private CommentAndUser copyCommentUnderPost(CommentUnderPostVO commentUnderPostVO) {
        List<CommentUnderPostVO> replies = commentUnderPostVO.getReplies();
        Long postId = commentUnderPostVO.getPostId();
        Integer commentType = commentUnderPostVO.getCommentType();
        String content = commentUnderPostVO.getContent();
        Long userId = commentUnderPostVO.getuId();
        Date createTime = commentUnderPostVO.getCreateTime();
        Long parentId = commentUnderPostVO.getParentId();
        List<String> picture = commentUnderPostVO.getPicture();

        CommentAndUser commentAndUser = new CommentAndUser();
        commentAndUser.setPostId(postId);
        commentAndUser.setCommentType(commentType);
        commentAndUser.setContent(content);
        commentAndUser.setuId(userId);
        commentAndUser.setCreateTime(createTime);
        commentAndUser.setParentId(parentId);
        commentAndUser.setPicture(picture);
        loadUser(commentAndUser);
        return commentAndUser;
    }

    private void postUserToVO(User user, CommentAndUser commentAndUser) {
        if (user != null) {
            commentAndUser.setUsername(user.getUserName());
            commentAndUser.setUserAvatar(user.getUserAvatar());
        }
    }

    /**
     * 递归处理第二层及以后的子评论，放在同一个 List 里面
     *
     * @param convertList
     * @param replies
     */
    private void convertCommentList(List<CommentUnderPostVO> convertList, List<CommentUnderPostVO> replies) {
        for (CommentUnderPostVO commentUnderPostVO : replies) {
            convertList.add(commentUnderPostVO);
            convertCommentList(convertList, commentUnderPostVO.getReplies());
        }
    }

    //更新点赞按钮
    private void updateLikeButton(){
        if(isLike){
            likeButton.setImageResource(R.drawable.like);
        }else{
            likeButton.setImageResource(R.drawable.unlike);
        }
    }

    //更新收藏按钮
    private void updatecollectButton(){
        if(isCollect){
            collectButton.setImageResource(R.drawable.collect);
        }else{
            collectButton.setImageResource(R.drawable.uncollect);
        }
    }


    /**
     * 初始化时加载用户是否点赞了该帖子
     * @param postId
     */
    private void loadLikeAsync(Long postId){

    }

    /**
     * 初始化时加载用户是否收藏了该帖子
     * @param postId
     */
    private void loadCollectAsync(Long postId) {
    }

    /**
     * 加载用户信息
     * @param userId
     */
    private void loadUserAsync(Long userId){
        try {
            // Create JSON object
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("userId", userId);

            RequestBody requestBody = RequestBody.create(
                    jsonObject1.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new PersistentCookieJar(getApplicationContext()))
                    .build();
            Request request = new Request.Builder()
                    .url(UrlConstant.baseUrl + "api/user/getUserById")
                    .post(requestBody)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);
            int code = jsonObject.getInt("code");
            if (code == 0) {
                JSONObject userData = jsonObject.getJSONObject("data");
                String userAvatar = userData.getString("userAvatar");
                String username = userData.getString("userName");
                User user = new User();
                user.setUserName(username);
                user.setUserAvatar(userAvatar);
                userCache.put(userId, user);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 将用户名称和头像封装到commentAndUser里面去
     *
     * @param commentAndUser
     */
//    private void loadUser(CommentAndUser commentAndUser) {
//        Long userId = commentAndUser.getuId();
//        new Thread(()->{
//            try {
//                // Create JSON object
//                JSONObject jsonObject1 = new JSONObject();
//                jsonObject1.put("userId", userId);
//
//                RequestBody requestBody = RequestBody.create(
//                        jsonObject1.toString(),
//                        MediaType.get("application/json; charset=utf-8")
//                );
//
//                OkHttpClient okHttpClient = new OkHttpClient.Builder()
//                        .cookieJar(new PersistentCookieJar(getApplicationContext()))
//                        .build();
//                Request request = new Request.Builder()
//                        .url(UrlConstant.baseUrl + "api/user/getUserById")
//                        .post(requestBody)
//                        .build();
//                Response response = okHttpClient.newCall(request).execute();
//                String responseBody = response.body().string();
//                JSONObject jsonObject = new JSONObject(responseBody);
//                int code = jsonObject.getInt("code");
//                if (code == 0) {
//                    JSONObject userData = jsonObject.getJSONObject("data");
//                    String userAvatar = userData.getString("userAvatar");
//                    String username = userData.getString("userName");
//                    User user = new User();
//                    user.setUserName(username);
//                    user.setUserAvatar(userAvatar);
//                    userCache.put(userId, user);
//                    new Handler(Looper.getMainLooper()).post(() -> {
//                        postUserToVO(user,commentAndUser);
//                    });
//                }
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }).start();
//
//    }
    private void loadUser(CommentAndUser commentAndUser) {
        Long userId = commentAndUser.getuId();
        if (userCache.containsKey(userId)) {
            postUserToVO(userCache.get(userId), commentAndUser);
            return;
        }

        try {
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("userId", userId);

            RequestBody requestBody = RequestBody.create(
                    jsonObject1.toString(),
                    MediaType.get("application/json; charset=utf-8")
            );

            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .cookieJar(new PersistentCookieJar(getApplicationContext()))
                    .build();
            Request request = new Request.Builder()
                    .url(UrlConstant.baseUrl + "api/user/getUserById")
                    .post(requestBody)
                    .build();
            Response response = okHttpClient.newCall(request).execute();
            String responseBody = response.body().string();
            JSONObject jsonObject = new JSONObject(responseBody);
            int code = jsonObject.getInt("code");
            if (code == 0) {
                JSONObject userData = jsonObject.getJSONObject("data");
                String userAvatar = userData.getString("userAvatar");
                String username = userData.getString("userName");
                User user = new User();
                user.setUserName(username);
                user.setUserAvatar(userAvatar);
                userCache.put(userId, user);
                postUserToVO(user, commentAndUser);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private List<Comment> parseComments(String responseBody) {
        // Implement parsing logic
        // Convert JSON response to List<Comment>
        return new ArrayList<>();
    }


    private void followUser() {

    }

    //点赞取消点赞
    private void likeOrUnlikePost(Long postId){
        new Thread(()->{
            try {
                // Create JSON object
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("postId", postId);

                RequestBody requestBody = RequestBody.create(
                        jsonObject1.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .cookieJar(new PersistentCookieJar(getApplicationContext()))
                        .build();
                Request request = new Request.Builder()
                        .url(UrlConstant.baseUrl + "api/post/likePost")
                        .post(requestBody)
                        .build();
                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                int code = jsonObject.getInt("code");
                if (code == 0) {
                    JSONObject userData = jsonObject.getJSONObject("data");
                    Long likes = userData.getLong("likes");
                    runOnUiThread(()->{
                        if(isLike){
                            isLike = false;
                            Toast.makeText(this,"取消点赞成功",Toast.LENGTH_SHORT).show();
                        }else{
                            isLike = true;
                            Toast.makeText(this,"点赞成功",Toast.LENGTH_SHORT).show();
                        }
                        updateLikeButton();
                        likeCount.setText(String.valueOf(likes));
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    //收藏取消收藏
    private void CollectOrunCollectPost(Long postId){
        new Thread(()->{
            try {
                // Create JSON object
                JSONObject jsonObject1 = new JSONObject();
                jsonObject1.put("collectId", postId);
                jsonObject1.put("type",0);

                RequestBody requestBody = RequestBody.create(
                        jsonObject1.toString(),
                        MediaType.get("application/json; charset=utf-8")
                );

                OkHttpClient okHttpClient = new OkHttpClient.Builder()
                        .cookieJar(new PersistentCookieJar(getApplicationContext()))
                        .build();
                Request request = new Request.Builder()
                        .url(UrlConstant.baseUrl + "api/post/collectPost")
                        .post(requestBody)
                        .build();
                Response response = okHttpClient.newCall(request).execute();
                String responseBody = response.body().string();
                JSONObject jsonObject = new JSONObject(responseBody);
                int code = jsonObject.getInt("code");
                if (code == 0) {
                    JSONObject userData = jsonObject.getJSONObject("data");
                    Long collectNum = userData.getLong("collectNum");
                    runOnUiThread(()->{
                        if(isCollect){
                            isCollect= false;
                            Toast.makeText(this,"取消收藏成功",Toast.LENGTH_SHORT).show();
                        }else{
                            isCollect = true;
                            Toast.makeText(this,"收藏成功",Toast.LENGTH_SHORT).show();
                        }
                        updatecollectButton();
                        collectCount.setText(String.valueOf(collectNum));
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void postComment(Long postId) {
        // Implement post comment logic
    }
}

