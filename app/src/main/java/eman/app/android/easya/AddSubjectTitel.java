package eman.app.android.easya;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.transition.Fade;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

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

import eman.app.android.easya.data.CourseContract;


public class AddSubjectTitel extends AppCompatActivity {

    private static final String LOG_TAG = AddSubjectTitel.class.getSimpleName();
    EditText lessonNameET, lessonOverViewET;
    ImageView outlineImage;
    TextInputLayout inputLayoutName, inputLayoutOutline;

    String lessonId, lessonOverView, lessonName, lessonNameOld;
    String imageUrl;
    // Bundle data;
    Bundle dataB;
    boolean back = false, edit = false;
    private Uri mUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_subject_titel);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.setDuration(1000);
            getWindow().setExitTransition(fade);
            getWindow().setEnterTransition(fade);

        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setUpID();
        Intent intent = getIntent();

        if (getIntent().getExtras() != null) {
            //dataB = getIntent().getExtras();
            getSavedInstanceState( getIntent().getExtras());
        }
        if (intent.getStringExtra("SavedStatue") != null) {
            Log.e(LOG_TAG, "back == true");
            back = true;

        }
        lessonId = intent.getStringExtra("CourseId");

        if (intent.getStringExtra("LessonURL") != null) {
            edit = true;
            Log.e(LOG_TAG, "edit true");
            mUri = Uri.parse(intent.getStringExtra("LessonURL"));
            lessonOverView = intent.getStringExtra("lessonOverView");
            lessonName = intent.getStringExtra("lessonName");
            lessonNameET.setText(lessonName);
            lessonOverViewET.setText(lessonOverView);
        }
        if (intent.getStringExtra("outlineImage") != null) {
            imageUrl = intent.getStringExtra("outlineImage");
        } else {
            imageUrl = "l";

        }
        Picasso.with(this).load(imageUrl).error(R.drawable.air_plan)
                .into(outlineImage);
        lessonNameET.addTextChangedListener(new MyTextWatcher(lessonNameET));
        lessonOverViewET.addTextChangedListener(new MyTextWatcher(lessonOverViewET));

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.next_btn);
        assert fab != null;
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lessonName = lessonNameET.getText().toString();
                lessonOverView = lessonOverViewET.getText().toString();

                Log.e(LOG_TAG, lessonName + lessonOverView);
                startDetailIntent();
            }

        });

        outlineImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startImageIntent();
            }

        });


    }

    private void setUpID() {
        inputLayoutName = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputLayoutOutline = (TextInputLayout) findViewById(R.id.input_layout_outline);

        lessonNameET = (EditText) findViewById(R.id.lesson_name_et);
        lessonOverViewET = (EditText) findViewById(R.id.lesson_outline_et);
        outlineImage = (ImageView) findViewById(R.id.outline_image);

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, SubjectList.class)
                    .setData(CourseContract.SubjectEntry.buildSubjectWithID(lessonId));
            startActivity(intent);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void startDetailIntent() {

        if (!validateName()) {
            return;
        }

        if (!validateOutline()) {
            return;
        }
        Intent intent = new Intent(this, AddLessonDetail.class);

        if (edit) {
            intent.putExtra("LessonURL", mUri.toString());

        }
        intent.putExtra("CourseId", lessonId);
        intent.putExtra("LessonName", lessonName);
        intent.putExtra("LessonOverview", lessonOverView);
        intent.putExtra("ImageUrl", imageUrl);
        Log.e(LOG_TAG, lessonName + lessonOverView);
        setSavedInstanceState();
        startActivity(intent);
    }

    public void startImageIntent() {
        Intent intent = new Intent(AddSubjectTitel.this, ImagesSearch.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra("SearchValue", lessonNameET.getText().toString());
        intent.putExtra("CourseId", lessonId);
        intent.putExtra("imageName", "outlineImage");
        intent.putExtras(setSavedInstanceState());
        startActivity(intent);
        finish();
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.lesson_name_et:
                    validateName();
                    break;
                case R.id.lesson_outline_et:
                    validateOutline();
                    break;

            }
        }
    }

    private boolean validateName() {
        if (lessonNameET.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError("Enter Lesson Name");
            requestFocus(lessonNameET);
            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateOutline() {
        if (lessonOverViewET.getText().toString().trim().isEmpty()) {
            inputLayoutOutline.setError("Enter lesson outline");
            requestFocus(lessonOverViewET);
            return false;
        } else {
            inputLayoutOutline.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }


    public Bundle setSavedInstanceState() {
        // Store values between instances here
        // SharedPreferences.Editor savedInstanceState = getPreferences(MODE_PRIVATE).edit();
        Bundle data = new Bundle();
        data.putString("LessonId", lessonId);

        lessonName = lessonNameET.getText().toString();
        lessonOverView = lessonOverViewET.getText().toString();



        data.putString("LessonOverView", lessonOverView);
        data.putString("LessonName", lessonName);
        data.putString("ImageUrl", imageUrl);

        return data;
    }


    public void getSavedInstanceState(Bundle data) {

        if (data.getString("LessonName") != null) {
            lessonName = data.getString("LessonName", null);
            lessonNameET.setText(lessonName);
        }
        lessonId = data.getString("LessonId");

        if (data.getString("LessonOverView") != null) {
            lessonOverView = data.getString("LessonOverView");
            lessonOverViewET.setText(lessonOverView);
        }

        if (data.getString("ImageUrl") != null) {
            imageUrl = data.getString("ImageUrl");


        }

    }

}
