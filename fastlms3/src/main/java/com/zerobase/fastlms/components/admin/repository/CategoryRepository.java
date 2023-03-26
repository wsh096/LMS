package com.zerobase.fastlms.components.admin.repository;

import com.zerobase.fastlms.components.admin.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}
