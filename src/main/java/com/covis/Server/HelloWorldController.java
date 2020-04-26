package com.covis.Server;


import com.covis.api.HelloWorldResource;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloWorldController implements HelloWorldResource{

    public HelloWorldController(){
        //empty public contructor
    }

    public String helloWorldEndPoint(){
        return "Hello Covis";
    }

}
