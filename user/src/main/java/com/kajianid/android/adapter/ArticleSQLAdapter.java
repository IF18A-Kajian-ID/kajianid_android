package com.kajianid.android.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kajianid.android.activities.ReadArticleSQLActivity;
import com.kajianid.android.data.Article;
import com.kajianid.android.databinding.ListArticleBinding;

import java.util.ArrayList;

public class ArticleSQLAdapter extends RecyclerView.Adapter<ArticleSQLAdapter.ArticleSQLViewHolder> {
    private final Context context;
    ArrayList<Article> mArticle = new ArrayList<>();

    public ArticleSQLAdapter(Context context) {
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
    public ArticleSQLAdapter.ArticleSQLViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListArticleBinding binding = ListArticleBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new ArticleSQLAdapter.ArticleSQLViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleSQLAdapter.ArticleSQLViewHolder holder, int position) {
        holder.bind(mArticle.get(position));
    }

    @Override
    public int getItemCount() {
        return mArticle == null ? 0 : mArticle.size();
    }

    static class ArticleSQLViewHolder extends RecyclerView.ViewHolder {

        private final ListArticleBinding binding;

        public ArticleSQLViewHolder(@NonNull ListArticleBinding itemView) {
            super(itemView.getRoot());
            this.binding = itemView;
        }

        public void bind(Article article) {
            binding.TVJudul.setText(article.getTitle());
            binding.TVRingkasan.setText(article.getContent());
            binding.TVTanggal.setText(article.getPostDate());
            binding.TVUstadz.setText(article.getUstadzName());

            if (article.getHasImg().equals("false")) {
                binding.IJudul.setVisibility(View.GONE);
            } else {
                Glide.with(binding.getRoot().getContext())
                        .asBitmap()
                        .load(article.getImgUrl())
                        .into(binding.IJudul);
            }

            binding.getRoot().setOnClickListener(view -> {
                Intent artikelIntent = new Intent(binding.getRoot().getContext(), ReadArticleSQLActivity.class);
                artikelIntent.putExtra(ReadArticleSQLActivity.EXTRA_PARCEL_ARTICLES, article.getId());
                binding.getRoot().getContext().startActivity(artikelIntent);
            });
        }
    }
}