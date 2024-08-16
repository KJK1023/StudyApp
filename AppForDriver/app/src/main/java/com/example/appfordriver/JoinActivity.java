package com.example.appfordriver;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.AuthResult;
import com.google.android.gms.tasks.OnCompleteListener;
import androidx.annotation.NonNull;

public class JoinActivity extends AppCompatActivity {

    private EditText editTextName;
    private EditText editTextUsername;
    private EditText editTextPassword;
    private EditText editTextConfirmPassword;
    private EditText editTextPhoneNumber;
    private EditText editEmergency;
    private Button buttonSignUp;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join);

        // FirebaseAuth 인스턴스 초기화
        mAuth = FirebaseAuth.getInstance();

        editEmergency = findViewById(R.id.editEmergency);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextConfirmPassword = findViewById(R.id.editTextConfirmPassword);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        buttonSignUp = findViewById(R.id.buttonSignUp);

        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = editTextName.getText().toString().trim();
                String username = editTextUsername.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String confirmPassword = editTextConfirmPassword.getText().toString().trim();
                String phoneNumber = editTextPhoneNumber.getText().toString().trim();
                String emergency = editEmergency.getText().toString().trim();

                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password) ||
                        TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(phoneNumber) || TextUtils.isEmpty(emergency)) {
                    Toast.makeText(JoinActivity.this, "모든 필드를 입력하세요.", Toast.LENGTH_SHORT).show();
                } else if (!password.equals(confirmPassword)) {
                    Toast.makeText(JoinActivity.this, "비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                } else {
                    // 회원가입 로직 추가
                    registerUser(username, password);
                }
            }
        });
    }

    private void registerUser(String username, String password) {
        mAuth.createUserWithEmailAndPassword(username, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // 회원가입 성공
                            FirebaseUser user = mAuth.getCurrentUser();
                            Toast.makeText(JoinActivity.this, "회원가입 성공!", Toast.LENGTH_SHORT).show();
                            // 여기서 추가적으로 사용자 정보를 저장할 수 있습니다.
                            // 예를 들어, Firestore에 사용자 데이터를 저장하는 등.
                        } else {
                            // 회원가입 실패
                            Toast.makeText(JoinActivity.this, "회원가입 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}