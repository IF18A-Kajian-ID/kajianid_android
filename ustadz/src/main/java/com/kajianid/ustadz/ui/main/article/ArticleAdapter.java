package com.kajianid.ustadz.ui.main.article;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.kajianid.ustadz.data.Article;
import com.kajianid.ustadz.databinding.ItemArticlesBinding;
import com.kajianid.ustadz.ui.article.ReadArticleActivity;

import java.util.ArrayList;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ArticleViewHolder> {

    private final ArrayList<Article> mData = new ArrayList<>();

    public void setArticles(ArrayList<Article> articles) {
        mData.clear();
        mData.addAll(articles);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ArticleAdapter.ArticleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemArticlesBinding binding = ItemArticlesBinding.inflate(LayoutInflater.from(parent.getContext()));
        return new ArticleViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ArticleAdapter.ArticleViewHolder holder, int position) {
        holder.bind(mData.get(position));
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ArticleViewHolder extends RecyclerView.ViewHolder {

        private final ItemArticlesBinding binding;

        public ArticleViewHolder(@NonNull ItemArticlesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Article article) {
            String title = article.getTitle();
            String content = article.getContent();
            String postDate = article.getPost_date();
            String id = article.getId();

            binding.tvTitle.setText(title);
            binding.tvTextLess.setText(content);
            binding.tvPostDate.setText(postDate);

            if (!article.isHasImg()) {
                binding.imgArticle.setVisibility(View.GONE);
            } else {
                Glide.with(binding.getRoot().getContext())
                        .load(article.getImgUrl())
                        .into(binding.imgArticle);
            }

            binding.getRoot().setOnClickListener(itView -> {
                Intent i = new Intent(binding.getRoot().getContext(), ReadArticleActivity.class);
                i.putExtra(ReadArticleActivity.EXTRA_ARTICLE_ID, id);
                binding.getRoot().getContext().startActivity(i);
            });
        }
    }
}
