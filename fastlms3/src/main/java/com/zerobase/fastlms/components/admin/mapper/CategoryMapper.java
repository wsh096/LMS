package com.zerobase.fastlms.components.admin.mapper;


import com.zerobase.fastlms.components.admin.dto.CategoryDto;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper {

    List<CategoryDto> select(CategoryDto parameter);

}
