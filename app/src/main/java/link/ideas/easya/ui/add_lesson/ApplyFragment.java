package link.ideas.easya.ui.add_lesson;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class ApplyFragment extends Fragment implements View.OnClickListener {

    private static final String LOG_TAG = ApplyFragment.class.getSimpleName();

    TextInputLayout inputLayoutAppTitle, inputLayoutApp;
    public EditText lessonLifeAppTitle, lessonLifeApp;
    ImageView infoTitle, infoApp;
    public ImageView imageApp;

    public Bitmap thumbnail = null;
    public String lessonAppTitle = "", lessonApp = "";

    public ApplyFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_apply, container, false);
        setUpIds(view);


        if (getArguments() != null) {
            Lesson lesson = getArguments().getParcelable(Constants.PREF_LESSON);
            setApplyData(lesson.getLessonPracticalTitle(),
                    lesson.getLessonPractical(), lesson.getLessonTitle());
        }
        return view;
    }

    private void setUpIds(View view) {
        inputLayoutAppTitle = (TextInputLayout) view.findViewById(R.id.input_layout_app_title);
        inputLayoutApp = (TextInputLayout) view.findViewById(R.id.input_layout_app);

        lessonLifeAppTitle = (EditText) view.findViewById(R.id.lesson_app_title_et);
        lessonLifeApp = (EditText) view.findViewById(R.id.lesson_app_et);


        infoTitle = (ImageView) view.findViewById(R.id.title_info);
        infoApp = (ImageView) view.findViewById(R.id.app_info);
        infoTitle.setContentDescription(getResources().getString(R.string.a11y_app_title_info));
        infoApp.setContentDescription(getResources().getString(R.string.a11y_app_info));

        imageApp = (ImageView) view.findViewById(R.id.app_image);
        imageApp.setContentDescription(getResources().getString(R.string.a11y_app_image));

        infoTitle.setOnClickListener(this);
        infoApp.setOnClickListener(this);

        imageApp.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        String info;
        switch (v.getId()) {
            case R.id.title_info:
                info = this.getString(R.string.info_title);
                Helper.startDialog(getActivity(), "", info);
                break;
            case R.id.app_info:
                info = this.getString(R.string.info_descr);
                Helper.startDialog(getActivity(), "", info);
                break;

            case R.id.app_image:
                lessonAppTitle = lessonLifeAppTitle.getText().toString();
                lessonApp = lessonLifeApp.getText().toString();
                if (Build.VERSION.SDK_INT >= 23) {
                    if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        startDialog();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 103);

                    }
                } else {
                    startDialog();
                }
                break;


        }

    }


    @Override
    public void onResume() {
        super.onResume();

        imageApp.setImageBitmap(thumbnail);
        lessonLifeAppTitle.setText(lessonAppTitle);
        lessonLifeApp.setText(lessonApp);
    }

    @Override
    public void onPause() {
        super.onPause();
        imageApp.setImageBitmap(null);

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
                    // Uri selectedImage = imageReturnedIntent.getData();
                    thumbnail = (Bitmap) imageReturnedIntent.getExtras().get("data");

                    if (thumbnail.getByteCount() > Constants.BYTE_COUNT) {
                        thumbnail = Helper.getImageCompress(thumbnail);

                    }
                    if (thumbnail != null) {
                        imageApp.setImageBitmap(thumbnail);


                    }
                    break;
                }
            case 1:
                if (resultCode == RESULT_OK) {

                    // Uri selectedImage = imageReturnedIntent.getData();
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
                        imageApp.setImageBitmap(thumbnail);


                    }
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    thumbnail = null;
                    thumbnail = (Bitmap) imageReturnedIntent.getExtras().get("data");

                    if (thumbnail != null) {
                        imageApp.setImageBitmap(thumbnail);
                    }
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 103:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startDialog();
                } else {
                    Helper.startDialog(getActivity(), "",
                            getResources().getString(R.string.get_image_permissions));
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    public interface Callback {
        public void onSavedClicked(String lessonAppTitle, String lessonApp, Bitmap applyImage);
    }

    public void getApplyData() {
        lessonAppTitle = lessonLifeAppTitle.getText().toString();
        lessonApp = lessonLifeApp.getText().toString();
        ((Callback) getActivity())
                .onSavedClicked(lessonAppTitle, lessonApp, thumbnail);
    }

    private void setApplyData(String lessonAppTitle, String lessonApp, String applyImage) {
        lessonLifeAppTitle.setText(lessonAppTitle);
        lessonLifeApp.setText(lessonApp);
        Bitmap appImageBit = new ImageSaver(getActivity()).
                setFileName(applyImage + Constants.LESSON_APP).
                setDirectoryName(Constants.APP_NAME).
                load();
        imageApp.setImageBitmap(appImageBit);
    }
}
