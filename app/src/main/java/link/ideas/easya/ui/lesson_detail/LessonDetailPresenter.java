package link.ideas.easya.ui.lesson_detail;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;

import link.ideas.easya.data.database.Course;
import link.ideas.easya.data.database.DateConverter;
import link.ideas.easya.data.database.Lesson;
import link.ideas.easya.utils.Constants;
import link.ideas.easya.utils.Helper;

/**
 * Created by Eman on 3/1/2018.
 */

public class LessonDetailPresenter implements LessonDetailPresenterContract {

    LessonDetailViewModel mViewModel;

    public LessonDetailPresenter(LessonDetailViewModel mViewModel){
        this.mViewModel = mViewModel;

    }



    @Override
    public void shareUserLesson(Course shareCourse, Lesson shareLesson, String accountName
            , String userName, Bitmap outlineImageBit, Bitmap linkImageBit, Bitmap appImageBit) {


        if (shareCourse.getFirebaseId().equals("")) {
            addCourseToFirebase(shareCourse, shareLesson, accountName, userName
                    , outlineImageBit, linkImageBit, appImageBit);
        } else {

            final String coursePushId =shareCourse.getFirebaseId();
            if (shareLesson.getFirebaseId().equals("")) {
                addLessonToFirebase(coursePushId, shareLesson, userName
                        , outlineImageBit, linkImageBit, appImageBit);
            }
        }

    }


    private void addCourseToFirebase(final Course shareCourse, final Lesson shareLesson
            , final String accountName, final String userName, final Bitmap outlineImageBit,
                                     final Bitmap linkImageBit, final Bitmap appImageBit) {

        Date todayDate = Helper.getNormalizedUtcDateForToday();
        Course course = new Course(shareCourse.getId(), shareCourse.getCourseName()
                , shareCourse.getTeacherName(), shareCourse.getTeacherEmail(),
                shareCourse.getTeacherPhotoURL(), shareCourse.getTeacherColor(),
                DateConverter.toTimestamp(todayDate), DateConverter.toTimestamp(todayDate));

        DatabaseReference mCoursDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_LOCATION_USERS_COURSES).
                child(Helper.encodeEmail(accountName)).child(Constants.FIREBASE_LOCATION_USER_COURSES);
        final String coursePushId = mCoursDatabaseReference.push().getKey();
        mCoursDatabaseReference.child(coursePushId).setValue(course);


        mCoursDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mViewModel.updateFirebaseId(coursePushId);
                addLessonToFirebase(coursePushId, shareLesson, userName
                        , outlineImageBit, linkImageBit, appImageBit);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void addLessonToFirebase(String coursePushId, Lesson shareLesson
            , String userName, Bitmap outlineImageBit, Bitmap linkImageBit, Bitmap appImageBit) {

        DatabaseReference mLessonDatabaseReference = FirebaseDatabase.getInstance().getReference().
                child(Constants.FIREBASE_LOCATION_USERS_LESSONS).child(coursePushId);
        final String lessonPushId = mLessonDatabaseReference.push().getKey();


        if (outlineImageBit != null) {
            addImageToFirebase(coursePushId,lessonPushId, shareLesson, userName
                    , outlineImageBit, linkImageBit, appImageBit);
        } else {
            addLessonLinkToFirebase(coursePushId,lessonPushId, shareLesson, userName
                    , linkImageBit, appImageBit, null);
        }

    }

    private void addLessonLinkToFirebase(final String coursePushId, final String lessonPushId,
                                         final Lesson shareLesson
            , final String userName, final Bitmap linkImageBit, final Bitmap appImageBit, Uri summaryUrl ) {

        DatabaseReference mLessonDatabaseReference = FirebaseDatabase.getInstance().getReference().
                child(Constants.FIREBASE_LOCATION_USERS_LESSONS).child(coursePushId).child(lessonPushId);

        Date todayDate = Helper.getNormalizedUtcDateForToday();
        Lesson lessonLinks = new Lesson(shareLesson.getLessonTitle(), shareLesson.getLessonLink()
                , summaryUrl + "", userName, true,
                DateConverter.toTimestamp(todayDate), DateConverter.toTimestamp(todayDate));


        mLessonDatabaseReference.setValue(lessonLinks);
        mLessonDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mViewModel.updateFirebaseIdL(lessonPushId);
                addLessonDetailToFirebase(coursePushId,lessonPushId, shareLesson, linkImageBit, appImageBit);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //todo set error
            }
        });

    }

    private void addLessonDetailToFirebase(String courseFirebaseId, String lessonFirebaseId, Lesson shareLesson, Bitmap linkImageBit,
                                           Bitmap appImageBit) {

        if (linkImageBit != null) {
            addImageToFirebaseLink(courseFirebaseId,  lessonFirebaseId,shareLesson
                    , linkImageBit, appImageBit);
        } else if (appImageBit != null) {
            addImageToFirebaseApp(courseFirebaseId,  lessonFirebaseId,shareLesson, appImageBit, null);
        } else {
            addLessonDetailsToFirebase(courseFirebaseId, lessonFirebaseId, shareLesson, null, null);
        }


    }

    private void addLessonDetailsToFirebase(String courseFirebaseId, final String lessonFirebaseId,Lesson shareLesson,
                                            Uri appUrl ,Uri linkUrl) {
        DatabaseReference mLessonDetailDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.FIREBASE_LOCATION_USERS_LESSONS_DETAIL).
                        child(courseFirebaseId).child(lessonFirebaseId);

        Lesson lessonDetail = new Lesson(shareLesson.getLessonSummary(), linkUrl + "",
                shareLesson.getLessonPracticalTitle(), shareLesson.getLessonPractical(),
                appUrl + "", shareLesson.getLessonDebug());

        mLessonDetailDatabaseReference.setValue(lessonDetail);

    }

    private void addImageToFirebase(final String coursePushId,  final String lessonPushId,final Lesson shareLesson
            , final String userName, final Bitmap outlineImageBit,
                                    final Bitmap linkImageBit, final Bitmap appImageBit) {

        StorageReference mUserImagesReferenceSummary = FirebaseStorage.getInstance().getReference()
                .child(coursePushId + "/" + lessonPushId+ "/"+  shareLesson.getLessonTitle() + "/summary.jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        outlineImageBit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mUserImagesReferenceSummary.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                Uri summaryUrl = taskSnapshot.getDownloadUrl();
                addLessonLinkToFirebase(coursePushId, lessonPushId,shareLesson, userName
                        , linkImageBit, appImageBit,  summaryUrl);

            }
        });
    }

    private void addImageToFirebaseLink(final String courseFirebaseId, final String lessonFirebaseId,final Lesson shareLesson,
                                        final Bitmap linkImageBit, final Bitmap appImageBit) {

        StorageReference mUserImagesReferenceLink = FirebaseStorage.getInstance().getReference()
                .child(courseFirebaseId + "/" + lessonFirebaseId+ "/"+ shareLesson.getLessonTitle() + "/link.jpg");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        linkImageBit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = mUserImagesReferenceLink.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri linkUrl = taskSnapshot.getDownloadUrl();
                if (appImageBit != null) {
                    addImageToFirebaseApp(courseFirebaseId,lessonFirebaseId,  shareLesson, appImageBit, linkUrl);
                } else {
                    addLessonDetailsToFirebase(courseFirebaseId,lessonFirebaseId, shareLesson, null, linkUrl);
                }
            }
        });
    }

    private void addImageToFirebaseApp(final String courseFirebaseId, final String lessonFirebaseId,final Lesson shareLesson
            , final Bitmap appImageBit,final Uri linkUrl) {

        StorageReference mUserImagesReferenceApp = FirebaseStorage.getInstance().getReference()
                .child(courseFirebaseId + "/" + lessonFirebaseId+ "/"+   shareLesson.getLessonTitle() + "/app.jpg");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        appImageBit.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        UploadTask uploadTask = mUserImagesReferenceApp.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Uri  appUrl = taskSnapshot.getDownloadUrl();
                addLessonDetailsToFirebase(courseFirebaseId,lessonFirebaseId, shareLesson,appUrl, linkUrl );
            }
        });
    }
}
