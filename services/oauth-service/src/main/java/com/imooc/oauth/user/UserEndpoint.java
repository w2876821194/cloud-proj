package com.imooc.oauth.user;


import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@Slf4j
@RestController
public class UserEndpoint {
    @RequestMapping
    public Principal getUser(Principal principal) {
        log.info("principal={}", principal);
        return principal;
    }
}
