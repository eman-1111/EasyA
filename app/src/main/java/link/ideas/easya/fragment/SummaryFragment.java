package link.ideas.easya.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import link.ideas.easya.ImagesSearch;
import link.ideas.easya.R;
import link.ideas.easya.interfacee.SaveLesson;
import link.ideas.easya.utils.Helper;

import static android.app.Activity.RESULT_OK;

public class SummaryFragment extends Fragment {

    public static Bitmap thumbnail = null;
    private static final String LOG_TAG = SummaryFragment.class.getSimpleName();
    public static EditText lessonNameET, lessonOverViewET;
    public static ImageView outlineImage;
    public static TextInputLayout inputLayoutName, inputLayoutOutline;
    View view;

    public SummaryFragment() {
        setHasOptionsMenu(true);
    }

    public SummaryFragment newInstance(SaveLesson mListener) {
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

        view = inflater.inflate(R.layout.fragment_add_summary, container, false);
        setUpID(view);
        return view;
    }


    private void setUpID(View view) {
        inputLayoutName = (TextInputLayout) view.findViewById(R.id.input_layout_name);
        inputLayoutOutline = (TextInputLayout) view.findViewById(R.id.input_layout_outline);

        lessonNameET = (EditText) view.findViewById(R.id.lesson_name_et);
        lessonOverViewET = (EditText) view.findViewById(R.id.lesson_outline_et);
        outlineImage = (ImageView) view.findViewById(R.id.outline_image);



        outlineImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startDialog();
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
                        Log.e(LOG_TAG, "cameraA: " +thumbnail.getByteCount());
                    }
                    if (thumbnail != null) {
                        outlineImage.setImageBitmap(thumbnail);


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
                                Log.e(LOG_TAG, "gallaryA: " +thumbnail.getByteCount());
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
                    Log.e(LOG_TAG, "Search: " +thumbnail.getByteCount());

                    if (thumbnail != null) {
                        outlineImage.setImageBitmap(thumbnail);
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
