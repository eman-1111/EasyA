package link.ideas.easya.fragment;

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
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import link.ideas.easya.ImagesSearch;
import link.ideas.easya.R;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.Helper;

import static android.app.Activity.RESULT_OK;

/**
 * Created by Eman on 4/1/2017.
 */

public class LinkFragment extends Fragment implements View.OnClickListener {


    private final String LOG_TAG = LinkFragment.class.getSimpleName();
    Bitmap thumbnail = null;
    TextInputLayout inputLayoutLink, inputLayoutDebug;
    EditText lessonLink, lessonDebug;
    ImageView infoLink, infoDebug;
    public  ImageView imageLink;

    public LinkFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.btn_save_menu) {
        }
        return super.onOptionsItemSelected(item);
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
        return view;

    }


    private void setUpIds(View view) {
        inputLayoutLink = (TextInputLayout) view.findViewById(R.id.input_layout_linkx);
        inputLayoutDebug = (TextInputLayout) view.findViewById(R.id.input_layout_debug);

        lessonLink = (EditText) view.findViewById(R.id.lesson_linkx_et);
        lessonDebug = (EditText) view.findViewById(R.id.lesson_debug_et);

        infoLink = (ImageView) view.findViewById(R.id.link_info);
        infoDebug = (ImageView) view.findViewById(R.id.debug_info);
        infoLink.setOnClickListener(this);
        infoDebug.setOnClickListener(this);

        imageLink = (ImageView) view.findViewById(R.id.link_image);
        imageLink.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        String info;
        switch (v.getId()) {
            case R.id.link_info:
                info = this.getString(R.string.info_link);

                Helper.startDialog(getActivity(), "",info);
                break;
            case R.id.debug_info:
                info = this.getString(R.string.info_debug);
                Helper.startDialog(getActivity(), "",info);
                break;

            case R.id.link_image:
                if (Build.VERSION.SDK_INT >= 23) {
                    if (getActivity().checkSelfPermission(Manifest.permission.CAMERA)
                            == PackageManager.PERMISSION_GRANTED) {
                        startDialog();
                    } else {
                        requestPermissions(new String[]{Manifest.permission.CAMERA,
                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE}, 102);

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
        imageLink.setImageBitmap(thumbnail);
    }

    @Override
    public void onPause() {
        super.onPause();
        imageLink.setImageBitmap(null);
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
                        imageLink.setImageBitmap(thumbnail);

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
                        imageLink.setImageBitmap(thumbnail);


                    }
                }
                break;
            case 2:
                if (resultCode == RESULT_OK) {
                    thumbnail = null;
                    thumbnail = (Bitmap) imageReturnedIntent.getExtras().get("data");
                    if (thumbnail != null) {
                        imageLink.setImageBitmap(thumbnail);
                    }
                }
                break;

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 102:
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
        public void onSavedClickedLink(String lessonLink,String lessonDebug,Bitmap linkImage);
    }

    public  void getLinkData(){
        ((LinkFragment.Callback) getActivity())
                .onSavedClickedLink( lessonLink.getText().toString(), lessonDebug.getText().toString(), thumbnail);
    }
}
