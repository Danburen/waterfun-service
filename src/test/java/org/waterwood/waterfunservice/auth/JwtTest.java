package org.waterwood.waterfunservice.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.waterwood.waterfunservice.infrastructure.security.RsaJwtUtil;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
@SpringBootTest
public class JwtTest {
    @Autowired
    private RsaJwtUtil rsaJwtUtil;
    @Test
    public void testGen(){
        Map<String,Object> claims = new HashMap<>();
        claims.put("sub","user0");
        claims.put("id","1");
        String token = rsaJwtUtil.generateToken(claims, Duration.ofMinutes(10)).tokenValue();
        System.out.println(token);
    }

    @Test
    public void testParse(){
        String token = "eyJhbGciOiJSUzI1NiJ9" +
                ".eyJzdWIiOiJ1c2VyMCIsImlkIjoiMSIsImlzcyI6IndhdGVyZnVuIiwiaWF0IjoxNzYxMjMyNDEyLCJleHAiOjE3NjEyMzMwMTJ9" +
                ".pM6neneEL1yWTyYdfCVWzmzJqy0LOi1wbycNhORu8UMNuaZCxsR138EePZchiKZIZDpXtMtluAHIiviXqmoiEt0xVsplnvhPCCN16aDYxDajEd_Q-OogcpLDIzMF3HrH-oxLWVtER5y3w3WEfO9e8yvzsXIkbUze7IPBC_JnAnoLQ_91UWYIW_41cCBvpbQBA3miDUMSradCROgsrJgYZMRA-1jOcqkb0nDQEVoX1r9i956EdZTPPmict_v8iQbQLfV1N8LyEJ15Wohpbtj-QXYcnl7dXrEvBUCJmBwvYePt438x_0UrGYgvdhwGpppzZTXlkWtNhUAogWddbgpd-A";
        Map<String,Object> claims = rsaJwtUtil.parseToken(token);
        System.out.println(claims);
    }
}
