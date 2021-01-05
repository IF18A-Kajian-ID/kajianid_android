package com.kajianid.ustadz.ui.mosque;

import android.app.Activity;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.kajianid.ustadz.data.Mosque;
import com.kajianid.ustadz.databinding.ItemMosqueBinding;
import com.kajianid.ustadz.utils.StringHelper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MosqueChooserAdapter extends RecyclerView.Adapter<MosqueChooserAdapter.MosqueChooserViewHolder> {

    private final ArrayList<Mosque> mosques = new ArrayList<>();

    public void setMosques(ArrayList<Mosque> mosques) {
        this.mosques.clear();
        this.mosques.addAll(mosques);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MosqueChooserAdapter.MosqueChooserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMosqueBinding binding = ItemMosqueBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new MosqueChooserViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MosqueChooserAdapter.MosqueChooserViewHolder holder, int position) {
        try {
            holder.bind(mosques.get(position));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mosques.size();
    }

    public static class MosqueChooserViewHolder extends RecyclerView.ViewHolder {

        private final ItemMosqueBinding binding;

        public MosqueChooserViewHolder(@NonNull ItemMosqueBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(Mosque mosque) throws IOException {
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

            binding.getRoot().setOnClickListener(it -> {
                Intent i = new Intent();
                i.putExtra(MosqueChooserActivity.EXTRA_MOSQUE_RESULT, mosque);
                ((Activity) binding.getRoot().getContext()).setResult(MosqueChooserActivity.REQUEST_MOSQUE, i);
                ((Activity) binding.getRoot().getContext()).finish();
            });
        }
    }
}
