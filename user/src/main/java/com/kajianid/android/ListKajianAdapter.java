package com.kajianid.android;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.bumptech.glide.Glide;
import com.kajianid.android.data.Kajian;
import com.kajianid.android.databinding.ListKajianBinding;

import java.util.ArrayList;
import java.util.Objects;


public class ListKajianAdapter extends RecyclerView.Adapter<ListKajianAdapter.MyHolder>{

    public ListKajianAdapter(Context context) {
        this.context = context;
    }
    ArrayList<Kajian> mKajian = new ArrayList<>();
    private Context context;

    public void setKajian(ArrayList<Kajian> mKajian) {
        if (mKajian != null) this.mKajian.clear();
        assert mKajian != null;
        this.mKajian.addAll(mKajian);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListKajianBinding binding = ListKajianBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new MyHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyHolder holder, final int position) {
        holder.bind(mKajian.get(position));
    }

    @Override
    public int getItemCount() {
        return mKajian == null? 0 : mKajian.size();
    }

    static class MyHolder extends RecyclerView.ViewHolder{

        private ListKajianBinding binding;

        public MyHolder(@NonNull ListKajianBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(Kajian kajian) {
            if (Objects.equals(kajian.getPlace(), "Di Tempat")) {
                if (kajian.getImgResource()!= null)
                    Glide.with(binding.getRoot().getContext()).asBitmap().load(kajian.getImgResource()).into(binding.ImageJudul);
                else
                    Glide.with(binding.getRoot().getContext()).asBitmap().load(R.drawable.icon).into(binding.ImageJudul);
            } else {
                Glide.with(binding.getRoot().getContext()).asBitmap().load(kajian.getImgResource()).into(binding.ImageJudul);
            }

            binding.TextViewJudul.setText(kajian.getTitle());
            binding.TextViewUstadz.setText(kajian.getUstadzName());
            binding.TextViewMosque.setText((Objects.equals(kajian.getAddress(), "null")) ? kajian.getMosque() : kajian.getAddress());
            binding.TextViewKeterangan.setText(kajian.getPlace());
            binding.TextViewTanggal.setText(kajian.getDate());

            binding.getRoot().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent KajianIntent = new Intent(binding.getRoot().getContext(), ShowKajianActivity.class);
                    KajianIntent.putExtra(ShowKajianActivity.EXTRA_KAJIAN_ID, kajian.getId());
                    binding.getRoot().getContext().startActivity(KajianIntent);
                }
            });
        }
    }
}
