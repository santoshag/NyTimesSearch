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
import com.codepath.nytimessearch.models.ArticlesResponse;
import com.codepath.nytimessearch.models.Response;
import com.codepath.nytimessearch.utils.ArticlesService;
import com.codepath.nytimessearch.utils.EndlessRecyclerViewScrollListener;

import java.io.IOException;
import java.util.ArrayList;

import jp.wasabeef.recyclerview.animators.SlideInUpAnimator;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SearchActivity extends AppCompatActivity {
    EditText etQuery;
    Button btnSearch;
    ArrayList<Article> articles;
    ArticleAdapter adapter;
    final static String BASE_URL = "https://api.nytimes.com";

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
                if(page < ArticlesResponse.MAX_PAGES_TO_RETRIEVE) {
                    getArticles(searchQuery, page);
                }
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

        final String apiKey = "119d6da8a49945548737cfc94407e7a0";

        //Use retrofit networking library to make API calls
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        /*
            //Add below interceptor to enable debugging logs
            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            // Can be Level.BASIC, Level.HEADERS, or Level.BODY
            // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.networkInterceptors().add(httpLoggingInterceptor);

        */
        builder.interceptors().add(new Interceptor() {
                @Override
                public okhttp3.Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();
                    HttpUrl originalHttpUrl = original.url();

                    HttpUrl url = originalHttpUrl.newBuilder()
                            .addQueryParameter("api-key", apiKey)
                            .build();

                    // Request customization: add request headers
                    Request.Builder requestBuilder = original.newBuilder()
                            .url(url);

                    Request request = requestBuilder.build();
                    return chain.proceed(request);

                }
            });


            OkHttpClient httpClient = builder.build();


            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(httpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            ArticlesService articlesService = retrofit.create(ArticlesService.class);
            Call<Response> call = articlesService.listArticles(searchQuery, page);
            call.enqueue(new Callback<Response>() {

                @Override
                public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
                    int statusCode = response.code();
                    Response user = response.body();
                    ArrayList<Article> articles2 = new ArrayList<Article>();
                    articles.addAll(user.articlesResponse.articles);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onFailure(Call<Response> call, Throwable t) {
                    Log.i("DEBUG", " Call to network failed! ");
                }
            });



    }
}
