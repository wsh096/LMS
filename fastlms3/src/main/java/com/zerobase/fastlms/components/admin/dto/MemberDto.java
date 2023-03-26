package com.zerobase.fastlms.components.admin.dto;

import com.zerobase.fastlms.member.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class MemberDto {
    
    String userId;
    String userName;
    String phone;
    String password;
    LocalDateTime regDt;
    LocalDateTime udtDt;
    
    boolean emailAuthYn;
    LocalDateTime emailAuthDt;
    String emailAuthKey;
    
    String resetPasswordKey;
    LocalDateTime resetPasswordLimitDt;
    
    boolean adminYn;
    String userStatus;
    
    private String zipcode;
    private String addr;
    private String addrDetail;
    
    //추가컬럼
    long totalCount;
    long seq;
    
    
    public static MemberDto of(MemberEntity memberEntity) {
        
        return MemberDto.builder()
                .userId(memberEntity.getUserId())
                .userName(memberEntity.getUserName())
                .phone(memberEntity.getPhone())
                //.password(member.getPassword())
                .regDt(memberEntity.getRegDt())
                .udtDt(memberEntity.getUdtDt())
                .emailAuthYn(memberEntity.isEmailAuthYn())
                .emailAuthDt(memberEntity.getEmailAuthDt())
                .emailAuthKey(memberEntity.getEmailAuthKey())
                .resetPasswordKey(memberEntity.getResetPasswordKey())
                .resetPasswordLimitDt(memberEntity.getResetPasswordLimitDt())
                .adminYn(memberEntity.isAdminYn())
                .userStatus(memberEntity.getUserStatus())
                
                .zipcode(memberEntity.getZipcode())
                .addr(memberEntity.getAddr())
                .addrDetail(memberEntity.getAddrDetail())
                
                .build();
    }
    
    
    public String getRegDtText() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        return regDt != null ? regDt.format(formatter) : "";
    }
    
    public String getUdtDtText() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
        return udtDt != null ? udtDt.format(formatter) : "";
        
    }
    
}
