package org.ideaprojects.researchassistant;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/api/research")
@RestController
@CrossOrigin(origins = "*")
public class ResearchController {

    private final ResearchService researchService;


    public ResearchController(ResearchService researchService) {
        this.researchService = researchService;
    }

    @PostMapping("/process")
    public ResponseEntity<String> processContent(@RequestBody ResearchRequest request){

        String result=researchService.processContent(request);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}
