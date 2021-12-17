package com.example.musiclovers.signIn_signUpActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.musiclovers.PlaceHolder;
import com.example.musiclovers.R;
import com.example.musiclovers.models.userItem;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * DONE
 */
public class createNewAccount extends AppCompatActivity {

    EditText createAccountUsername;
    EditText createAccountEmail;
    EditText createAccountPassword;
    Button createAccountSignUp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:3000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //Let retrofit do its job
        PlaceHolder placeHolder = retrofit.create(PlaceHolder.class);
        findViewById();

        createAccountSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = createAccountUsername.getText().toString();
                String emailAddress = createAccountEmail.getText().toString();
                String password = createAccountPassword.getText().toString();
                if(!userName.isEmpty() && !emailAddress.isEmpty() && !password.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()){
                    Call<userItem> call = placeHolder.register(userName, emailAddress, password);
                    call.enqueue(new Callback<userItem>() {
                        @Override
                        public void onResponse(Call<userItem> call, Response<userItem> response) {
                            if(!response.isSuccessful()) {
                                Toast.makeText(createNewAccount.this, response.code() + ": " + response.message(), Toast.LENGTH_SHORT).show();
                                return;
                            }
                            Toast.makeText(createNewAccount.this, "Success!!!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(Call<userItem> call, Throwable t) {
                            Toast.makeText(createNewAccount.this, "\nSomething went wrong! 😢 \n Please try again! \n", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void findViewById(){
        createAccountUsername = findViewById(R.id.activity_create_account_username);
        createAccountEmail = findViewById(R.id.activity_create_account_emailAddress);
        createAccountPassword = findViewById(R.id.activity_create_account_password);
        createAccountSignUp = findViewById(R.id.activity_create_account_signup);
    }
}
