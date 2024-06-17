package com.work37.napnap.ui.userlogin_register;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface UserDao {
    @Insert //插入用户信息
    public void insertUser(User user);

    @Query("DELETE FROM user")   // 删除用户
    public void deleteUser();

    @Query("SELECT * FROM user") //获取用户列表作为LiveData对象
    LiveData<List<User>> getUser();
}
