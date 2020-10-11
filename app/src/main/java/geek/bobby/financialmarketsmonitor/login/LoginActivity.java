package geek.bobby.financialmarketsmonitor.login;


import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import geek.bobby.financialmarketsmonitor.R;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText usernameEditText, passwordEditText;
    private View loginButton;
    private ProgressBar loadingProgressBar;

    final static String TAG = "LoginActivity";


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mAuth = FirebaseAuth.getInstance();

        usernameEditText = findViewById(R.id.username);
        passwordEditText = findViewById(R.id.password);
        loginButton = findViewById(R.id.login);
        loadingProgressBar = findViewById(R.id.loading);

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginButton.setEnabled(true);
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);

        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    startValidation();
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startValidation();
            }
        });
    }

    private void showLoginFailed(String errorString) {

        Toast.makeText(this, errorString, Toast.LENGTH_LONG).show();
    }

    private void startValidation()
    {
        loginButton.setEnabled(false);
        validateInput();
    }

    private void validateInput()
    {
        final String email = usernameEditText.getText().toString().trim();
        if (email.length()==0)
        {
            usernameEditText.setError(getString(R.string.error_can_not_be_empty));
            return;
        }

        final String password = passwordEditText.getText().toString().trim();
        if (password.length()<=5)
        {
            passwordEditText.setError(getString(R.string.invalid_password));
            return;
        }

        usernameEditText.setEnabled(false);
        passwordEditText.setEnabled(false);
        loadingProgressBar.setVisibility(View.VISIBLE);
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            //FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());

                            loadingProgressBar.setVisibility(View.INVISIBLE);
                            loginButton.setEnabled(true);
                            usernameEditText.setEnabled(true);
                            passwordEditText.setEnabled(true);

                            // com.google.firebase.FirebaseNetworkException: A network error
                            // com.google.firebase.auth.FirebaseAuthInvalidCredentialsException: The password is invalid or the user does not have a password.
                            // com.google.firebase.auth.FirebaseAuthInvalidUserException: There is no user record corresponding to this identifier. The user may have been deleted.

                            if (task.getException() instanceof FirebaseAuthInvalidUserException)
                                usernameEditText.setError(getString(R.string.invalid_username));
                            else if (task.getException() instanceof FirebaseAuthInvalidCredentialsException)
                                passwordEditText.setError(getString(R.string.invalid_password));

                            if (task.getException() instanceof FirebaseException)
                                showLoginFailed(task.getException().getMessage());
                            else
                                showLoginFailed(getString(R.string.login_failed));
                        }
                    }
                });
    }
}