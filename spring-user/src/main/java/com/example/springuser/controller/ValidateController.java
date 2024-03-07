package com.example.springuser.controller;

import com.example.springuser.dto.ValidateTestRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ValidateController {

    @PostMapping("/validation")
    @ResponseBody
    public ValidateTestRequest testValid(@RequestBody @Valid ValidateTestRequest requestDto) {
        return requestDto;
    }
}
