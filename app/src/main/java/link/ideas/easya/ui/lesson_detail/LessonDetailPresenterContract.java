package link.ideas.easya.ui.lesson_detail;

import android.graphics.Bitmap;

import link.ideas.easya.data.database.Course;
import link.ideas.easya.data.database.Lesson;

/**
 * Created by Eman on 3/1/2018.
 */

public interface LessonDetailPresenterContract {
    void shareUserLesson(Course shareCourse, Lesson shareLesson, String accountName
            , String userName, Bitmap outlineImageBit, Bitmap linkImageBit, Bitmap appImageBit);



}
