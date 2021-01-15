package com.kajianid.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.kajianid.android.activities.ShowKajianSQLActivity;
import com.kajianid.android.data.Kajian;
import com.kajianid.android.databinding.ListKajianBinding;
import java.util.ArrayList;

public class KajianSQLAdapter extends RecyclerView.Adapter<KajianSQLAdapter.KajianSQLViewHolder> {
    ArrayList<Kajian> mKajian = new ArrayList<>();

    public KajianSQLAdapter() {
    }

    public void setKajian(ArrayList<Kajian> mKajian) {
        if (mKajian != null) this.mKajian.clear();
        assert mKajian != null;
        this.mKajian.addAll(mKajian);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public KajianSQLViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListKajianBinding binding = ListKajianBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new KajianSQLAdapter.KajianSQLViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull KajianSQLAdapter.KajianSQLViewHolder holder, int position) {
        holder.bind(mKajian.get(position));
    }

    @Override
    public int getItemCount() {
        return mKajian.size();
    }

    public class KajianSQLViewHolder extends RecyclerView.ViewHolder {
        private final ListKajianBinding binding;

        public KajianSQLViewHolder(ListKajianBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(Kajian kajian) {
            binding.TextViewJudul.setText(kajian.getTitle());
            binding.TextViewUstadz.setText(kajian.getUstadzName());
            binding.TextViewMosque.setText(kajian.getMosque());
            binding.TextViewKeterangan.setText(kajian.getDescription());
            binding.TextViewTanggal.setText(kajian.getDate());

            if (kajian.getImgResource().equals("false")) {
                binding.ImageJudul.setVisibility(View.GONE);
            } else {
                Glide.with(binding.getRoot().getContext())
                        .asBitmap()
                        .load(kajian.getImgResource())
                        .into(binding.ImageJudul);
            }

            binding.getRoot().setOnClickListener(view -> {
                Intent kajianIntent = new Intent(binding.getRoot().getContext(), ShowKajianSQLActivity.class);
                kajianIntent.putExtra(ShowKajianSQLActivity.EXTRA_PARCEL_KAJIAN, kajian);
                binding.getRoot().getContext().startActivity(kajianIntent);
            });
        }
    }
}
