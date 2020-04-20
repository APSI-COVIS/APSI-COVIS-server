package com.covis.Server;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController {

    public HelloWorldController(){
        //empty public contructor
    }

    @GetMapping("/helloworld")
    public String helloWorldEndPoint(){
        return "Hello Covis";
    }

}
