package pl.jw.android.gamescheduler;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import butterknife.Bind;
import butterknife.ButterKnife;
import pl.jw.android.gamescheduler.rest.data.LoginRequest;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

/**
 * A login screen that offers login via email/password.
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    @Bind(R.id.nameAlias)
    AutoCompleteTextView nameAliasView;
    @Bind(R.id.password)
    EditText passwordView;
    @Bind(R.id.login_progress)
    View progressView;
    @Bind(R.id.login_form)
    View formView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //jeśli kiedykolwiek było udane logowanie - wszystko wiadomo i nie ma sensu pokazywać widoku
        if (GameSchedulerApplication.getInstance().isLoggedIn()) {
            loginSuccess(GameSchedulerApplication.getInstance().getUserNameAlias());
            return;
        }

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        passwordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

    }


    private void attemptLogin() {

        // Reset errors.
        nameAliasView.setError(null);
        passwordView.setError(null);

        // Store values at the time of the login attempt.
        final String nameAlias = nameAliasView.getText().toString();
        String password = passwordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if (!isPasswordValid(password)) {
            passwordView.setError(getString(R.string.error_field_required));
            focusView = passwordView;
            cancel = true;
        }

        if (TextUtils.isEmpty(nameAlias) && nameAlias.trim().length() > 4) {
            nameAliasView.setError(getString(R.string.error_field_name_required, 4));
            focusView = nameAliasView;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            showProgress(true);

            LoginRequest loginRequest = new LoginRequest(GameSchedulerApplication.getInstance().getUserName(), nameAlias, password);
            GameSchedulerApplication.getInstance().getRestApi(this).login(loginRequest).subscribeOn(Schedulers.newThread()).
                    observeOn(AndroidSchedulers.mainThread()).subscribe(new Action1<Boolean>() {
                @Override
                public void call(Boolean success) {

                    if (success) {
                        loginSuccess(nameAlias);
                    } else {
                        loginFailed();
                    }
                }
            }, new Action1<Throwable>() {
                @Override
                public void call(Throwable throwable) {
                    showProgress(false);

                    loginFailed();

                    Log.d(TAG, "Błąd logowania.", throwable);
                }
            }, new Action0() {
                @Override
                public void call() {
                    showProgress(false);
                }
            });
        }
    }

    private void loginSuccess(String userAlias) {
        GameSchedulerApplication.getInstance().saveUserNameAlias(userAlias);

        finish();

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        LoginActivity.this.startActivity(intent);
    }

    private void loginFailed() {
        passwordView.setError(getString(R.string.error_login_incorrect));
        passwordView.requestFocus();
    }

    private boolean isPasswordValid(String password) {
        return !TextUtils.isEmpty(password) && password.trim().length() > 1;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            formView.setVisibility(show ? View.GONE : View.VISIBLE);
            formView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    formView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            formView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }


}

