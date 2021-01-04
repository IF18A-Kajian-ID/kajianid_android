package com.kajianid.ustadz.ui.credential;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.kajianid.ustadz.R;
import com.kajianid.ustadz.data.Credential;
import com.kajianid.ustadz.databinding.ActivityEditPasswordBinding;
import com.kajianid.ustadz.prefs.CredentialPreference;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class EditPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityEditPasswordBinding binding = ActivityEditPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setFinishOnTouchOutside(false);

        binding.btnCancel.setOnClickListener(it -> finish());

        binding.btnChange.setOnClickListener(it -> {
            CredentialPreference preference = new CredentialPreference(this);
            String oldPassword = preference.getCredential().getPassword();

            binding.edtOldPassword.setError(null);
            binding.edtNewPassword.setError(null);
            binding.edtConfirmPassword.setError(null);

            if (binding.edtOldPassword.getText().toString().equals(oldPassword)) {
                if (binding.edtNewPassword.getText().toString().length() < 6) {
                    // new password kurang dari 6
                    binding.edtNewPassword.setError(getString(R.string.new_password_not_meet_minimum));
                }
                if (binding.edtConfirmPassword.getText().toString().length() < 6) {
                    // confirm password kurang dari 6
                    binding.edtConfirmPassword.setError(getString(R.string.confirm_password_not_meet_minimum));
                }
                if ((binding.edtNewPassword.getText().toString().length() >= 6) &&
                        (binding.edtConfirmPassword.getText().toString().length() >= 6)) {
                    if (binding.edtNewPassword.getText().toString().equals(
                            binding.edtConfirmPassword.getText().toString()
                    )) {
                        binding.rlButtons.setVisibility(View.GONE);
                        binding.progressBar.setVisibility(View.VISIBLE);

                        String api = getString(R.string.server) + "api/ustadz/credential";
                        AsyncHttpClient client = new AsyncHttpClient();
                        RequestParams params = new RequestParams();
                        params.put("username", preference.getCredential().getUsername());
                        params.put("old_pass", binding.edtOldPassword.getText().toString());
                        params.put("new_pass", binding.edtNewPassword.getText().toString());

                        client.put(api, params, new AsyncHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                Credential credential = new Credential();
                                credential.setUsername(preference.getCredential().getUsername());
                                credential.setPassword(binding.edtNewPassword.getText().toString());

                                Toast.makeText(EditPasswordActivity.this, getString(R.string.success_change_pass), Toast.LENGTH_SHORT).show();
                                finish();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                Toast.makeText(EditPasswordActivity.this, getString(R.string.failed_change_pass) + error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        binding.edtConfirmPassword.setError(getString(R.string.confirm_password_not_same));
                    }
                }
            } else {
                binding.edtOldPassword.setError(getString(R.string.wrong_old_pass));
            }
        });
    }
}