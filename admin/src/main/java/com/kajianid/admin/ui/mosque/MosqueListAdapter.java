package com.kajianid.admin.ui.mosque;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.kajianid.admin.R;
import com.kajianid.admin.data.Mosque;
import com.kajianid.admin.databinding.ItemMosqueListBinding;
import com.kajianid.ustadz.prefs.CredentialPreference;
import com.kajianid.admin.utils.StringHelper;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class MosqueListAdapter extends RecyclerView.Adapter<MosqueListAdapter.MosqueChooserViewHolder> {

    private final ArrayList<Mosque> mosques = new ArrayList<>();

    public void setMosques(ArrayList<Mosque> mosques) {
        this.mosques.clear();
        this.mosques.addAll(mosques);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MosqueChooserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMosqueListBinding binding = ItemMosqueListBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new MosqueChooserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MosqueChooserViewHolder holder, int position) {
        try {
            holder.bind(mosques.get(position), position);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mosques.size();
    }

    public class MosqueChooserViewHolder extends RecyclerView.ViewHolder {

        private final ItemMosqueListBinding binding;

        public MosqueChooserViewHolder(@NonNull ItemMosqueListBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        @SuppressLint("QueryPermissionsNeeded")
        public void bind(Mosque mosque, int position) throws IOException {
            binding.tvMosqueName.setText(mosque.getMosqueName());
            if (StringHelper.isNullOrEmpty(mosque.getAddress())) {
                String latLng = mosque.getLatLng();
                String[] latLngArray = latLng.split(",");
                double latitude = Double.parseDouble(latLngArray[0]);
                double longitude = Double.parseDouble(latLngArray[1]);
                LatLng location = new LatLng(latitude, longitude);
                Geocoder geocoder = new Geocoder(binding.getRoot().getContext(), Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                );

                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                String state = addresses.get(0).getAdminArea();
                String country = addresses.get(0).getCountryName();
                String postalCode = addresses.get(0).getPostalCode();

                String fullAddress = address + ", " + city + ", " + state + ", " + country + " " + postalCode;
                binding.tvMosqueAddress.setText(fullAddress);
            } else binding.tvMosqueAddress.setText(mosque.getAddress());

            binding.getRoot().setOnClickListener(it -> new AlertDialog.Builder(binding.getRoot().getContext())
                    .setMessage(R.string.choose_action_mosque)
                    .setPositiveButton(R.string.view_on_maps, (a, b) -> {
                        Intent mapIntent = new Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse(mosque.getLatLng() + "?q=" + mosque.getLatLng() + "(" + mosque.getMosqueName() + ")")
                        );
                        mapIntent.setPackage("com.google.android.apps.maps");
                        if (mapIntent.resolveActivity(binding.getRoot().getContext().getPackageManager()) != null)
                            binding.getRoot().getContext().startActivity(mapIntent);
                        else
                            Toast.makeText(binding.getRoot().getContext(), "Aplikasi com.google.android.apps.maps tidak ada", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton(R.string.delete, (a, b) -> new AlertDialog.Builder(binding.getRoot().getContext())
                            .setMessage(binding.getRoot().getContext().getString(R.string.delete_mosque_confirm) + mosque.getMosqueName() + "?")
                            .setPositiveButton(R.string.yes, (c, d) -> {
                                CredentialPreference preference = new CredentialPreference(binding.getRoot().getContext());
                                String ustadzId = preference.getCredential().getUsername();
                                String api = binding.getRoot().getContext().getString(R.string.server) + "api/mosques/" + mosque.getId() + "/" + ustadzId;
                                AsyncHttpClient client = new AsyncHttpClient();
                                client.delete(api, new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                                        mosques.remove(position);
                                        notifyDataSetChanged();
                                        Toast.makeText(
                                                binding.getRoot().getContext(),
                                                binding.getRoot().getContext().getString(R.string.delete_mosque_success) + mosque.getMosqueName() + "!",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                                        Toast.makeText(
                                                binding.getRoot().getContext(),
                                                binding.getRoot().getContext().getString(R.string.delete_mosque_failed) + mosque.getMosqueName() + "!",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                });
                            })
                            .setNegativeButton(R.string.no, null)
                            .create().show())
                    .setNeutralButton(R.string.cancel, null)
                    .create().show());
        }
    }
}
