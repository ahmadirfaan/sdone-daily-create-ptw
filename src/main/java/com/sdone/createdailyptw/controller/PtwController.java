package com.sdone.createdailyptw.controller;

import com.sdone.createdailyptw.model.CreateDailyPtw;
import com.sdone.createdailyptw.model.FieldConstant;
import com.sdone.createdailyptw.service.PtwService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@CrossOrigin
public class PtwController {

    @Autowired
    private PtwService ptwService;

    @PostMapping("/v1/ptw/daily/create")
    public ResponseEntity<Map<String, Object>> getPtwList(@RequestBody @Valid CreateDailyPtw createDailyPtw) {
        var result = ptwService.createDailyPtw(createDailyPtw);
        var httpStatus = (Integer) result.get(FieldConstant.HTTP_STATUS);
        result.remove(FieldConstant.HTTP_STATUS);
        return ResponseEntity.status(httpStatus).body(result);
    }
}
