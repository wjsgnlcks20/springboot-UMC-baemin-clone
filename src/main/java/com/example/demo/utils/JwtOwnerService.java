package com.example.demo.utils;


import com.example.demo.config.BaseException;
import com.example.demo.config.secret.Secret;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class JwtOwnerService {

    /**
     * JWT 생성
     * @param userIdx
     * @return String
     */
    public String createJwt(int userIdx){
        final int expirationTimeInMill = 1*(1000*60*60*24*365);
        Date now = new Date();
        return Jwts.builder()
                .setHeaderParam("type","jwt")
                .claim("userIdx",userIdx)
                .setIssuedAt(now)
                .setExpiration(new Date(System.currentTimeMillis()+ expirationTimeInMill))
                .signWith(SignatureAlgorithm.HS256, Secret.JWT_OWNER_SECRET_KEY)
                .compact();
    }

    /**
     * Header에서 X-ACCESS-TOKEN 으로 JWT 추출
     * @return String
     */
    public String getJwt(){
        HttpServletRequest request = ((ServletRequestAttributes)RequestContextHolder.currentRequestAttributes()).getRequest();
        return request.getHeader("X-ACCESS-TOKEN");
    }

    /**
     * JWT에서 userIdx 추출
     * @return int
     * @throws BaseException
     */
    public int getUserIdx() throws BaseException{
        Jws<Claims> claims = parseJwt();
        // 3. userIdx 추출
        return claims.getBody().get("userIdx",Integer.class);  // jwt 에서 userIdx를 추출합니다.
    }

    /**
     * 유효기간 Expiration 값을 반환
     * @return Date
     * @throws BaseException
     */
    public Date getExpiration() throws BaseException {
        Jws<Claims> claims = parseJwt();
        return claims.getBody().get("exp", Date.class);
    }

    public Jws<Claims> parseJwt() throws BaseException {
        // Jwt 가져오기
        String accessToken = getJwt();
        if(accessToken == null || accessToken.length() == 0){
            throw new BaseException(EMPTY_JWT);
        }
        // Jwt parse
        Jws<Claims> claims;
        try{
            claims = Jwts.parser()
                    .setSigningKey(Secret.JWT_OWNER_SECRET_KEY)
                    .parseClaimsJws(accessToken);
        } catch(Exception ignored){
            throw new BaseException(INVALID_JWT);
        }
        return claims;
    }
}
