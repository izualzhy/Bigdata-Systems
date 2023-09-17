package cn.izualzhy.springmore.controller;


import cn.izualzhy.springmore.pojo.User;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/status")
public class HttpStatusController {
    @GetMapping("/user")
    HttpEntity<User> userById(Long id) {
        User user = new User(id, "name_" + id, "note_" + id);
        HttpHeaders headers = new HttpHeaders();
        headers.add("sample_header_key", "sample_header_val");

        return new ResponseEntity<>(user, headers, HttpStatus.CREATED);
    }
}
