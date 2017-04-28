package link.ideas.easya;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import link.ideas.easya.data.CourseContract;
import link.ideas.easya.fragment.LessonDetailFragment;


public class LessonDetail extends BaseActivity {
    String lessonName;
    String lessonId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail);
        setDrawer(false);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (savedInstanceState == null) {


            Bundle arguments = new Bundle();

            arguments.putParcelable(LessonDetailFragment.DETAIL_URI, getIntent().getData());

            Uri mUri = arguments.getParcelable(LessonDetailFragment.DETAIL_URI);
            lessonName = CourseContract.SubjectEntry.getSubjectTitleFromUri(mUri);
            lessonId = CourseContract.SubjectEntry.getSubjectIdFromUri(mUri);
            LessonDetailFragment fragment = new LessonDetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_detail, fragment)
                    .commit();
        }




    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, LessonList.class)
                    .setData(CourseContract.SubjectEntry.buildSubjectWithID(lessonId));
            intent.putExtra("CourseName", lessonName);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
