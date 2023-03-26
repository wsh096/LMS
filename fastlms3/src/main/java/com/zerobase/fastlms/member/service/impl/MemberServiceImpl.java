package com.zerobase.fastlms.member.service.impl;

import com.zerobase.fastlms.admin.dto.MemberDto;
import com.zerobase.fastlms.admin.mapper.MemberMapper;
import com.zerobase.fastlms.admin.model.MemberParam;
import com.zerobase.fastlms.components.MailComponents;
import com.zerobase.fastlms.course.model.ServiceResult;
import com.zerobase.fastlms.member.entity.MemberEntity;
import com.zerobase.fastlms.member.entity.MemberCode;
import com.zerobase.fastlms.member.exception.MemberNotEmailAuthException;
import com.zerobase.fastlms.member.exception.MemberStopUserException;
import com.zerobase.fastlms.member.model.MemberInput;
import com.zerobase.fastlms.member.model.ResetPasswordInput;
import com.zerobase.fastlms.member.repository.MemberRepository;
import com.zerobase.fastlms.member.service.MemberService;
import com.zerobase.fastlms.util.PasswordUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class MemberServiceImpl implements MemberService {
    
    private final MemberRepository memberRepository;
    private final MailComponents mailComponents;
    
    private final MemberMapper memberMapper;
    
    /**
     * 회원 가입
     */
    @Override
    public boolean register(MemberInput parameter) {
        // null을 사용하기 위해 나온 Optional
        Optional<MemberEntity> optionalMemberEntity =
                memberRepository.findById(parameter.getUserId());
        if (optionalMemberEntity.isPresent()) {
            //현재 userId에 해당하는 데이터 존재
            return false;
        }

        String encPassword = BCrypt.hashpw(parameter.getPassword(), BCrypt.gensalt());
        String uuid = UUID.randomUUID().toString();
        
        MemberEntity memberEntity = MemberEntity.builder()
                .userId(parameter.getUserId())
                .userName(parameter.getUserName())
                .phone(parameter.getPhone())
                .password(encPassword)
                .regDt(LocalDateTime.now())
                .emailAuthYn(false)
                .emailAuthKey(uuid)
                .userStatus(MemberEntity.MEMBER_STATUS_REQ)
                .build();
        memberRepository.save(memberEntity);
        
        String email = parameter.getUserId();
        String subject = "fastlms 사이트 가입을 축하드립니다. ";
        String text = "<p>fastlms 사이트 가입을 축하드립니다.<p><p>아래 링크를 클릭하셔서 가입을 완료 하세요.</p>"
                + "<div><a target='_blank' href='http://localhost:8080/member/email-auth?id=" + uuid + "'> 가입 완료 </a></div>";
        mailComponents.sendMail(email, subject, text);
        
        return true;
    }
    
    @Override
    public boolean emailAuth(String uuid) {
        
        Optional<MemberEntity> optionalMember = memberRepository.findByEmailAuthKey(uuid);
        if (!optionalMember.isPresent()) {
            return false;
        }
        
        MemberEntity memberEntity = optionalMember.get();
        
        if (memberEntity.isEmailAuthYn()) {
            return false;
        }
        
        memberEntity.setUserStatus(MemberEntity.MEMBER_STATUS_ING);
        memberEntity.setEmailAuthYn(true);
        memberEntity.setEmailAuthDt(LocalDateTime.now());
        memberRepository.save(memberEntity);
        
        return true;
    }
    
    @Override
    public boolean sendResetPassword(ResetPasswordInput parameter) {
    
        Optional<MemberEntity> optionalMember = memberRepository.findByUserIdAndUserName(parameter.getUserId(), parameter.getUserName());
        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }
        
        MemberEntity memberEntity = optionalMember.get();
        
        String uuid = UUID.randomUUID().toString();
        
        memberEntity.setResetPasswordKey(uuid);
        memberEntity.setResetPasswordLimitDt(LocalDateTime.now().plusDays(1));
        memberRepository.save(memberEntity);
        
        String email = parameter.getUserId();
        String subject = "[fastlms] 비밀번호 초기화 메일 입니다. ";
        String text = "<p>fastlms 비밀번호 초기화 메일 입니다.<p>" +
                "<p>아래 링크를 클릭하셔서 비밀번호를 초기화 해주세요.</p>"+
                "<div><a target='_blank' href='http://localhost:8080/member/reset/password?id=" + uuid + "'> 비밀번호 초기화 링크 </a></div>";
        mailComponents.sendMail(email, subject, text);
    
        return false;
    }
    
    @Override
    public List<MemberDto> list(MemberParam parameter) {
        
        long totalCount = memberMapper.selectListCount(parameter);
        
        List<MemberDto> list = memberMapper.selectList(parameter);
        if (!CollectionUtils.isEmpty(list)) {
            int i = 0;
            for(MemberDto x : list) {
                x.setTotalCount(totalCount);
                x.setSeq(totalCount - parameter.getPageStart() - i);
                i++;
            }
        }
        
        return list;
        
        //return memberRepository.findAll();
    }
    
    @Override
    public MemberDto detail(String userId) {
        
        Optional<MemberEntity> optionalMember  = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            return null;
        }
        
        MemberEntity memberEntity = optionalMember.get();
        
        return MemberDto.of(memberEntity);
    }
    
    @Override
    public boolean updateStatus(String userId, String userStatus) {
    
        Optional<MemberEntity> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }
    
        MemberEntity memberEntity = optionalMember.get();
        
        memberEntity.setUserStatus(userStatus);
        memberRepository.save(memberEntity);
        
        return true;
    }
    
    @Override
    public boolean updatePassword(String userId, String password) {
    
        Optional<MemberEntity> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }
    
        MemberEntity memberEntity = optionalMember.get();
        
        String encPassword = BCrypt.hashpw(password, BCrypt.gensalt());
        memberEntity.setPassword(encPassword);
        memberRepository.save(memberEntity);
    
        return true;
        
    }
    
    @Override
    public ServiceResult updateMember(MemberInput parameter) {
        
        String userId = parameter.getUserId();
    
        Optional<MemberEntity> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            return new ServiceResult(false, "회원 정보가 존재하지 않습니다.");
        }
    
        MemberEntity memberEntity = optionalMember.get();
        
        memberEntity.setPhone(parameter.getPhone());
        memberEntity.setZipcode(parameter.getZipcode());
        memberEntity.setAddr(parameter.getAddr());
        memberEntity.setAddrDetail(parameter.getAddrDetail());
        memberEntity.setUdtDt(LocalDateTime.now());
        memberRepository.save(memberEntity);
        
        return new ServiceResult();
    }
    
    @Override
    public ServiceResult updateMemberPassword(MemberInput parameter) {
    
        String userId = parameter.getUserId();
        
        Optional<MemberEntity> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            return new ServiceResult(false, "회원 정보가 존재하지 않습니다.");
        }
    
        MemberEntity memberEntity = optionalMember.get();
        
        if (!PasswordUtils.equals(parameter.getPassword(), memberEntity.getPassword())) {
            return new ServiceResult(false, "비밀번호가 일치하지 않습니다.");
        }
        
        String encPassword = PasswordUtils.encPassword(parameter.getNewPassword());
        memberEntity.setPassword(encPassword);
        memberRepository.save(memberEntity);
        
        return new ServiceResult(true);
    }
    
    @Override
    public ServiceResult withdraw(String userId, String password) {
    
        Optional<MemberEntity> optionalMember = memberRepository.findById(userId);
        if (!optionalMember.isPresent()) {
            return new ServiceResult(false, "회원 정보가 존재하지 않습니다.");
        }
    
        MemberEntity memberEntity = optionalMember.get();
        
        if (!PasswordUtils.equals(password, memberEntity.getPassword())) {
            return new ServiceResult(false, "비밀번호가 일치하지 않습니다.");
        }
    
        memberEntity.setUserName("삭제회원");
        memberEntity.setPhone("");
        memberEntity.setPassword("");
        memberEntity.setRegDt(null);
        memberEntity.setUdtDt(null);
        memberEntity.setEmailAuthYn(false);
        memberEntity.setEmailAuthDt(null);
        memberEntity.setEmailAuthKey("");
        memberEntity.setResetPasswordKey("");
        memberEntity.setResetPasswordLimitDt(null);
        memberEntity.setUserStatus(MemberCode.MEMBER_STATUS_WITHDRAW);
        memberEntity.setZipcode("");
        memberEntity.setAddr("");
        memberEntity.setAddrDetail("");
        memberRepository.save(memberEntity);
        
        return new ServiceResult();
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<MemberEntity> optionalMember = memberRepository.findById(username);
        if (!optionalMember.isPresent()) {
            throw new UsernameNotFoundException("회원 정보가 존재하지 않습니다.");
        }

        MemberEntity memberEntity = optionalMember.get();
        
        if (MemberEntity.MEMBER_STATUS_REQ.equals(memberEntity.getUserStatus())) {
            throw new MemberNotEmailAuthException("이메일 활성화 이후에 로그인을 해주세요.");
        }
        
        if (MemberEntity.MEMBER_STATUS_STOP.equals(memberEntity.getUserStatus())) {
            throw new MemberStopUserException("정지된 회원 입니다.");
        }
    
        if (MemberEntity.MEMBER_STATUS_WITHDRAW.equals(memberEntity.getUserStatus())) {
            throw new MemberStopUserException("탈퇴된 회원 입니다.");
        }

        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        if (memberEntity.isAdminYn()) {
            grantedAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        }

        return new User(memberEntity.getUserId(), memberEntity.getPassword(), grantedAuthorities);
    }
}















