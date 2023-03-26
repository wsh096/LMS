package com.zerobase.fastlms.course.model;

import com.zerobase.fastlms.components.admin.model.CommonParam;
import lombok.Data;

@Data
public class CourseParam extends CommonParam {

    long id;//course.id
    long categoryId;

}
