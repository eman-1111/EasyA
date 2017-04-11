package eman.app.android.easya;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Fade;
import android.transition.Slide;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import eman.app.android.easya.data.CourseContract;
import eman.app.android.easya.fragment.FavSubjectListFragment;
import eman.app.android.easya.fragment.SubjectDetailFragment;
import eman.app.android.easya.fragment.SubjectListFragment;


public class SubjectList extends AppCompatActivity implements SubjectListFragment.Callback, FavSubjectListFragment.Callback {
    static Uri mUri;
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_list);


        if (savedInstanceState == null) {

            mUri = getIntent().getData();
            Log.e("SubjectList", "d: "+ mUri);
            if (mUri == null) {
                Log.e("SubjectList", "no uri");
            }


        }
         toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        //ImageSyncAdapter.initializeSyncAdapter(this);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);




    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle arguments = new Bundle();
        arguments.putParcelable(SubjectListFragment.SUBJECT_URI, mUri);

        SubjectListFragment fragment = new SubjectListFragment();
        fragment.setArguments(arguments);

        FavSubjectListFragment fragmentFav = new FavSubjectListFragment();
        fragmentFav.setArguments(arguments);
        adapter.addFragment(fragment, "Subject");
        adapter.addFragment(fragmentFav, "Fav Subject");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_subject_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();



        if (id == R.id.btn_add_menu) {
            startIntent();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onItemSelected(Uri contentUri) {
        Intent intent = new Intent(this, SubjectDetail.class)
                .setData(contentUri);
        startActivity(intent);

    }

    public void startIntent() {
        //Intent intent = new Intent(this, AddSubjectTitel.class);
        Intent intent = new Intent(this, AddNewLesson.class);
        intent.putExtra("CourseId", CourseContract.SubjectEntry.getSubjectIdFromUri(mUri));
        startActivity(intent);
    }


}
