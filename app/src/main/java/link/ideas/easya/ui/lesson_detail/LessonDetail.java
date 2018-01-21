package link.ideas.easya.ui.lesson_detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import link.ideas.easya.R;
import link.ideas.easya.ui.BaseActivity;
import link.ideas.easya.ui.lesson_list.LessonList;
import link.ideas.easya.utils.Constants;


public class LessonDetail extends BaseActivity {
    String lessonName;
    int lessonId;

    private static final String LOG_TAG = LessonDetail.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail);
        setDrawer(false);


        if (savedInstanceState == null) {


            Bundle arguments = new Bundle();
            Intent intent = getIntent();
            lessonName = intent.getStringExtra(Constants.PREF_LESSON_NAME);
            lessonId = intent.getIntExtra(Constants.PREF_LESSON_ID,0);

            arguments.putString(Constants.PREF_LESSON_NAME, lessonName);
            arguments.putInt(Constants.PREF_LESSON_ID, lessonId);
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
            Intent intent = new Intent(this, LessonList.class);
            intent.putExtra("CourseName", lessonName);
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


}
