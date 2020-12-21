package com.kajianid.android.Adapter;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kajianid.android.ReadArticleActivity;
import com.kajianid.android.data.Article;
import com.kajianid.android.databinding.ListArticleBinding;

import java.util.ArrayList;
import java.util.Objects;

public class ListArticleAdapter extends RecyclerView.Adapter<ListArticleAdapter.Myarticle> {


    ArrayList<Article> mArticle = new ArrayList<>();
    private final Context context;
    public ListArticleAdapter(Context context) {
        this.context = context;
    }

    public void setArticle(ArrayList<Article> mArticle) {
        if (mArticle != null) this.mArticle.clear();
        assert mArticle != null;
        this.mArticle.addAll(mArticle);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public Myarticle onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListArticleBinding binding = ListArticleBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new Myarticle(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull final Myarticle binding, final int position) {
        binding.bind(mArticle.get(position));
    }

    @Override
    public int getItemCount() {
        return mArticle == null ? 0 : mArticle.size();
    }

    static class Myarticle extends RecyclerView.ViewHolder {

        private final ListArticleBinding binding;

        public Myarticle(@NonNull ListArticleBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;

        }

        public void bind(Article article) {
            if (Objects.equals(article.getHasImg(), "1")) {
                //jika gambar ada tampilkan
                Glide.with(binding.getRoot().getContext()).asBitmap().load(article.getImgUrl()).into(binding.IJudul);
            } else {
                //hilangkan tampilan gambar jika gambar tidak ada
                //untuk ngilangin gambar jika gambar tidak ada langsung tampil atribut dibawahnya
                Glide.with(binding.getRoot().getContext()).asBitmap().load(article.getImgUrl()).into(binding.IJudul);

            }
            binding.TVJudul.setText(article.getTitle());
            binding.TVRingkasan.setText(article.getContent());
            binding.TVTanggal.setText(article.getPostDate());
            binding.TVUstadz.setText(article.getUstadzName());
            binding.getRoot().setOnClickListener(view -> {
                Intent artikelIntent = new Intent(binding.getRoot().getContext(), ReadArticleActivity.class);
                artikelIntent.putExtra(ReadArticleActivity.EXTRA_ARTICLE_ID, article.getId());
                binding.getRoot().getContext().startActivity(artikelIntent);
            });
        }
    }
}
