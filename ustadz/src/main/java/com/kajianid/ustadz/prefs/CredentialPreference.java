package com.kajianid.ustadz.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.kajianid.ustadz.data.Credential;

public class CredentialPreference {

    private static final String PREFERENCE_NAME = "credential_preference";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";

    private final SharedPreferences sharedPreferences;

    public CredentialPreference(Context context) {
        sharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public Credential getCredential() {
        Credential credential = new Credential();
        credential.setUsername(sharedPreferences.getString(USERNAME, ""));
        credential.setPassword(sharedPreferences.getString(PASSWORD, ""));
        return credential;
    }

    public void setCredential(Credential credential) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(USERNAME, credential.getUsername());
        editor.putString(PASSWORD, credential.getPassword());
        editor.apply();
    }
}
