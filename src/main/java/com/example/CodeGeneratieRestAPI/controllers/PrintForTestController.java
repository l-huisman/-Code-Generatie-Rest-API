package com.example.CodeGeneratieRestAPI.controllers;
import org.springframework.stereotype.Controller;

@Controller
public class PrintForTestController {
    public static void print(String input){
        System.out.println(input);
    }
}