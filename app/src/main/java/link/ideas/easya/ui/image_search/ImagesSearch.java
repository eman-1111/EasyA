package link.ideas.easya.ui.image_search;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import link.ideas.easya.BuildConfig;
import link.ideas.easya.R;
import link.ideas.easya.models.Image;
import link.ideas.easya.ui.BaseActivity;


public class ImagesSearch extends BaseActivity {
    ImageAdapter mImageAdapter;

    Bundle data;
    static Bitmap imageB = null;
    LinearLayout linlaHeaderProgress;
    ImageView ivNoResult;
    String searchValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_search);
        setDrawer(false);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        linlaHeaderProgress = (LinearLayout) findViewById(R.id.lin_Progress);
        ivNoResult = (ImageView) findViewById(R.id.iv_no_result);

        mImageAdapter = new ImageAdapter(this, R.layout.list_item_search, new ArrayList<Image>());
        GridView gridView = (GridView) findViewById(R.id.image_gridview);
        gridView.setAdapter(mImageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                startDialog(view, mImageAdapter.getItem(position));

            }
        });


    }


    private void upDateSearch(String query) {

        if (query != null) {
            if (isDeviceOnline()) {
                FetchImage fetchImage = new FetchImage();
                fetchImage.execute(query);
            } else {
                //TODO tell the user there is no internet connection
            }

        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        if (searchValue != null) {
            upDateSearch(searchValue);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                // Perform search here!
                Log.e("query", "query: " + query);
                upDateSearch(query);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ivNoResult.setVisibility(View.GONE);
                return true;
            }
        });


        return true;

    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.e("search", query);
            searchValue = query;
            upDateSearch(searchValue);
        }
    }

    public class FetchImage extends AsyncTask<String, Void, ArrayList<Image>> {
        public final String LOG_TAG = FetchImage.class.getSimpleName();

        @Override
        protected void onPostExecute(ArrayList<Image> result) {

            if (result != null) {
                linlaHeaderProgress.setVisibility(View.GONE);
                if (result.size() > 0) {
                    mImageAdapter.clear();
                    mImageAdapter.addAll(result);
                    mImageAdapter.notifyDataSetChanged();
                } else {
                    mImageAdapter.clear();
                    ivNoResult.setVisibility(View.VISIBLE);
                }
            }
        }

        @Override
        protected void onPreExecute() {
            // SHOW THE SPINNER WHILE LOADING FEEDS
            linlaHeaderProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected ArrayList<Image> doInBackground(String... params) {
            Log.d(LOG_TAG, "Starting sync");
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String imageJsonStr = null;


            String apiKey = BuildConfig.UNIQUE_FLICKR_KEY;;
            String pagesValue = "20";
            String formValue = "json";
            final String callBack = "1";
            ArrayList<Image> imageUrl = null;

            try {


                final String IMAGE_BASE_URL =
                        "https://api.flickr.com/services/rest/?method=flickr.photos.search";
                final String PAGES = "per_page";
                final String KEY_PARAM = "api_key";
                final String SEARCH = "tags";
                final String FORMAT = "format";
                final String JSON_CALL_BACK = "nojsoncallback";
//                final String API_SIG = "api_sig";
//                final String AUTH_TOKEN = "auth_token";

                //https://api.flickr.com/services/rest/?method=flickr.photos.search&
                // api_key=f5caac7e998b2510dbe40f47f8f3c469&tags=cat&per_page=10&format=json&nojsoncallback=1

                Uri builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .appendQueryParameter(SEARCH, params[0])
                        .appendQueryParameter(PAGES, pagesValue)
                        .appendQueryParameter(FORMAT, formValue)
                        .appendQueryParameter(JSON_CALL_BACK, callBack)
                        .build();

                URL url = new URL(builtUri.toString());
                Log.e("URL", builtUri.toString());

                // Create the request to themoviedb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    // return;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {

                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.

                }

                imageJsonStr = buffer.toString();
                imageUrl = getImageDataFromJson(imageJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attempting
                // to parse it.
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }

            return imageUrl;
        }

        private ArrayList<Image> getImageDataFromJson(String imageJsonStr)
                throws JSONException {

            final String OWM_RESULT = "photos";

            final String OWM_PHOTO = "photo";
            final String OWM_ID = "id";
            final String OWM_SECRET = "secret";
            final String OWM_SERVER = "server";
            final String OWM_FARM = "farm";


            try {


                JSONObject imagesJson = new JSONObject(imageJsonStr);
                JSONObject photosJson = imagesJson.getJSONObject(OWM_RESULT);
                JSONArray imageArray = photosJson.getJSONArray(OWM_PHOTO);
                ArrayList<Image> images = new ArrayList<Image>();
                for (int i = 0; i < imageArray.length(); i++) {

                    String id;
                    String secret;
                    String server;
                    String farm;


                    JSONObject fullImage = imageArray.getJSONObject(i);

                    id = fullImage.getString(OWM_ID);
                    secret = fullImage.getString(OWM_SECRET);
                    server = fullImage.getString(OWM_SERVER);
                    farm = fullImage.getString(OWM_FARM);

                    Image image = new Image(id, secret, server, farm);

                    images.add(image);
                }
                return images;
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

            return null;

        }
    }


    protected void startDialog(final View view, final Image image) {


        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.add_image, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                this);

        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final ImageView imageIV = (ImageView) promptsView
                .findViewById(R.id.icon_img);


        String url = "https://farm" + image.getFarm() + ".staticflickr.com/" + image.getServer() +
                "/" + image.getId() + "_" + image.getSecret() + ".jpg";
        Log.e("imageUrl ", url);

        Glide.with(this)
                .load(url)
                .asBitmap()
                .into(new SimpleTarget<Bitmap>(100, 100) {
                    @Override
                    public void onResourceReady(Bitmap resource, GlideAnimation glideAnimation) {
                        imageB = resource;
                        imageIV.setImageBitmap(imageB);

                    }
                });
        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //TODO MAKE IT HD
                                if (imageB != null) {
                                    startImageIIntent(imageB);
                                    dialog.cancel();
                                }


                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                imageB = null;
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


    }

    public void startImageIIntent(Bitmap imageB) {
        Intent intent = new Intent();
        intent.putExtra("data", imageB);
        setResult(Activity.RESULT_OK, intent);
        finish();

    }
}