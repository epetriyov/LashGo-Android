package com.lashgo.android;

import com.lashgo.android.ui.BaseActivity;
import com.lashgo.android.ui.auth.EnterEmailDialog;
import com.lashgo.android.ui.auth.LoginActivity;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Eugene on 24.06.2014.
 */
@Module(
        injects = {
                EnterEmailDialog.class
        },
        complete = false
)
public class LoginModule {

    private final LoginActivity loginActivity;

    public LoginModule(LoginActivity loginActivity) {
        this.loginActivity = loginActivity;
    }

    @Provides
    LoginActivity provideLoginActivity() {
        return loginActivity;
    }
}
