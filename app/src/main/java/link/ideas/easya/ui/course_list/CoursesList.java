package link.ideas.easya.ui.course_list;

import android.content.Intent;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.os.Bundle;
import android.view.MenuItem;

import link.ideas.easya.ui.lesson_list.LessonList;
import link.ideas.easya.R;
import link.ideas.easya.ui.BaseActivity;
import link.ideas.easya.utils.Constants;

//https://github.com/googlesamples/google-services/blob/master/android/signin/app/src/main/java/com/google/samples/quickstart/signin/SignInActivity.java#L68-L70
//https://firebase.google.com/docs/auth/android/google-signin
//https://github.com/firebase/quickstart-android/blob/master/auth/app/src/main/java/com/google/firebase/quickstart/auth/GoogleSignInActivity.java


public class CoursesList extends BaseActivity implements
        CourseListFragment.Callback {
    MenuItem addCourse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.courses_list);
        setDrawer(true);
        setUpAPIs();
        loadNavHeader(getResources().getString(R.string.your_course));
        setUpNavigationView();


    }

    @Override
    public void onItemSelected(int courseId, String courseName) {

        Intent intent = new Intent(this, LessonList.class);
        intent.putExtra(Constants.PREF_COURSE_NAME, courseName);
        intent.putExtra(Constants.PREF_COURSE_ID, courseId);
        ActivityOptionsCompat activityOptions =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this);
        ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }
}
