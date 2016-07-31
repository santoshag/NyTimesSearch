package com.codepath.nytimessearch.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.models.Article;

import java.util.List;

/**
 * Created by santoshag on 7/19/16.
 */
public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "ArticleArrayAdapter";
    // Store a member variable for the movies
    private List<Article> mArticles;

    private Context mContext;

    private final int ARTICLE = 0, ARTICLE_WITH_THUMBNAIL = 1;

    public Context getContext() {
        return mContext;
    }

    // Pass in the movie list into the constructor
    public ArticleAdapter(Context context, List<Article> movies){
        mContext = context;
        mArticles = movies;
    }

    @Override
    public int getItemViewType(int position) {
        if (mArticles.get(position).doesArticleHaveThumbnail()) {
            return ARTICLE_WITH_THUMBNAIL;
        } else {
            return ARTICLE;
        }
    }


    /**
     * This method creates different RecyclerView.ViewHolder objects based on the item view type.\
     *
     * @param viewGroup ViewGroup container for the item
     * @param viewType type of view to be inflated
     * @return viewHolder to be inflated
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        RecyclerView.ViewHolder viewHolder;
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());

        switch (viewType) {
            case ARTICLE:
                View v1 = inflater.inflate(R.layout.article, viewGroup, false);
                viewHolder = new ArticleViewHolder(v1);
                break;
            case ARTICLE_WITH_THUMBNAIL:
                View v2 = inflater.inflate(R.layout.article_thumbnail, viewGroup, false);
                viewHolder = new ArticleThumbnailViewHolder(v2);
                break;
            default:
                View v = inflater.inflate(R.layout.article, viewGroup, false);
                viewHolder = new ArticleViewHolder(v);
                break;
        }
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        switch (viewHolder.getItemViewType()) {
            case ARTICLE:
                ArticleViewHolder vh1 = (ArticleViewHolder) viewHolder;
                configureArticleViewHolder(vh1, position);
                break;
            case ARTICLE_WITH_THUMBNAIL:
                ArticleThumbnailViewHolder vh2 = (ArticleThumbnailViewHolder) viewHolder;
                configurerArticleThumbnailViewHolder(vh2, position);
                break;
            default:
                ArticleViewHolder v = (ArticleViewHolder) viewHolder;
                configureArticleViewHolder(v, position);
                break;
        }
    }


    private void configureArticleViewHolder(ArticleViewHolder viewHolder, int position) {
        final Article article = (Article) mArticles.get(position);
            // Set item views based on your views and data model
        TextView tvTitle = viewHolder.getTvTitle();
        tvTitle.setText(article.headline.mainHeadline);
    }

    private void configurerArticleThumbnailViewHolder(ArticleThumbnailViewHolder viewHolder, int position) {
        Article article = (Article) mArticles.get(position);

        if(article != null) {
            // Set item views based on your views and data model
            TextView tvTitle = viewHolder.getTvTitle();
            tvTitle.setText(article.headline.mainHeadline);
            ImageView ivThumbnail = viewHolder.getIvThumbnail();
            Glide.with(getContext()).load(article.thumbnails.get(0).getThumbnailUrl()).into(ivThumbnail);
        }
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mArticles.size();
    }
}