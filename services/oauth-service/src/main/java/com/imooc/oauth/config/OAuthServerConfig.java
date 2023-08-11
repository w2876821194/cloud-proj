package com.imooc.oauth.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.authserver.AuthorizationServerTokenServicesConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.token.store.redis.RedisTokenStore;

/**
 * 自定义授权服务器配置
 */
@Configuration
@EnableAuthorizationServer
public class OAuthServerConfig extends AuthorizationServerConfigurerAdapter {

    // 使用 Redis 连接工厂来配置 Redis 存储
    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    // 使用 Spring Security 提供的身份验证管理器
    @Autowired
    private AuthenticationManager authenticationManager;


    // Client(接入方) 定义grant type
    @Override
    public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory()
                // 接入方
                .withClient("client-a")
                // 密码
                .secret("{bcrypt}" + new BCryptPasswordEncoder().encode("WXF"))
                // 可以访问的授权类型
                .authorizedGrantTypes("client_credentials", "refresh_token", "password", "authorization_code")
                // 客户端访问范围
                .scopes("all")
                // 客户端权限
                .authorities("oauth2")
                // 重定向uri
                .redirectUris("http://www.baidu.com");
        // 配置多个client
//                .and().clients()
    }

    // 配置了授权服务器的端点
    @Override
    public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        // 使用redis对token进行存储
        endpoints.tokenStore(new RedisTokenStore(redisConnectionFactory))
                // 使用 Spring Security 提供的身份验证管理器
                .authenticationManager(authenticationManager);
//                .allowedTokenEndpointRequestMethods(HttpMethod.POST,HttpMethod.GET)
    }

    @Override
    public void configure(AuthorizationServerSecurityConfigurer security) throws Exception {
        // 启动表单认证
        security.allowFormAuthenticationForClients()
                // 开放令牌密钥访问端点
                .tokenKeyAccess("permitAll()")
                // 开放检查令牌访问端点
                .checkTokenAccess("permitAll()");
                // 只有经过身份验证的用户才能访问检查令牌访问端点
//                .checkTokenAccess("isAuthenticated()");
    }
}
