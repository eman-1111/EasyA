package link.ideas.easya.ui.add_lesson;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.io.IOException;

import link.ideas.easya.data.database.Lesson;
import link.ideas.easya.ui.image_search.ImagesSearch;
import link.ideas.easya.R;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.Helper;
import link.ideas.easya.utils.ImageSaver;

import static android.app.Activity.RESULT_OK;

public class SummaryFragment extends Fragment {

    Bitmap thumbnail = null;
    private final String LOG_TAG = SummaryFragment.class.getSimpleName();
    EditText lessonNameET, lessonOverViewET;
    ImageView outlineImage;
    TextInputLayout inputLayoutName, inputLayoutOutline;
    View view;

    public SummaryFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        view = inflater.inflate(R.layout.fragment_add_summary, container, false);

        setUpID(view);

        if(getArguments() != null){
            Lesson lesson = getArguments().getParcelable(Constants.PREF_LESSON);
            setSummaryData(lesson.getLessonTitle(),
                    lesson.getLessonSummary(), lesson.getLessonTitle());
        }
        return view;
    }


    private void setUpID(View view) {
        inputLayoutName = (TextInputLayout) view.findViewById(R.id.input_layout_name);
        inputLayoutOutline = (TextInputLayout) view.findViewById(R.id.input_layout_outline);

        lessonNameET = (EditText) view.findViewById(R.id.lesson_name_et);
        lessonOverViewET = (EditText) view.findViewById(R.id.lesson_outline_et);

        outlineImage = (ImageView) view.findViewById(R.id.outline_image);
        outlineImage.setContentDescription(getResources().getString(R.string.a11y_outline_image));
        outlineImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= 23) {
                    if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        Log.e(LOG_TAG, "PERMISSION_GRANTED");
                        startDialog();
                    } else {
                        Log.e(LOG_TAG, "Not PERMISSION_GRANTED(Else.VERSION.SDK_INT");
                        //  ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
                        requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);

                    }
                } else {
                    startDialog();
                }
            }
        });

    }


    @Override
    public void onResume() {
        super.onResume();
        outlineImage.setImageBitmap(thumbnail);
    }


    @Override
    public void onPause() {
        super.onPause();
        outlineImage.setImageBitmap(null);
    }

    public void startImageIntent() {
        Intent intent = new Intent(getActivity(), ImagesSearch.class);
        startActivityForResult(intent, 2);
    }

    // take image Dialog
    AlertDialog alertDialog;

    public void startDialog() {
        LayoutInflater li = LayoutInflater.from(getActivity());
        View promptsView = li.inflate(R.layout.select_image_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        // set prompts.xml to alertdialog builder
        alertDialogBuilder.setView(promptsView);

        final LinearLayout gallery = (LinearLayout) promptsView
                .findViewById(R.id.linear_gallery);
        final LinearLayout photo = (LinearLayout) promptsView
                .findViewById(R.id.linear_take_photo);
        final LinearLayout searchPhoto = (LinearLayout) promptsView
                .findViewById(R.id.linear_search_photo);

        final TextView txtCancel = (TextView) promptsView
                .findViewById(R.id.txt_cancel);

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(takePicture, 0);
                alertDialog.dismiss();

            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(pickPhoto, 1);
                alertDialog.dismiss();


            }
        });

        searchPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startImageIntent();
                alertDialog.dismiss();
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.cancel();
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.show();


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
        Image image = null;
        switch (requestCode) {

            case 0:
                if (resultCode == RESULT_OK) {
                    thumbnail = null;
                    thumbnail = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    if (thumbnail.getByteCount() > Constants.BYTE_COUNT) {
                        thumbnail = Helper.getImageCompress(thumbnail);

                    }
                    if (thumbnail != null) {
                        outlineImage.setImageBitmap(thumbnail);


                    }
                    break;
                }
            case 1:
                if (resultCode == RESULT_OK) {
                    thumbnail = null;
                    if (imageReturnedIntent != null) {
                        try {
                            thumbnail = MediaStore.Images.Media.getBitmap
                                    (getActivity().getApplicationContext().getContentResolver(), imageReturnedIntent.getData());

                            if (thumbnail.getByteCount() > Constants.BYTE_COUNT) {
                                thumbnail = Helper.getImageCompress(thumbnail);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (thumbnail != null) {
                        outlineImage.setImageBitmap(thumbnail);
                    }
                }
                break;
            case 2:

                if (resultCode == RESULT_OK) {
                    thumbnail = null;
                    thumbnail = (Bitmap) imageReturnedIntent.getExtras().get("data");

                    if (thumbnail != null) {
                        outlineImage.setImageBitmap(thumbnail);
                    }
                }
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 101:
                Log.e(LOG_TAG, "onRequestPermissionsResult 1");
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e(LOG_TAG, "onRequestPermissionsResult 2");

                    startDialog();
                } else {
                    Log.e(LOG_TAG, "onRequestPermissionsResult 3");

                    Helper.startDialog(getActivity(), "",
                            getResources().getString(R.string.get_image_permissions));
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public interface Callback {
        public void onSavedClickedSummary(String lessonTitle,String lessonSummary,Bitmap SummaryImage);
    }

    public  void getSummerData(){
        ((Callback) getActivity())
                .onSavedClickedSummary( lessonNameET.getText().toString(), lessonOverViewET.getText().toString(), thumbnail);
    }



    private boolean validateName() {
        if (lessonNameET.getText().toString().trim().isEmpty()) {
            inputLayoutName.setError(getResources().getString(R.string.lesson_name_error));
            requestFocus(lessonNameET);

            return false;
        } else {
            inputLayoutName.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateOutline() {
        if (lessonOverViewET.getText().toString().trim().isEmpty()) {
            inputLayoutOutline.setError(getResources().getString(R.string.summary_error));
            requestFocus(lessonOverViewET);
            return false;
        } else {
            inputLayoutOutline.setErrorEnabled(false);
        }

        return true;
    }
    public boolean validateSummary() {
        if(validateName() &&  validateOutline()){
            return true;
        }else {
            return false;
        }
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void setSummaryData(String lessonTitle, String lessonOver, String image) {
        lessonNameET.setText(lessonTitle);
        lessonOverViewET.setText(lessonOver);

        thumbnail = new ImageSaver(getActivity()).
                setFileName(image + Constants.LESSON_SUMMARY).
                setDirectoryName(Constants.APP_NAME).
                load();

    }

}
