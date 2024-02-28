package it.uniba.dib.sms232413.Shared;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import it.uniba.dib.sms232413.Doc.DocActivity.RegisterDocActivity;
import it.uniba.dib.sms232413.R;
import it.uniba.dib.sms232413.object.LanguageManager;
import it.uniba.dib.sms232413.object.LoginSession;

// Dichiarazione della classe principale LoginActivity che estende AppCompatActivity
public class LoginActivity extends AppCompatActivity {

    Boolean passwordVisible = false;
    private EditText inputEmail, inputPassword;
    private String email, password;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        // Impostazione della visualizzazione a schermo intero
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // Nascondi la ActionBar se presente
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        // Imposta la lingua corrente
        LanguageManager.applyLanguage(this);

        TextView textView = findViewById(R.id.registernow);

        inputEmail = findViewById(R.id.inputEmail);
        inputPassword = findViewById(R.id.passwordInput);

        TextView forgotPassword = findViewById(R.id.forgotPassword);
        forgotPassword.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, ForgotPasswordActivity.class)));

        Button btnLogin = findViewById(R.id.btnLogin);

        textView.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, RegisterDocActivity.class)));

        btnLogin.setOnClickListener(v -> {
            email = inputEmail.getText().toString();
            password = inputPassword.getText().toString();

            inputEmail.setText(email);
            email = email.toLowerCase(Locale.ROOT);
            LoginSession.login(this, email, password);
        });

        inputPassword.setOnTouchListener((view, motionEvent) -> {
            final int right = 2;
            final int left = 2;
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                if (motionEvent.getRawX() >= inputPassword.getRight() - inputPassword.getCompoundDrawables()[right].getBounds().width()) {
                    int selection = inputPassword.getSelectionEnd();
                    if (passwordVisible) {
                        inputPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_security_24, 0, R.drawable.ic_baseline_visibility_off_24, 0);

                        inputPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                        passwordVisible = false;
                    } else {
                        inputPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.ic_baseline_security_24, 0, R.drawable.ic_baseline_visibility_24, 0);
                        inputPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                        passwordVisible = true;
                    }
                    inputPassword.setSelection(selection);
                    return true;
                }
            }
            return false;
        });
    }
}