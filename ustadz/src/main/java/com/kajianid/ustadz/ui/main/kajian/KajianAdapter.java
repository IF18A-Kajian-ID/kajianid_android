package com.kajianid.ustadz.ui.main.kajian;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kajianid.ustadz.R;
import com.kajianid.ustadz.data.Kajian;
import com.kajianid.ustadz.databinding.ItemKajianBinding;
import com.kajianid.ustadz.ui.kajian.ShowKajianActivity;
import com.kajianid.ustadz.utils.StringHelper;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class KajianAdapter extends RecyclerView.Adapter<KajianAdapter.KajianViewHolder> {

    private ArrayList<Kajian> data = new ArrayList<>();

    @NonNull
    @Override
    public KajianViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemKajianBinding binding = ItemKajianBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new KajianViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull KajianViewHolder holder, int position) {
        holder.bind(data.get(position));
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public void setData(ArrayList<Kajian> kajian) {
        this.data.clear();
        this.data.addAll(kajian);
        notifyDataSetChanged();
    }

    public static class KajianViewHolder extends RecyclerView.ViewHolder {

        private ItemKajianBinding binding;

        public KajianViewHolder(@NonNull ItemKajianBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Kajian kajian) {
            String mosqueOrAddress = (kajian.getPlace().equals("Di Tempat")) ? kajian.getAddress() : kajian.getMosqueName();
            binding.tvTitle.setText(kajian.getTitle());
            binding.tvKet.setText(kajian.getPlace());
            binding.tvDue.setText(kajian.getDate());
            binding.tvMosqueAddress.setText(mosqueOrAddress);

            if (!StringHelper.isNullOrEmpty(kajian.getImgResource())) {
                Glide.with(binding.getRoot().getContext())
                        .load(kajian.getImgResource())
                        .into(binding.imgKajian);
            } else {
                Glide.with(binding.getRoot().getContext())
                        .load(R.drawable.intro3)
                        .into(binding.imgKajian);
            }

            binding.getRoot().setOnClickListener(it -> {
                Intent i = new Intent(binding.getRoot().getContext(), ShowKajianActivity.class);
                i.putExtra(ShowKajianActivity.EXTRA_KAJIAN_ID, kajian.getId());
                binding.getRoot().getContext().startActivity(i);
            });
        }
    }
}
