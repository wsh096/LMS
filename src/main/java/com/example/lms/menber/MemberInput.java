package com.example.lms.menber;

import lombok.Data;

@Data
public class MemberInput {
    private String userId;
    private String userName;
    private String password;
    private String phone;
}
