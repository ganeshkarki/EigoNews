package com.paradox.eigonews;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.NetworkImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    MyAdapter mAdapter;
    VerticalViewPager mPager;
    String url = "https://newsapi.org/v2/top-headlines?country=jp&apiKey=" + BuildConfig.GOOGLE_NEWS_API_KEY;

    private int newsCount = 1;
    private static final String TAG = "MainActivity";
    private JSONArray articleJson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializNewsDataFromApi();
        mAdapter = new MyAdapter(getSupportFragmentManager(), this.newsCount, this.articleJson);
        mPager = findViewById(R.id.viewpager);
        mPager.setAdapter(mAdapter);
    }

    protected void initializNewsDataFromApi() {
        Log.d(TAG, "GOOGLE API KEY:" + BuildConfig.GOOGLE_NEWS_API_KEY ); // TODO: Remove LOG

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                try {
                    newsCount = response.getInt("totalResults");
                    articleJson = response.getJSONArray("articles");

                    mAdapter.update(newsCount, articleJson);
                    Toast.makeText(MainActivity.this, "News fetched", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "news count fetched: " + newsCount + articleJson.toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, "Something went wrong " + e.getMessage());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                // TODO: Handle error
                Toast.makeText(MainActivity.this, "Something went wrong", Toast.LENGTH_LONG).show();
                Log.e(TAG, url);
                Log.e(TAG, error.getMessage());
            }
        });

        // Access the RequestQueue through your singleton class.
        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }


    public static class MyAdapter extends SmartFragmentStatePagerAdapter {
        Integer newsCount;
        JSONArray articleJson;

        MyAdapter(FragmentManager fm, int articleCount, JSONArray articleJson) {
            super(fm);
            this.newsCount = articleCount;
            this.articleJson = articleJson;
        }

        @Override
        public int getCount() {
            Log.d(TAG, "getCount: " + this.newsCount );
            return this.newsCount;
        }

        @Override
        public Fragment getItem(int position) {
            String url = "";
            String title = "";
            JSONObject article;
            NewsCard card = null;


            if (this.articleJson != null) {
                try {
                    card = new NewsCard(articleJson.getJSONObject(position));
                    return NewsCardFragment.newInstance(card);
                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e(TAG, e.getMessage());
                }
            }

            return new LoadingCardFragment();
//            if (position <= getCount() && articleJson != null) {
//                return NewsCardFragment.newInstance("NEWS 0", Color.WHITE);
//            }

//            Log.e(TAG, "Trying to get wrong position: " + position);
//            return null;


//            switch (position) {
//                case 0:
//                    return NewsCardFragment.newInstance("NEWS 0", Color.WHITE);
//                case 1:
//                    // return a different Fragment class here
//                    // if you want want a completely different layout
//                    return NewsCardFragment.newInstance("NEWS 1", Color.CYAN);
//                default:
//                    return null;
//            }
        }

        void update(Integer newsCount, JSONArray articleJson) {
            this.articleJson = articleJson;
            this.newsCount = newsCount;
            notifyDataSetChanged();
        }

        public int getItemPosition(@NonNull Object item) {
//            TODO: Don't allow to swipe once reached end of Cards.
//            NewsCardFragment fragment = (NewsCardFragment) item;
//            String title = fragment.getTitle();
//            int position = titles.indexOf(title);
//
//            if (position >= 0) {
//                return position;
//            } else {
                return POSITION_NONE;
//            }
        }
    }

    public static class NewsCardFragment extends Fragment {
        private static final String ARTICLE_TITLE = "title";
        private static final String ARTICLE_DESCRIPTION = "description";
        private static final String ARTICLE_URL = "url";
        private static final String ARTICLE_IMG_URL = "urlToImage";
        private static final String ARTICLE_SOURCE = "sourceName";

        private String articleTitle, articleDescription, articleSource, articleImgUrl;

        // You can modify the parameters to pass in whatever you want
        static NewsCardFragment newInstance(NewsCard article) {
            NewsCardFragment f = new NewsCardFragment();
            Bundle args = new Bundle();
            args.putString(ARTICLE_SOURCE, article.getSourceName());
            args.putString(ARTICLE_TITLE, article.getTitle());
            args.putString(ARTICLE_DESCRIPTION, article.getDescription());
            args.putString(ARTICLE_URL, article.getUrl());
            args.putString(ARTICLE_IMG_URL, article.getImgUrl());
            f.setArguments(args);
            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            articleTitle = getArguments() != null
                    ? getArguments().getString(ARTICLE_TITLE)
                    : "Sorry something went wrong";
            articleDescription = getArguments() != null
                    ? getArguments().getString(ARTICLE_DESCRIPTION)
                    : "...";
            articleSource = getArguments() != null
                    ? "Source: " + getArguments().getString(ARTICLE_SOURCE)
                    : "...";
            articleImgUrl = getArguments() != null
                    ? getArguments().getString(ARTICLE_IMG_URL)
                    : "...";
        }

        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View v = inflater.inflate(R.layout.fragment_news_card, container, false);
            NetworkImageView imgView = v.findViewById(R.id.imageView);
            TextView titleText = v.findViewById(R.id.articleTitleText);
            TextView descriptionText = v.findViewById(R.id.articleContentText);
            TextView sourceText = v.findViewById(R.id.sourceCreditText);

            imgView.setImageUrl(articleImgUrl, MySingleton.getInstance(getActivity()).getImageLoader());
            titleText.setText(articleTitle);
            descriptionText.setText(articleDescription);
            sourceText.setText(articleSource);
            return v;
        }
    }

    public static class LoadingCardFragment extends Fragment {

        public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_loading_card, container, false);
        }
    }
}
