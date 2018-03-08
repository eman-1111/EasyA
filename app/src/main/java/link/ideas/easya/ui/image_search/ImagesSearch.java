package link.ideas.easya.ui.image_search;

import android.app.Activity;
import android.app.SearchManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
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

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import link.ideas.easya.R;
import link.ideas.easya.models.Image;
import link.ideas.easya.ui.BaseActivity;
import link.ideas.easya.utils.InjectorUtils;


public class ImagesSearch extends BaseActivity {
    ImageAdapter mImageAdapter;

    static Bitmap imageB = null;

    String searchValue;

    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @BindView(R.id.lin_Progress)
    LinearLayout linlaHeaderProgress;

    @BindView(R.id.iv_no_result)
    ImageView ivNoResult;

    @BindView(R.id.image_gridview)
    GridView gridView;

    ImageSearchViewModel mViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_image_search);
        ButterKnife.bind(this);

        setDrawer(false);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageSearchFactory factory = InjectorUtils.provideSearchViewModelFactory(this);
        mViewModel = ViewModelProviders.of(this, factory).get(ImageSearchViewModel.class);

        mImageAdapter = new ImageAdapter(this, R.layout.list_item_search, new ArrayList<Image.Photos.photo>());
        gridView.setAdapter(mImageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                startDialog(mImageAdapter.getItem(position));
            }
        });


    }


    private void upDateSearch(String query) {

        if (query != null && query.length() > 1) {
            if (isDeviceOnline()) {

                linlaHeaderProgress.setVisibility(View.VISIBLE);
                mViewModel.getSearchQuery(query).observe(this, new Observer<Image>() {
                    @Override
                    public void onChanged(@Nullable Image image) {

                        Log.e("Nullable2",image.getPhotos().getTotal());
                        if ( image.getPhotos().getPhoto().size() > 0) {
                            linlaHeaderProgress.setVisibility(View.GONE);
                            mImageAdapter.clear();
                            mImageAdapter.addAll(image.getPhotos().getPhoto());
                            mImageAdapter.notifyDataSetChanged();
                            ivNoResult.setVisibility(View.GONE);
                        } else {
                            mImageAdapter.clear();
                            ivNoResult.setVisibility(View.VISIBLE);
                        }
                    }
                });
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



    protected void startDialog( final Image.Photos.photo image) {


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