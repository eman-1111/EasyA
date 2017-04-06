package eman.app.android.easya.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import eman.app.android.easya.ImagesSearch;
import eman.app.android.easya.R;
import eman.app.android.easya.utils.Helper;

import static android.app.Activity.RESULT_OK;

public class ApplyFragment extends Fragment implements View.OnClickListener{

    private static final String LOG_TAG = ApplyFragment.class.getSimpleName();

    TextInputLayout inputLayoutAppTitle, inputLayoutApp;
    public static  EditText  lessonLifeAppTitle, lessonLifeApp;
    ImageView  infoTitle, infoApp;
    public static ImageView imageApp;

    public static Bitmap thumbnail = null;
    public static String lessonAppTitle = "", lessonApp = "";

    public ApplyFragment() {

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
                showDialog(info);
                break;
            case R.id.app_info:
                info = this.getString(R.string.info_descr);
                showDialog(info);
                break;

            case R.id.app_image:
                startDialog();
                break;


        }

    }

    private void showDialog(String info) {
        AlertDialog.Builder builder2 = new AlertDialog.Builder(getActivity());
        builder2.setMessage(info);
        builder2.setPositiveButton("OK", null);
        builder2.show();
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

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (Build.VERSION.SDK_INT >= 23) {
                    if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(takePicture, 0);
                        alertDialog.dismiss();
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 1);
                        alertDialog.dismiss();

                    }
                } else {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                    alertDialog.dismiss();
                }


            }
        });
        gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 23) {
                    if (getActivity().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                            == PackageManager.PERMISSION_GRANTED) {
                        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(pickPhoto, 1);

                        alertDialog.dismiss();
                    } else {
                        ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                        alertDialog.dismiss();
                    }
                } else {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);

                    alertDialog.dismiss();
                }


            }
        });

        searchPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startImageIntent();
                alertDialog.dismiss();
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

                    Log.e(LOG_TAG, "camera: " +thumbnail.getByteCount());

                    if(thumbnail.getByteCount() > 1000000){
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
                            Log.e(LOG_TAG, "gallary: " +thumbnail.getByteCount());

                            if(thumbnail.getByteCount() > 100000){
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
                    Log.e(LOG_TAG, "Search: " +thumbnail.getByteCount());

                    if (thumbnail != null) {
                        imageApp.setImageBitmap(thumbnail);
                    }
                }
                break;

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case 2: {
                //add
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickPhoto, 1);
                } else {

                }
                return;
            }

            case 1: {
                //add
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePicture, 0);
                } else {

                }
                return;
            }
        }
    }
}
