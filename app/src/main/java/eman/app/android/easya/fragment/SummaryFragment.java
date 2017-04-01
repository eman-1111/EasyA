package eman.app.android.easya.fragment;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import eman.app.android.easya.AddSubjectTitel;
import eman.app.android.easya.R;

public class SummaryFragment extends Fragment {


    private static final String LOG_TAG = SummaryFragment.class.getSimpleName();
    EditText lessonNameET, lessonOverViewET;
    ImageView outlineImage;
    TextInputLayout inputLayoutName, inputLayoutOutline;

    public SummaryFragment() {
        // Required empty public constructor
    }



    public static SummaryFragment newInstance() {
        SummaryFragment fragment = new SummaryFragment();
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
        View view = inflater.inflate(R.layout.fragment_add_summary, container, false);
        setUpID(view);

        return view;
    }


    private void setUpID(View view) {
        inputLayoutName = (TextInputLayout) view.findViewById(R.id.input_layout_name);
        inputLayoutOutline = (TextInputLayout) view.findViewById(R.id.input_layout_outline);

        lessonNameET = (EditText) view.findViewById(R.id.lesson_name_et);
        lessonOverViewET = (EditText) view.findViewById(R.id.lesson_outline_et);
        outlineImage = (ImageView) view.findViewById(R.id.outline_image);

    }
}
