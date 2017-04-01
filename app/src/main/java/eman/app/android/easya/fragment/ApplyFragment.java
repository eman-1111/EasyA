package eman.app.android.easya.fragment;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import eman.app.android.easya.AddLessonDetail;
import eman.app.android.easya.R;

public class ApplyFragment extends Fragment {

    private static final String LOG_TAG = AddLessonDetail.class.getSimpleName();

    boolean edit = false;
    TextInputLayout inputLayoutAppTitle, inputLayoutApp;
    EditText  lessonLifeAppTitle, lessonLifeApp;
    ImageView  infoTitle, infoApp;
    ImageView imageApp;
    String  imageAppS = "l";
    String  lessonLifeAppTitles, lessonLifeApps;
    public ApplyFragment() {
        // Required empty public constructor
    }


    public static ApplyFragment newInstance() {
        ApplyFragment fragment = new ApplyFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_apply, container, false);
        setUpIds(view);
        return view;
    }
    private void setUpIds(View view) {
        inputLayoutAppTitle = (TextInputLayout) view.findViewById(R.id.input_layout_app_title);
        inputLayoutApp = (TextInputLayout) view.findViewById(R.id.input_layout_app);

        lessonLifeAppTitle = (EditText) view.findViewById(R.id.lesson_app_title_et);
        lessonLifeApp = (EditText) view.findViewById(R.id.lesson_app_et);


        infoTitle = (ImageView) view.findViewById(R.id.title_info);
        infoApp = (ImageView) view.findViewById(R.id.app_info);

        imageApp = (ImageView) view.findViewById(R.id.app_image);

    }

}
