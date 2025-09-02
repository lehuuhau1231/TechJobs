package com.lhh.techjobs.controller.web;

import com.lhh.techjobs.service.BillService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class WebJobReview {
    BillService billService;

    @GetMapping("/job-review")
    public String jobReview(Model model) {
        model.addAttribute("bill", billService.getBillPending());
        return "jobReview";
    }
}
