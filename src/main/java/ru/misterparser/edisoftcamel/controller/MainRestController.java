package ru.misterparser.edisoftcamel.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import ru.misterparser.edisoftcamel.service.SourceService;

import java.util.List;

@RestController
@Slf4j
public class MainRestController {

    private SourceService sourceService;

    public MainRestController(SourceService sourceService) {
        this.sourceService = sourceService;
    }

    @GetMapping(path = "/source/getAllOrderNumbers", produces = MediaType.APPLICATION_JSON_VALUE)
    public List<String> getAllOrderNumbers() {
        log.debug("getAllOrderNumbers request....");
        return sourceService.getAllOrderNumbers();
    }

    @GetMapping(path = "/source/getSourceByOrderNumber/{orderNumber}", produces = MediaType.APPLICATION_XML_VALUE)
    public String getSourceByOrderNumber(@PathVariable String orderNumber) throws SourceNotFoundException {
        log.debug("getSourceByOrderNumber request....");
        return sourceService.getSourceByOrderNumber(orderNumber).orElseThrow(SourceNotFoundException::new);
    }

    @ExceptionHandler(SourceNotFoundException.class)
    public ResponseEntity handleSourceNotFoundException() {
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }

    private class SourceNotFoundException extends Exception {
    }
}
