package com.codepath.nytimessearch.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.Button;
import android.widget.EditText;

import com.codepath.nytimessearch.R;
import com.codepath.nytimessearch.adapters.ArticleAdapter;
import com.codepath.nytimessearch.models.Article;
import com.codepath.nytimessearch.models.Response;
import com.codepath.nytimessearch.utils.EndlessRecyclerViewScrollListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;

public class SearchActivity extends AppCompatActivity {
    EditText etQuery;
    Button btnSearch;
    ArrayList<Article> articles;
    ArticleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setUpViews();
    }

    private void setUpViews(){
        etQuery = (EditText) findViewById(R.id.etQuery);
        btnSearch = (Button) findViewById(R.id.btnSearch);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onArticleSearch(View view) {
        final String searchQuery = etQuery.getText().toString();

        // Lookup the recyclerview in activity layout
        RecyclerView rvArticles = (RecyclerView) findViewById(R.id.rvArticles);
        rvArticles.setItemAnimator(new SlideInUpAnimator(new OvershootInterpolator(1f)));

        // Get movies
        articles = new ArrayList<>();

        //search articles on page 0 initially
        getArticles(searchQuery, 0);
        // Create adapter passing in articles list
        adapter = new ArticleAdapter(this, articles);
        // Attach the adapter to the recyclerview to populate items
        rvArticles.setAdapter(adapter);
        // Set layout manager to position the items
        // First param is number of columns and second param is orientation i.e Vertical or Horizontal
        StaggeredGridLayoutManager staggeredGridLayoutManager =
                new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        // Attach the layout manager to the recycler view
        rvArticles.addOnScrollListener(new EndlessRecyclerViewScrollListener(staggeredGridLayoutManager) {

            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                Log.i("DEBUG","page: " + page );
                getArticles(searchQuery, page);
            }


        });

        rvArticles.setLayoutManager(staggeredGridLayoutManager);

        //Toast.makeText(this, "Searching for: " + searchQuery, Toast.LENGTH_LONG).show();


    }



    private void getArticles(final String searchQuery, final int page){

        //clear articles if it is a new search
        if(page == 0) {
            articles.clear();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "https://api.nytimes.com/svc/search/v2/articlesearch.json";
        RequestParams params = new RequestParams();
        params.put("api-key", "119d6da8a49945548737cfc94407e7a0");
        params.put("page", page);
        params.put("q", searchQuery);

        client.get(url, params, new TextHttpResponseHandler() {


            @Override
            public void onSuccess(int statusCode, Header[] headers, String responseString){

                Log.i("success", responseString);

                Gson gson = new GsonBuilder().create();
                Response response = gson.fromJson(responseString, Response.class);
/*                List<Article> articles1 = response.articlesResponse.articles;
                for (Article result : articles1) {

                    Log.i("INFO", "Headline: " + result.headline.mainHeadline + " weburl: " +result.webUrl);
                    if(result.thumbnails.size() > 0){
                        Log.i("INFO", result.thumbnails.get(0).getThumbnailUrl());
                    }
                }*/
                articles.addAll(response.articlesResponse.articles);
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.i("DEBUG error", responseString);
            }
    });

        /*//using async http request
        client.get(url, params, new JsonHttpResponseHandler(){
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                JSONArray articleJsonResults = null;
                try{
                    articleJsonResults = response.getJSONObject("response").getJSONArray("docs");
                    articles.addAll(Article.fromJSONArray(articleJsonResults));
                    Log.d("SearchActivity", articles.toString());
                    adapter.notifyDataSetChanged();

                }catch (JSONException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
            }
        });*/
    }
}
