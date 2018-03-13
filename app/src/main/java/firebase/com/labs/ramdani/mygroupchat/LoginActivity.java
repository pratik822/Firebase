package firebase.com.labs.ramdani.mygroupchat;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressBar progressBar;
    private EditText edtEmail, edtPassword;
    private Button btnLogin;


    private FirebaseAuth mFirebaseAuth;
    private DatabaseReference mDatabaseReference;
    private FirebaseDatabase mFirebaseDatabase;
    List<User> list = new ArrayList<>();
    AppPreference mAppPreference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_login);
        mAppPreference = new AppPreference(LoginActivity.this);
        setupUI();
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();


    }

    public void setupUI() {
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);

        edtEmail = (EditText) findViewById(R.id.edt_email);
        edtPassword = (EditText) findViewById(R.id.edt_password);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_login) {


            boolean isEmptyField = false;

            final String email = edtEmail.getText().toString().trim();
            String password = edtPassword.getText().toString().trim();

            //validasi

            if (TextUtils.isEmpty(email)) {
                isEmptyField = true;
                edtEmail.setError("Email is required");
            }

            if (TextUtils.isEmpty(password)) {
                isEmptyField = true;
                edtPassword.setError("Password is required");
            }

            if (!isEmptyField) {
                progressBar.setVisibility(View.VISIBLE);


                mFirebaseAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {

                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d("mytask", task.toString());
                                    Query query = mDatabaseReference.child("users").orderByChild("email").equalTo(email);
                                    ValueEventListener valueEventListener = new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            for (DataSnapshot task : dataSnapshot.getChildren()) {
                                                mAppPreference.setEmail(email);
                                                mAppPreference.setusername(task.getValue(User.class).getUserId());
                                                Log.d("myusers", new Gson().toJson(task.getValue(User.class)));


                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {

                                        }
                                    };

                                    query.addValueEventListener(valueEventListener);


                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Login failed", Toast.LENGTH_SHORT).show();
                                    progressBar.setVisibility(View.INVISIBLE);
                                }
                            }
                        });
            }
        }
    }
}
