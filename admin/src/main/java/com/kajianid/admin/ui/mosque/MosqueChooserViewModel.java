package com.kajianid.admin.ui.mosque;

import android.content.Context;
import android.os.Looper;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.kajianid.admin.R;
import com.kajianid.admin.data.Mosque;
import com.kajianid.admin.prefs.CredentialPreference;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class MosqueChooserViewModel extends ViewModel {

    private MutableLiveData<ArrayList<Mosque>> listMosques = new MutableLiveData<>();

    public LiveData<ArrayList<Mosque>> getMosques() {
        return listMosques;
    }

    public String setMosques(Context context, String searchQuery, boolean isUstadzOnly) {
        try {
            Looper.prepare();
        } catch (Exception ignored) {
        }
        final String[] ret = {null};
        CredentialPreference credential = new CredentialPreference(context);
        String endpoint = (isUstadzOnly) ? "api/mosques/ustadz" : "api/mosques/user";
        String url = context.getResources().getString(R.string.server) + endpoint;
        ArrayList<Mosque> listItems = new ArrayList<>();
        SyncHttpClient client = new SyncHttpClient();

        RequestParams params = new RequestParams();
        if (isUstadzOnly) params.put("ustadz_id", credential.getCredential().getUsername());
        params.put("q", searchQuery);

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject resultObject = new JSONObject(result);
                    JSONArray list = resultObject.getJSONArray("data");

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject mosqueJson = list.getJSONObject(i);
                        Mosque mosque = new Mosque();

                        int mosqueId = (isUstadzOnly) ? mosqueJson.getInt("mosque_id") : mosqueJson.getInt("id");
                        mosque.setId(mosqueId);
                        mosque.setMosqueName(mosqueJson.getString("name"));
                        mosque.setAddress(mosqueJson.getString("address"));
                        mosque.setLatLng(mosqueJson.getString("lat_lng"));

                        listItems.add(mosque);
                    }

                    listMosques.postValue(listItems);
                    ret[0] = null;
                } catch (JSONException e) {
                    e.printStackTrace();
                    String expected = new String(responseBody);
                    ret[0] = expected;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
                String expected = new String(responseBody);
                ret[0] = expected;
            }
        });

        return ret[0];
    }

    public boolean setMosqueAsync(Context context,
                                  MosqueListAdapter adapter,
                                  String searchQuery,
                                  Boolean isUstadzOnly) {
        final boolean[] ret = {false};
        CredentialPreference credential = new CredentialPreference(context);
        String endpoint = (isUstadzOnly) ? "api/mosques/ustadz" : "api/mosques/user";
        String url = context.getResources().getString(R.string.server) + endpoint;
        ArrayList<Mosque> listItems = new ArrayList<>();
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        if (isUstadzOnly) params.put("ustadz_id", credential.getCredential().getUsername());
        params.put("q", searchQuery);

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject resultObject = new JSONObject(result);
                    JSONArray list = resultObject.getJSONArray("data");

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject mosqueJson = list.getJSONObject(i);
                        Mosque mosque = new Mosque();

                        int mosqueId = (isUstadzOnly) ? mosqueJson.getInt("mosque_id") : mosqueJson.getInt("id");
                        mosque.setId(mosqueId);
                        mosque.setMosqueName(mosqueJson.getString("name"));
                        mosque.setAddress(mosqueJson.getString("address"));
                        mosque.setLatLng(mosqueJson.getString("lat_lng"));

                        listItems.add(mosque);
                        adapter.notifyDataSetChanged();
                    }

                    listMosques.postValue(listItems);
                    ret[0] = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Failure!\n" + e.getMessage() + "\n${onSuccess()}\n${url}", Toast.LENGTH_SHORT).show();
                    ret[0] = false;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
                String expected = new String(responseBody);
                Toast.makeText(context, "Failure!\n" + error.getMessage() + "\n" + expected, Toast.LENGTH_SHORT).show();
                ret[0] = false;
            }
        });

        return ret[0];
    }

    public boolean setMosqueAsync(Context context,
                                  MosqueChooserAdapter adapter,
                                  String searchQuery,
                                  Boolean isUstadzOnly) {
        final boolean[] ret = {false};
        CredentialPreference credential = new CredentialPreference(context);
        String endpoint = (isUstadzOnly) ? "api/mosques/ustadz" : "api/mosques/user";
        String url = context.getResources().getString(R.string.server) + endpoint;
        ArrayList<Mosque> listItems = new ArrayList<>();
        AsyncHttpClient client = new AsyncHttpClient();

        RequestParams params = new RequestParams();
        if (isUstadzOnly) params.put("ustadz_id", credential.getCredential().getUsername());
        params.put("q", searchQuery);

        client.get(url, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                try {
                    String result = new String(responseBody);
                    JSONObject resultObject = new JSONObject(result);
                    JSONArray list = resultObject.getJSONArray("data");

                    for (int i = 0; i < list.length(); i++) {
                        JSONObject mosqueJson = list.getJSONObject(i);
                        Mosque mosque = new Mosque();

                        int mosqueId = (isUstadzOnly) ? mosqueJson.getInt("mosque_id") : mosqueJson.getInt("id");
                        mosque.setId(mosqueId);
                        mosque.setMosqueName(mosqueJson.getString("name"));
                        mosque.setAddress(mosqueJson.getString("address"));
                        mosque.setLatLng(mosqueJson.getString("lat_lng"));

                        listItems.add(mosque);
                        adapter.notifyDataSetChanged();
                    }

                    listMosques.postValue(listItems);
                    ret[0] = true;
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(context, "Failure!\n" + e.getMessage() + "\n${onSuccess()}\n${url}", Toast.LENGTH_SHORT).show();
                    ret[0] = false;
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                error.printStackTrace();
                String expected = new String(responseBody);
                Toast.makeText(context, "Failure!\n" + error.getMessage() + "\n" + expected, Toast.LENGTH_SHORT).show();
                ret[0] = false;
            }
        });

        return ret[0];
    }

}
