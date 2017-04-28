package link.ideas.easya;

import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import link.ideas.easya.fragment.CourseListFragment;

//https://github.com/googlesamples/google-services/blob/master/android/signin/app/src/main/java/com/google/samples/quickstart/signin/SignInActivity.java#L68-L70
//https://firebase.google.com/docs/auth/android/google-signin
//https://github.com/firebase/quickstart-android/blob/master/auth/app/src/main/java/com/google/firebase/quickstart/auth/GoogleSignInActivity.java


public class CoursesList extends BaseActivity implements
        CourseListFragment.Callback  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.courses_list);
        setDrawer(true);
        setUpAPIs();
        loadNavHeader(getResources().getString(R.string.your_course) );
        setUpNavigationView();


    }
    @Override
    public void onItemSelected(Uri contentUri, String courseName) {

        Intent intent = new Intent(this, LessonList.class)
                .setData(contentUri);
        intent.putExtra("CourseName", courseName);
        ActivityOptionsCompat activityOptions =
                ActivityOptionsCompat.makeSceneTransitionAnimation(this);
        ActivityCompat.startActivity(this, intent, activityOptions.toBundle());
        this.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_add, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.btn_add_menu) {
            startDialog();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }

}
