package com.kajianid.ustadz.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.kajianid.ustadz.R;
import com.kajianid.ustadz.data.Credential;
import com.kajianid.ustadz.databinding.ActivityLoginBinding;
import com.kajianid.ustadz.prefs.CredentialPreference;
import com.kajianid.ustadz.ui.main.MainActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Glide.with(this)
                .load(R.drawable.icon)
                .into(binding.contentLogin.namaSlogan3);

        binding.contentLogin.showPass.setOnClickListener(itView -> {
            if (binding.contentLogin.showPass.isChecked())
                binding.contentLogin.edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            else
                binding.contentLogin.edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        });

        binding.btnLogin.setOnClickListener(itView -> {
            binding.btnLogin.setVisibility(View.GONE);
            binding.progressBar.setVisibility(View.VISIBLE);

            CredentialPreference preference = new CredentialPreference(this);
            String api = getString(R.string.server) + "api/ustadz/credential";
            AsyncHttpClient client = new AsyncHttpClient();

            String username = binding.contentLogin.edtUsername.getText().toString();
            String password = binding.contentLogin.edtPassword.getText().toString();

            RequestParams params = new RequestParams();
            params.put("username", username);
            params.put("password", password);

            client.post(api, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String response = new String(responseBody);
                        JSONObject responseObject = new JSONObject(response);

                        Credential credential = new Credential();
                        credential.setUsername(username);
                        credential.setPassword(password);
                        preference.setCredential(credential);

                        String getUsername = responseObject.getString("username");
                        Toast.makeText(
                                LoginActivity.this,
                                getString(R.string.welcome) + getUsername,
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent i = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(i);
                        LoginActivity.this.finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.btnLogin.setVisibility(View.VISIBLE);
                    switch (statusCode) {
                        case 403:
                            Toast.makeText(
                                    LoginActivity.this,
                                    getString(R.string.wrong_username_password),
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;
                        case 401:
                            Toast.makeText(
                                    LoginActivity.this,
                                    getString(R.string.empty_username_password),
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;
                        default:
                            Toast.makeText(
                                    LoginActivity.this,
                                    error.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;
                    }
                }
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}