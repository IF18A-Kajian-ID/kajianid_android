package com.kajianid.ustadz.ui.intro;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kajianid.ustadz.R;
import com.kajianid.ustadz.databinding.ActivitySplashBinding;
import com.kajianid.ustadz.prefs.CredentialPreference;
import com.kajianid.ustadz.ui.login.LoginActivity;
import com.kajianid.ustadz.ui.main.MainActivity;
import com.kajianid.ustadz.utils.StringHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Glide.with(this)
                .load(R.drawable.icon)
                .into(binding.namaSlogan);

        SharedPreferences sharedPreferences = getSharedPreferences("root_preferences", Context.MODE_PRIVATE);
        switch (sharedPreferences.getString("theme", "light")) {
            case "dark":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
            case "light":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "auto":
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                break;
        }
        int loadTime = 2000;

        CredentialPreference credentialPreference = new CredentialPreference(this);
        binding.progressBar.setVisibility(View.GONE);
        String version = "Versi" + getString(R.string.app_version);
        binding.version.setText(version);

        new Handler(getMainLooper()).postDelayed(() -> {
            if (sharedPreferences.getBoolean("first", false)) {
                fetchLoginData(
                        credentialPreference.getCredential().getUsername(),
                        credentialPreference.getCredential().getPassword()
                );
            } else {
                Intent intro = new Intent(SplashActivity.this, IntroActivity.class);
                startActivity(intro);
                SplashActivity.this.finish();
            }
        }, loadTime);
    }

    private void fetchLoginData(String username, String password) {
        if (!StringHelper.isNullOrEmpty(username) || !StringHelper.isNullOrEmpty(password)) {
            binding.progressBar.setVisibility(View.VISIBLE);

            String api = getString(R.string.server) + "api/ustadz/credential";
            AsyncHttpClient client = new AsyncHttpClient();

            RequestParams params = new RequestParams();
            params.put("username", username);
            params.put("password", password);

            client.post(api, params, new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    try {
                        String response = new String(responseBody);
                        JSONObject responseObject = new JSONObject(response);

                        String getUsername = responseObject.getString("username");
                        Toast.makeText(
                                SplashActivity.this,
                                getString(R.string.welcome) + getUsername,
                                Toast.LENGTH_SHORT
                        ).show();

                        Intent i = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(i);
                        SplashActivity.this.finish();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    switch (statusCode) {
                        case 401:
                            Toast.makeText(
                                    SplashActivity.this,
                                    getString(R.string.wrong_username_password),
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;
                        case 403:
                            Toast.makeText(
                                    SplashActivity.this,
                                    getString(R.string.empty_username_password),
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;
                        default:
                            Toast.makeText(
                                    SplashActivity.this,
                                    error.getLocalizedMessage(),
                                    Toast.LENGTH_SHORT
                            ).show();
                            break;
                    }
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    SplashActivity.this.finish();
                }
            });
        } else {
            Intent login = new Intent(SplashActivity.this, LoginActivity.class);
            startActivity(login);
            SplashActivity.this.finish();
        }
    }

    @Override
    protected void onDestroy() {
        binding = null;
        super.onDestroy();
    }
}