package com.example.lms.menber;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Controller
@RequestMapping("/member")
public class MemberController {
    @GetMapping("/register")
    public String register(){
        System.out.println("request get!!!!!!!!!");
        return "member/register";
    }
    @PostMapping(value ="register")
    public String registerSubmit(HttpServletRequest request,
                                 HttpServletResponse response,
                                 MemberInput memberInput){


        System.out.println(memberInput.toString());

        return "member/register";
    }

}
