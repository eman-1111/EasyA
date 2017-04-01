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

/**
 * Created by Eman on 4/1/2017.
 */

public class LinkFragment extends Fragment {


    private static final String LOG_TAG = LinkFragment.class.getSimpleName();

    String courseId, lessonName, lessonOverView, imageOutline;
    boolean edit = false;
    TextInputLayout inputLayoutLink, inputLayoutDebug;
    EditText lessonLink, lessonDebug;
    ImageView infoLink, infoDebug;
    ImageView imageLink, imageDebug;
    String imageLinkS = "l" , imageDebugS = "l";
    String lessonLinks, lessonDebugs;


    public LinkFragment() {
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
        View view = inflater.inflate(R.layout.fragment_link, container, false);
        setUpIds(view);
        return view ;

    }


    private void setUpIds(View view) {
        inputLayoutLink = (TextInputLayout) view.findViewById(R.id.input_layout_linkx);
        inputLayoutDebug = (TextInputLayout) view.findViewById(R.id.input_layout_debug);

        lessonLink = (EditText) view.findViewById(R.id.lesson_linkx_et);
        lessonDebug = (EditText) view.findViewById(R.id.lesson_debug_et);

        infoLink = (ImageView) view.findViewById(R.id.link_info);
        infoDebug = (ImageView) view.findViewById(R.id.debug_info);


        imageLink = (ImageView) view.findViewById(R.id.link_image);
        imageDebug = (ImageView) view.findViewById(R.id.debug_image);

    }


}
