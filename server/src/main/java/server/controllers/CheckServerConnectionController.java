package server.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test-connection")
public class CheckServerConnectionController {
    @GetMapping(path = {"", "/"})
    public ResponseEntity<String> testConnection() {
        return ResponseEntity.ok("Success");
    }
}
