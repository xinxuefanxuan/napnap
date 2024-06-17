package com.work37.napnap.ui.userlogin_register;

import androidx.annotation.Nullable;

public class LoginState {
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer  passwordError;
    private boolean isValid;

    LoginState(@Nullable Integer  usernameError, @Nullable Integer  passwordError) {
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.isValid=false;
    }

   LoginState(boolean isValid) {
        this.isValid = isValid;
    }
    @Nullable
    public Integer  isUsernameError() {
        return usernameError;
    }
    @Nullable
    public Integer  isPasswordError() {
        return passwordError;
    }

    public boolean isValid() {
        return isValid;
    }
}
