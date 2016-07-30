package com.codepath.nytimessearch.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.codepath.nytimessearch.R;

/**
 * Created by santoshag on 7/22/16.
 */
public class ArticleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

    // Your holder should contain a member variable
    // for any view that will be set as you render a row
    private TextView tvTitle;

    public TextView getTvTitle() {
        return tvTitle;
    }

    public void setTvTitle(TextView tvTitle) {
        this.tvTitle = tvTitle;
    }


    // We also create a constructor that accepts the entire item row
    // and does the view lookups to find each subview
    public ArticleViewHolder(View itemView) {
        // Stores the itemView in a public final member variable that can be used
        // to access the context from any ViewHolder instance.

        super(itemView);
        tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        itemView.setOnClickListener(this);

    }

    // Handles the row being being clicked
    @Override
    public void onClick(View view) {
        int position = getLayoutPosition(); // gets item position
        // We can access the data within the views
    }

}
