package com.example.authorize.controller;


import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
@CrossOrigin
@RequestMapping("/netease")
public class AuthorizeController {

    @RequestMapping(value = "/authorize",method = RequestMethod.GET)
    public ModelAndView index(){return new ModelAndView("index");}

    @RequestMapping(value = "/authorize/event",method = RequestMethod.GET)
    public void getAu



}
