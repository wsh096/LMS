package com.zerobase.fastlms.member.repository;

import com.zerobase.fastlms.member.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<MemberEntity, String> {

    Optional<MemberEntity> findByEmailAuthKey(String emailAuthKey);
    Optional<MemberEntity> findByUserIdAndUserName(String userId,
                                                   String userName);

}
