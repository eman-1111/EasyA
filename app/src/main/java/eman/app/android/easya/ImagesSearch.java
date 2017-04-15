package eman.app.android.easya;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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

import eman.app.android.easya.adapter.ImageAdapter;


public class ImagesSearch extends AppCompatActivity {
    ImageAdapter mImageAdapter;
    String searchValue;
    Bundle data;
    Bitmap imageB;
    LinearLayout linlaHeaderProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_search);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent intent = getIntent();
        data = intent.getExtras();
        searchValue = intent.getStringExtra("SearchValue");
        linlaHeaderProgress = (LinearLayout) findViewById(R.id.linlaHeaderProgress);
        mImageAdapter = new ImageAdapter(this,R.layout.list_item_search, new ArrayList<Image>());
        GridView gridView = (GridView) findViewById(R.id.image_gridview);
        gridView.setAdapter(mImageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                startDialog( view,  mImageAdapter.getItem(position));

            }
        });



    }



    private void upDateSearch(String query) {

        if(query != null ){
            if(isDeviceOnline()){
                FetchImage fetchImage = new FetchImage();
                fetchImage.execute(query);
            }else{
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
        if(searchValue != null){
            upDateSearch(searchValue);
        }
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {

                // Perform search here!
                Log.e("query","query: "+ query);
                upDateSearch(query);


                // Clear the text in search bar but (don't trigger a new search!)
                //  searchView.setQuery("", false);

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
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
                mImageAdapter.clear();
                mImageAdapter.addAll(result);
                mImageAdapter.notifyDataSetChanged();
                linlaHeaderProgress.setVisibility(View.GONE);
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


            String apiKey = BuildConfig.UNIQUE_PIXABAY_KEY;
            String high_resolution = "high_resolution";
            String safeSearch = "true";
            ArrayList<Image> imageUrl = null;

            try {
                // Construct the URL for the api.themoviedb.org query

                final String IMAGE_BASE_URL =
                        "https://pixabay.com/api/?";
                final String RESPONSE_PARAM = "response_group";
                final String KEY_PARAM = "key";
                final String SEARCH = "q";
                final String SAFE_SEARCH = "safesearch";

                //https://pixabay.com/api/?key=[KEY]&response_group=high_resolution&q=yellow+flower&pretty=true
                Uri builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                        .appendQueryParameter(KEY_PARAM, apiKey)
                        .appendQueryParameter(RESPONSE_PARAM, high_resolution)
                        .appendQueryParameter(SEARCH, params[0])
                        .appendQueryParameter(SAFE_SEARCH, safeSearch)
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
            String[] imageNor = new String[imageUrl.size()];
            int i = 0;
            for (Image image : imageUrl) {

                imageNor[i] = image.getImage();
                i++;
            }

            return imageUrl;
        }

        private ArrayList<Image> getImageDataFromJson(String imageJsonStr)
                throws JSONException {

            final String OWM_RESULT = "hits";

            final String OWM_HD_IMAGE = "fullHDURL";
            final String OWM_IMAGE = "previewURL";
            final String OWM_HASH = "id_hash";


            try {


                JSONObject imagesJson = new JSONObject(imageJsonStr);
                JSONArray imageArray = imagesJson.getJSONArray(OWM_RESULT);
                ArrayList<Image> images = new ArrayList<Image>();
                for (int i = 0; i < imageArray.length(); i++) {

                    String hdImage;
                    String imageNor;
                    String hashId;


                    JSONObject fullMovie = imageArray.getJSONObject(i);
                    hdImage = fullMovie.getString(OWM_HD_IMAGE);
                    imageNor = fullMovie.getString(OWM_IMAGE);
                    hashId = fullMovie.getString(OWM_HASH);

                    Image image = new Image(hashId, imageNor, hdImage);
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
    /**
     * Checks whether the device currently has a network connection.
     *
     * @return true if the device has a network connection, false otherwise.
     */
    private boolean isDeviceOnline() {
        ConnectivityManager connMgr =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
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
        Picasso.with(this).load(image.getImage()).error(R.drawable.blue)
                .into(imageIV);

        Picasso.with(this).load(image.getImage())
                .into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        imageIV.setImageBitmap(bitmap);
                        imageB = bitmap;
                    }

                    @Override
                    public void onBitmapFailed(Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                });

        // set dialog message
        alertDialogBuilder
                .setCancelable(false)
                .setPositiveButton("Save",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                //TODO MAKE IT HD
                                if(imageB == null){
                                    Picasso.with(ImagesSearch.this).load(image.getImage()).into(target);
                                    startImageIIntent(imageB);
                                }else{
                                    startImageIIntent(imageB);
                                }


                            }
                        })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        });

        // create alert dialog
        AlertDialog alertDialog = alertDialogBuilder.create();

        // show it
        alertDialog.show();


    }

    private Target target = new Target() {
        @Override
        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
            imageB = bitmap;
        }

        @Override
        public void onBitmapFailed(Drawable errorDrawable) {
        }

        @Override
        public void onPrepareLoad(Drawable placeHolderDrawable) {
        }
    };
    public void startImageIIntent(Bitmap imageB) {
        Intent intent=new Intent();
        intent.putExtra("data",imageB);
        setResult(Activity.RESULT_OK,intent);
        finish();

    }
}