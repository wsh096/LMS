package com.example.lms;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MainPage {
    @GetMapping("/")
    public String index(){
        return "Index Page";
    }
    @GetMapping("/hello")
    public String hello(){
        return "<html>" +
                "<head>" +
                "</head>" +
                "<body>" +
                "<p>hello</p> <p> fastlms website!!!</p>" +
                "</body>" +
                "</html>";
    }
}
