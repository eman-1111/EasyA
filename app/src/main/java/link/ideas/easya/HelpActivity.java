package link.ideas.easya;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.github.paolorotolo.appintro.AppIntro;
import link.ideas.easya.fragment.SampleSlide;

/**
 * Created by Eman on 5/5/2017.
 */

public class HelpActivity extends AppIntro {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setFadeAnimation();

        addSlide(SampleSlide.newInstance(R.layout.fragment_help_welcom));
        addSlide(SampleSlide.newInstance(R.layout.fragment_help_summary));
        addSlide(SampleSlide.newInstance(R.layout.fragment_help_link));
        addSlide(SampleSlide.newInstance(R.layout.fragment_help_apply));
        addSlide(SampleSlide.newInstance(R.layout.fragment_help_share));


        showSkipButton(true);
        setProgressButtonEnabled(true);


    }

    @Override
    public void onSkipPressed(Fragment currentFragment) {
        super.onSkipPressed(currentFragment);
        Intent homeIntent = new Intent(HelpActivity.this, CoursesList.class);
        startActivity(homeIntent);
    }

    @Override
    public void onDonePressed(Fragment currentFragment) {
        super.onDonePressed(currentFragment);
        Intent homeIntent = new Intent(HelpActivity.this, CoursesList.class);
        startActivity(homeIntent);
    }

    @Override
    public void onSlideChanged(@Nullable Fragment oldFragment, @Nullable Fragment newFragment) {
        super.onSlideChanged(oldFragment, newFragment);
        // Do something when the slide changes.
    }
}