//package com.imooc.restroom.config;
//
//import lombok.Data;
//import org.bouncycastle.util.Strings;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.stereotype.Component;
//
//import javax.management.remote.JMXAuthenticator;
//import javax.security.auth.Subject;
//
//@Component("jmxAuthenticator")
//@ConfigurationProperties(prefix = "my")
//@Data
//public class MyCustomJMXAuthenticator implements JMXAuthenticator {
//
//    private String username;
//
//    private String password;
//
//    @Override
//    public Subject authenticate(Object credentials) {
//        if (credentials instanceof String[]) {
//            String[] auth = (String[]) credentials;
//            String username = auth[0];
//            String password = auth[1];
//
//            // 进行自定义的身份验证逻辑
//            if (isValidUsernameAndPassword(username, password)) {
//                // 身份验证成功，返回Subject对象
//                return createSubject(username);
//            } else {
//                // 身份验证失败，返回null
//                return null;
//            }
//        }
//        // 如果凭证类型不匹配，也返回null
//        return null;
//    }
//
//    private boolean isValidUsernameAndPassword(String username, String password) {
//        // 自定义身份验证逻辑，根据需要进行用户名和密码的校验
//        return Strings.constantTimeAreEqual(this.username, username)
//                && Strings.constantTimeAreEqual(this.password, password);
//    }
//
//    private Subject createSubject(String username) {
//        // 根据需要创建Subject对象
//        // 这里只作为示例，仅创建一个仅包含用户名的Subject对象
//        Subject subject = new Subject();
//        subject.getPrincipals().add(new UserPrincipal(username));
//        return subject;
//    }
//
//    // 自定义Principal类，用于存储用户信息
//    private static class UserPrincipal implements java.security.Principal {
//        private final String name;
//
//        public UserPrincipal(String name) {
//            this.name = name;
//        }
//
//        @Override
//        public String getName() {
//            return name;
//        }
//    }
//}
