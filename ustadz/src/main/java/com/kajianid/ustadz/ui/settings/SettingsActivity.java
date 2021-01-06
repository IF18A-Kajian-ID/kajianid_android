package com.kajianid.ustadz.ui.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.kajianid.ustadz.R;
import com.kajianid.ustadz.data.Credential;
import com.kajianid.ustadz.data.Ustadz;
import com.kajianid.ustadz.databinding.ActivitySettingsBinding;
import com.kajianid.ustadz.prefs.CredentialPreference;
import com.kajianid.ustadz.ui.credential.EditIdentityActivity;
import com.kajianid.ustadz.ui.credential.EditPasswordActivity;
import com.kajianid.ustadz.ui.login.LoginActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import cz.msebera.android.httpclient.Header;

public class SettingsActivity extends AppCompatActivity {

    private ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Objects.requireNonNull(getSupportFragmentManager())
                .beginTransaction()
                .replace(R.id.settings, new SettingsFragment())
                .commit();
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Objects.requireNonNull(getSupportActionBar()).setTitle(getString(R.string.settings));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) super.onBackPressed();
        return true;
    }

    public static class SettingsFragment extends PreferenceFragmentCompat {

        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);

            ListPreference theme = findPreference("theme");
            Preference changePassword = findPreference("changePassword");
            Preference changeIdentity = findPreference("changeIdentity");
            Preference logout = findPreference("logout");

            theme.setOnPreferenceChangeListener((preference, newValue) -> {
                switch (newValue.toString()) {
                    case "dark":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                        break;
                    case "light":
                        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                        break;
                }

                return true;
            });

            changeIdentity.setOnPreferenceClickListener(it -> {
                CredentialPreference preference = new CredentialPreference(requireContext());
                Snackbar.make(requireView().getRootView(), getString(R.string.please_wait), BaseTransientBottomBar.LENGTH_SHORT).show();
                String api = getString(R.string.server) + "api/ustadz/identity/" + preference.getCredential().getUsername();
                AsyncHttpClient client = new AsyncHttpClient();

                client.get(api, new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        try {
                            String result = new String(responseBody);
                            JSONObject resultObject = new JSONObject(result);

                            Ustadz ustadz = new Ustadz();
                            ustadz.setName(resultObject.getString("name"));
                            ustadz.setAddress(resultObject.getString("address"));
                            ustadz.setEmail(resultObject.getString("email"));
                            ustadz.setPhone(resultObject.getString("phone"));
                            ustadz.setGender(resultObject.getString("gender"));

                            Intent i = new Intent(getContext(), EditIdentityActivity.class);
                            i.putExtra(EditIdentityActivity.EXTRA_PARCEL_USTADZ, ustadz);
                            startActivity(i);
                        } catch (JSONException e) {
                            Snackbar.make(requireView().getRootView(), getString(R.string.failed_load_data) + ": Failed Parsing", BaseTransientBottomBar.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Snackbar.make(requireView().getRootView(), getString(R.string.failed_load_data), BaseTransientBottomBar.LENGTH_SHORT).show();
                    }
                });

                return true;
            });

            changePassword.setOnPreferenceClickListener(it -> {
                Intent i = new Intent(getContext(), EditPasswordActivity.class);
                startActivity(i);

                return true;
            });

            logout.setOnPreferenceClickListener(it -> {
                new AlertDialog.Builder(requireContext())
                        .setTitle(getString(R.string.sure))
                        .setMessage(getString(R.string.logout_confirm))
                        .setPositiveButton(getString(R.string.yes), (a, b) -> {
                            CredentialPreference preference = new CredentialPreference(requireContext());
                            Credential credential = new Credential();

                            credential.setUsername("");
                            credential.setPassword("");
                            preference.setCredential(credential);

                            Toast.makeText(requireContext(), getString(R.string.goodbye), Toast.LENGTH_SHORT).show();

                            Intent i = new Intent(getContext(), LoginActivity.class);
                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(i);
                            requireActivity().finishAffinity();
                        })
                        .setNegativeButton(getString(R.string.no), null)
                        .create()
                        .show();

                return true;
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}