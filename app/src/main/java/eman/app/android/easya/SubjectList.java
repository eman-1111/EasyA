package eman.app.android.easya;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import eman.app.android.easya.data.CourseContract;
import eman.app.android.easya.fragment.SubjectDetailFragment;
import eman.app.android.easya.fragment.SubjectListFragment;


public class SubjectList extends AppCompatActivity implements SubjectListFragment.Callback {
    static Uri mUri;
    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_list);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Slide slide = new Slide();
            slide.setSlideEdge(Gravity.BOTTOM);
            slide.setDuration(1000);

            getWindow().setEnterTransition(slide);
            Fade fade = new Fade();
            slide.setDuration(1000);
            getWindow().setExitTransition(fade);

        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //ImageSyncAdapter.initializeSyncAdapter(this);

        if (savedInstanceState == null) {

            Bundle arguments = new Bundle();
            mUri = getIntent().getData();


            if(mUri == null){
                Log.e("SubjectList","no uri");
            }
//            if( getIntent().getStringExtra("CourseName").trim().isEmpty() == false){
//                getSupportActionBar().setTitle(getIntent().getStringExtra("CourseName"));
//            }

            arguments.putParcelable(SubjectListFragment.SUBJECT_URI, getIntent().getData());
            SubjectListFragment fragment = new SubjectListFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.subject_list_fragment, fragment)
                    .commit();

        }
        if (findViewById(R.id.subject_detail_container) != null) {
            // The detail container view will be present only in the large-screen layouts
            // (res/layout-sw600dp). If this view is present, then the activity should be
            // in two-pane mode.
            Log.e("subjectlist","Tab");
            mTwoPane = true;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.subject_detail_container, new SubjectDetailFragment(), DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Log.e("subjectlist","phon");
            mTwoPane = false;
        }
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startIntent();
            }
        });


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subject_list, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(Uri contentUri) {
        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(SubjectDetailFragment.DETAIL_URI, contentUri);

            SubjectDetailFragment fragment = new SubjectDetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.subject_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, SubjectDetail.class)
                    .setData(contentUri);
            startActivity(intent);
        }


    }

    public void startIntent() {
        //Intent intent = new Intent(this, AddSubjectTitel.class);
        Intent intent = new Intent(this, AddNewLesson.class);
        intent.putExtra("CourseId", CourseContract.SubjectEntry.getSubjectIdFromUri(mUri));
        startActivity(intent);
    }


}
