package com.kajianid.ustadz.ui.credential;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.kajianid.ustadz.R;
import com.kajianid.ustadz.data.Ustadz;
import com.kajianid.ustadz.databinding.ActivityEditIdentityBinding;
import com.kajianid.ustadz.prefs.CredentialPreference;
import com.kajianid.ustadz.utils.StringHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.Header;

public class EditIdentityActivity extends AppCompatActivity {

    public static final String EXTRA_PARCEL_USTADZ = "extra_parcel_ustadz";

    private ActivityEditIdentityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditIdentityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setFinishOnTouchOutside(false);

        binding.rlButtons.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.GONE);
        binding.btnCancel.setOnClickListener(it -> finish());

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            Ustadz ustadz = bundle.getParcelable(EXTRA_PARCEL_USTADZ);

            String name = ustadz.getName();
            String phone = ustadz.getPhone();
            String address = ustadz.getAddress();
            String email = ustadz.getEmail();
            String gender = ustadz.getGender();

            binding.edtUstadzName.setText(name);
            binding.edtPhoneNumber.setText(phone);
            binding.edtUstadzAddress.setText(address);
            binding.edtEmailAddress.setText(email);
            if (gender.equalsIgnoreCase("L"))
                binding.rgGender.check(R.id.rdMale);
            else
                binding.rgGender.check(R.id.rdFemale);

            binding.btnChange.setOnClickListener(it -> {
                boolean isError = false;
                if (StringHelper.isNullOrEmpty(binding.edtUstadzName.getText().toString())) {
                    isError = true;
                    binding.edtUstadzName.setError(getString(R.string.require_to_fill));
                }
                if (StringHelper.isNullOrEmpty(binding.edtEmailAddress.getText().toString())) {
                    isError = true;
                    binding.edtUstadzName.setError(getString(R.string.require_to_fill));
                } else if (!StringHelper.isValidEmail(binding.edtEmailAddress.getText().toString())) {
                    isError = true;
                    binding.edtEmailAddress.setError(getString(R.string.invalid_email));
                }
                if (StringHelper.isNullOrEmpty(binding.edtPhoneNumber.getText().toString())) {
                    isError = true;
                    binding.edtPhoneNumber.setError(getString(R.string.require_to_fill));
                }
                if (!isError) {
                    CredentialPreference preference = new CredentialPreference(this);
                    binding.rlButtons.setVisibility(View.GONE);
                    binding.progressBar.setVisibility(View.VISIBLE);

                    String ustadzGender = (binding.rgGender.getCheckedRadioButtonId() == R.id.rdMale) ? "L" : "P";
                    String api = getString(R.string.server) + "api/ustadz/identity/" + preference.getCredential().getUsername();
                    AsyncHttpClient client = new AsyncHttpClient();

                    RequestParams params = new RequestParams();
                    params.put("name", binding.edtUstadzName.getText().toString());
                    params.put("phone", binding.edtPhoneNumber.getText().toString());
                    params.put("gender", ustadzGender);
                    params.put("address", binding.edtUstadzAddress.getText().toString());
                    params.put("email", binding.edtEmailAddress.getText().toString());

                    client.put(api, params, new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            Toast.makeText(EditIdentityActivity.this, getString(R.string.success_modify_identity), Toast.LENGTH_SHORT).show();
                            EditIdentityActivity.this.finish();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            Toast.makeText(EditIdentityActivity.this, getString(R.string.failed_modify_identity) + error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}