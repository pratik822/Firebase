package firebase.com.labs.ramdani.mygroupchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText edtName, edtPassword, edtEmail;
    private Button btnRegister;
    private Button btn_login;
    private ProgressBar progress;

    //initial
    private FirebaseAuth mFirebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        //initial
        mFirebaseAuth = FirebaseAuth.getInstance();
        setupUI();

        AppPreference mAppPreference = new AppPreference(this);
        if (!TextUtils.isEmpty(mAppPreference.getEmail())) {
            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
            finish();
        }

    }

    public void setupUI() {
        edtName = (EditText) findViewById(R.id.edt_name);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        edtEmail = (EditText) findViewById(R.id.edt_email);
        btnRegister = (Button) findViewById(R.id.btn_register);
        progress = (ProgressBar) findViewById(R.id.progress);
        progress.setVisibility(View.INVISIBLE);

        btnRegister.setOnClickListener(this);
        btn_login = (Button) findViewById(R.id.btn_login);
        btn_login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.btn_register:
                boolean isEmptyField = false;

                String name = edtName.getText().toString().trim();
                String email = edtEmail.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                //validasi
                if (TextUtils.isEmpty(name)) {
                    isEmptyField = true;
                    edtName.setError("Display name is required");
                }

                if (TextUtils.isEmpty(email)) {
                    isEmptyField = true;
                    edtEmail.setError("Email is required");
                }

                if (TextUtils.isEmpty(password)) {
                    isEmptyField = true;
                    edtPassword.setError("Password is required");
                } else if (password.length() < 6) {
                    isEmptyField = true;
                    edtPassword.setError("Password length should be more than 5 characters");
                }

                if (!isEmptyField) {
                    progress.setVisibility(View.VISIBLE);

                    mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, "Register successfully", Toast.LENGTH_SHORT).show();
                                        progress.setVisibility(View.INVISIBLE);
                                        startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                                        finish();
                                    } else {
                                        progress.setVisibility(View.INVISIBLE);
                                        Toast.makeText(RegisterActivity.this, "Please try again", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
                break;

            case R.id.btn_login:
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                finish();
                break;

        }


    }
}
