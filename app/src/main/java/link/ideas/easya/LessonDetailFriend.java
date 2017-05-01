package link.ideas.easya;

import android.content.Intent;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import link.ideas.easya.models.Lesson;
import link.ideas.easya.models.LessonDetail;
import link.ideas.easya.utils.Constants;

public class LessonDetailFriend extends BaseActivity {

    TextView mLessonLink, mLessonDebug, mLessonPracticalTitle, mLessonPractical,
            mLessonOutline, mLink, mDebug;

    CollapsingToolbarLayout collapsingToolbar;
    ImageView outlineImage, linkImage, appImage;
    LinearLayout progress;

    ValueEventListener mValueEventLessonDetailListener;
    FirebaseDatabase mFirebaseDatabase;
    DatabaseReference mLessonDatabaseReference;

    String coursePushId, lessonPushId;

    LessonDetail lessonDetail;
    Lesson lesson;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_detail_friend);
        setDrawer(false);

        Intent intent = getIntent();
        coursePushId = intent.getStringExtra(Constants.PREF_COURSE_PUSH_ID);
        lessonPushId  = intent.getStringExtra(Constants.PREF_LESSON_PUSH_ID);
        lesson= (Lesson) intent.getParcelableExtra(Constants.PREF_LESSON_OBJECT);
        Log.e("data",coursePushId +" "+lessonPushId +" "+ lesson.getLessonName());
        initializeScreen();

    }

    private void initializeScreen() {
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        mLessonLink = (TextView) findViewById(R.id.lesson_linkx_content_tv);
        mLink = (TextView) findViewById(R.id.lesson_linkx_d_tv);
        mLessonPracticalTitle = (TextView) findViewById(R.id.lesson_app_title_d_tv);
        mLessonPractical = (TextView) findViewById(R.id.lesson_app_content_tv);
        mLessonDebug = (TextView) findViewById(R.id.lesson_debug_content_tv);
        mDebug = (TextView) findViewById(R.id.lesson_debugx_title_tv);
        mLessonOutline = (TextView) findViewById(R.id.lesson_overview_content_tv);

        linkImage = (ImageView) findViewById(R.id.link_iv);
        appImage = (ImageView) findViewById(R.id.app_iv);
        outlineImage = (ImageView) findViewById(R.id.outlook_iv);

        progress = (LinearLayout) findViewById(R.id.lin_Progress);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mLessonDatabaseReference = mFirebaseDatabase.getReference().
                child(Constants.FIREBASE_LOCATION_USERS_LESSONS_DETAIL).child(coursePushId).child(lessonPushId);

        attachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {

        progress.setVisibility(View.VISIBLE);
        mValueEventLessonDetailListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                lessonDetail = dataSnapshot.getValue(LessonDetail.class);

                progress.setVisibility(View.GONE);
                setUpView();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("DatabaseError", databaseError + "");

            }
        };
        mLessonDatabaseReference.addValueEventListener(mValueEventLessonDetailListener);
    }
    private void setUpView() {
//        mLessonLink, mLessonDebug, mLessonPracticalTitle, mLessonPractical,
//                mLessonOutline, mLink, mDebug;
        String lessonName = lesson.getLessonName();
        collapsingToolbar.setTitle(lessonName);
        mLessonLink.setText(lesson.getLessonLink());
        mLessonDebug.setText(lessonDetail.getLessonDebug());
        mLessonPracticalTitle.setText(lessonDetail.getLessonAppTitle());
        mLessonPractical.setText(lessonDetail.getLessonAppDescription());
        mLessonOutline.setText(lessonDetail.getLessonSummery());

        String outlineImageUrl = lesson.getLessonImage();
        if (outlineImageUrl.equals("")) {
            Glide.with(LessonDetailFriend.this).load(outlineImageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(outlineImage);
        }
        String linkImageUrl = lessonDetail.getLinkImage();
        if (linkImageUrl.equals("")) {
            Glide.with(LessonDetailFriend.this).load(linkImageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(linkImage);
        }
        String appImageUrl = lessonDetail.getAppImage();
        if (appImageUrl.equals("")) {
            Glide.with(LessonDetailFriend.this).load(appImageUrl)
                    .placeholder(R.drawable.placeholder)
                    .into(appImage);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.home) {
            Intent homeIntent = new Intent(LessonDetailFriend.this, LessonListFriends.class);
            homeIntent.putExtra(Constants.PREF_COURSE_PUSH_ID ,coursePushId);
            startActivity(homeIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        detachDatabaseReadListener();

    }

    private void detachDatabaseReadListener() {
        if (mValueEventLessonDetailListener != null) {
            mLessonDatabaseReference.removeEventListener(mValueEventLessonDetailListener);
            mValueEventLessonDetailListener = null;

        }
    }
}
