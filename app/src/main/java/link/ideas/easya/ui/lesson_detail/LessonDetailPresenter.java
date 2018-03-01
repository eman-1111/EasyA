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


    private Uri appUrl, linkUrl, summaryUrl;

    @Override
    public void shareUserLesson(Course shareCourse, Lesson shareLesson, String accountName
            , String userName, Bitmap outlineImageBit, Bitmap linkImageBit, Bitmap appImageBit) {


        if (shareCourse.getFirebaseId().equals("")) {
            addCourseToFirebase(shareCourse, shareLesson, accountName, userName
                    , outlineImageBit, linkImageBit, appImageBit);
        } else {
            DatabaseReference mCoursDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_LOCATION_USERS_COURSES).
                    child(Helper.encodeEmail(accountName)).child(Constants.FIREBASE_LOCATION_USER_COURSES);
            final String coursePushId = mCoursDatabaseReference.push().getKey();
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

    private void addLessonToFirebase(String shareCourse, Lesson shareLesson
            , String userName, Bitmap outlineImageBit, Bitmap linkImageBit, Bitmap appImageBit) {

        if (outlineImageBit != null) {
            addImageToFirebase(shareCourse, shareLesson, userName
                    , outlineImageBit, linkImageBit, appImageBit);
        } else {
            addLessonLinkToFirebase(shareCourse, shareLesson, userName
                    , linkImageBit, appImageBit);
        }

    }

    private void addLessonLinkToFirebase(final String courseFirebaseId, final Lesson shareLesson
            , final String userName, final Bitmap linkImageBit, final Bitmap appImageBit) {

        DatabaseReference mLessonDatabaseReference = FirebaseDatabase.getInstance().getReference().
                child(Constants.FIREBASE_LOCATION_USERS_LESSONS).child(courseFirebaseId);


        Date todayDate = Helper.getNormalizedUtcDateForToday();
        Lesson lessonLinks = new Lesson(shareLesson.getLessonTitle(), shareLesson.getLessonLink()
                , summaryUrl + "", userName, true,
                DateConverter.toTimestamp(todayDate), DateConverter.toTimestamp(todayDate));

        final String lessonPushId = mLessonDatabaseReference.push().getKey();
        mLessonDatabaseReference.child(lessonPushId).setValue(lessonLinks);
        mLessonDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                mViewModel.updateFirebaseIdL(lessonPushId);
                addLessonDetailToFirebase(courseFirebaseId, shareLesson, linkImageBit, appImageBit);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //todo set error
            }
        });

    }

    private void addLessonDetailToFirebase(String courseFirebaseId, Lesson shareLesson, Bitmap linkImageBit,
                                           Bitmap appImageBit) {

        if (linkImageBit != null) {
            addImageToFirebaseLink(courseFirebaseId, shareLesson
                    , linkImageBit, appImageBit);
        } else if (appImageBit != null) {
            addImageToFirebaseApp(courseFirebaseId, shareLesson, appImageBit);
        } else {
            addLessonDetailsToFirebase(courseFirebaseId, shareLesson);
        }


    }

    private void addLessonDetailsToFirebase(String courseFirebaseId, Lesson shareLesson) {
        DatabaseReference mLessonDetailDatabaseReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.FIREBASE_LOCATION_USERS_LESSONS_DETAIL).
                        child(courseFirebaseId).child(shareLesson.getFirebaseId());

        Lesson lessonDetail = new Lesson(shareLesson.getLessonSummary(), linkUrl + "",
                shareLesson.getLessonPracticalTitle(), shareLesson.getLessonPractical(),
                appUrl + "", shareLesson.getLessonDebug());

        mLessonDetailDatabaseReference.setValue(lessonDetail);

    }

    private void addImageToFirebase(final String shareCourse, final Lesson shareLesson
            , final String userName, final Bitmap outlineImageBit,
                                    final Bitmap linkImageBit, final Bitmap appImageBit) {

        StorageReference mUserImagesReferenceSummary = FirebaseStorage.getInstance().getReference()
                .child(shareCourse + "/" + shareLesson.getLessonTitle() + "/summary.jpg");

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

                summaryUrl = taskSnapshot.getDownloadUrl();
                addLessonLinkToFirebase(shareCourse, shareLesson, userName
                        , linkImageBit, appImageBit);

            }
        });
    }

    private void addImageToFirebaseLink(final String courseFirebaseId, final Lesson shareLesson,
                                        final Bitmap linkImageBit, final Bitmap appImageBit) {

        StorageReference mUserImagesReferenceLink = FirebaseStorage.getInstance().getReference()
                .child(courseFirebaseId + "/" + shareLesson.getLessonTitle() + "/link.jpg");

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
                linkUrl = taskSnapshot.getDownloadUrl();
                if (appImageBit != null) {
                    addImageToFirebaseApp(courseFirebaseId, shareLesson, appImageBit);
                } else {
                    addLessonDetailsToFirebase(courseFirebaseId, shareLesson);
                }
            }
        });
    }

    private void addImageToFirebaseApp(final String courseFirebaseId, final Lesson shareLesson
            , final Bitmap appImageBit) {

        StorageReference mUserImagesReferenceApp = FirebaseStorage.getInstance().getReference()
                .child(courseFirebaseId + "/" + shareLesson.getLessonTitle() + "/app.jpg");
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
                appUrl = taskSnapshot.getDownloadUrl();
                addLessonDetailsToFirebase(courseFirebaseId, shareLesson);
            }
        });
    }
}
