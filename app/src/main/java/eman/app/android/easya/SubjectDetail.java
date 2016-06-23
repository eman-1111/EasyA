package eman.app.android.easya;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import eman.app.android.easya.data.CourseContract;


public class SubjectDetail extends AppCompatActivity {
    String lessonName;
    String lessonId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.setDuration(1000);
            getWindow().setExitTransition(fade);
            getWindow().setEnterTransition(fade);

        }
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        // Set title of Detail page

        if (savedInstanceState == null) {


            Bundle arguments = new Bundle();

            arguments.putParcelable(SubjectDetailFragment.DETAIL_URI, getIntent().getData());

            Uri mUri = arguments.getParcelable(SubjectDetailFragment.DETAIL_URI);
            lessonName = CourseContract.SubjectEntry.getSubjectTitleFromUri(mUri);
            lessonId = CourseContract.SubjectEntry.getSubjectIdFromUri(mUri);
            SubjectDetailFragment fragment = new SubjectDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_detail, fragment)
                    .commit();
        }




    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, SubjectList.class)
                    .setData(CourseContract.SubjectEntry.buildSubjectWithID(lessonId));
          //  intent.putExtra("CourseName", lessonName);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }


}
