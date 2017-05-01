package link.ideas.easya;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import link.ideas.easya.data.CourseContract;
import link.ideas.easya.fragment.FavLessonListFragment;
import link.ideas.easya.fragment.LessonListFragment;


public class LessonList extends BaseActivity implements
        LessonListFragment.Callback, FavLessonListFragment.Callback {

    static Uri mUri;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.subject_list);

        if (savedInstanceState == null) {

            mUri = getIntent().getData();
            Log.e("LessonList", "d: "+ mUri);
            if (mUri == null) {
                Log.e("LessonList", "no uri");
            }
        }
        setDrawer(true);
        setUpAPIs();
        loadNavHeader(getResources().getString(R.string.your_lesson));
        setUpNavigationView();

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);




    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        Bundle arguments = new Bundle();
        arguments.putParcelable(LessonListFragment.SUBJECT_URI, mUri);

        LessonListFragment fragment = new LessonListFragment();
        fragment.setArguments(arguments);

        FavLessonListFragment fragmentFav = new FavLessonListFragment();
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
        Intent intent = new Intent(this, LessonDetail.class)
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