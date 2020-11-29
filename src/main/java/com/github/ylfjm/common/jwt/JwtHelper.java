package com.github.ylfjm.common.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.ylfjm.common.JwtTokenException;
import com.github.ylfjm.common.utils.DateUtil;
import com.github.ylfjm.common.utils.RSAUtil;
import com.github.ylfjm.common.utils.UUIDUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Date;

/**
 * @author YLFJM
 * @date 2019/9/29
 */
@Slf4j
public class JwtHelper {

    private static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJbbtCb2h51RBGO67Z9PQuFZzqvKcN1/+3HA4QyKOjc/dsIb7Qdw0NY0n7qnLBfOflu06XQB7tHyzd/lbrEtFfI0WVGPiV+B8T9ksSCESHHZcJ8n6tKRxehRPCT8foIfYnxuaDA/9O8X37zUk/RjhJUUkEs9OO80ub82RNr0JqyVAgMBAAECgYBqhic24Bww3NWRqpxCQxDB2c2TBvcdoVKnJ4CCf1TOUFUGifucpnAA4dXsUkFTai9ViHDUxsyhmacAM50vJNv6Pptaya6ivDTWMzADpK0Sem/sVqpaX5ej9LKWXc0gSlvalCkt6VQIefrvNAV3jC82DvLL0PHnmaXuF1iMyzusvQJBAMkErWT9pmS9RPj3DN6OcM5MU2DFrHTs5GexNhDCREDe7/FxP8FPmkBAT5H10B7/X/+8m9+GbcrzxsVZJP6vSOMCQQDAHtBLQ8GYaXmazscv5J2Q49V9z3LJAvjE5uJfUPSUb3DOcJosCzEEgDb7Pct7hRb/euY0iTIdo+rhgKaqEcYnAkABMXLqB4tmS61OIQFFjOjkgJV6HJJCe2l7qBSfEtge3nPkJfzv484RlZAjWKcsl+108iSmtgCO1NWYNEhA7r9rAkEAtmNKaXmMq+3ONWqC7PPDpk61Wjf8B9rbYsqyM7z+RIC7fjCmHJQPV++EwuW6bKw/1hDNxShOPJdmH+jr0F8JVwJAKy2g7kMSduU4QS42Hl6Pz1XtDMCFCv6CeuuM06S42VBZGwf6ZYm88RORi0fqp8To2ImTPrev2wJi1fa6E7BtWA==";
    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCW27Qm9oedUQRjuu2fT0LhWc6rynDdf/txwOEMijo3P3bCG+0HcNDWNJ+6pywXzn5btOl0Ae7R8s3f5W6xLRXyNFlRj4lfgfE/ZLEghEhx2XCfJ+rSkcXoUTwk/H6CH2J8bmgwP/TvF9+81JP0Y4SVFJBLPTjvNLm/NkTa9CaslQIDAQAB";

    private static ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 获取可用的token。
     * 当前token可用，返回当前token；当前token不可用，返回一个新token
     */
    public static String getGoodToken(String token) {
        Claims claims = getClaimFromToken(token);
        //校验当前token能否使用，不能使用则生成新token
        boolean flag = checkToken(claims);
        if (flag) {
            return null;
        } else {
            String subject = claims.getSubject();
            JWTInfo jwt;
            try {
                jwt = objectMapper.readValue(subject, JWTInfo.class);
            } catch (IOException ex) {
                throw new JwtTokenException("非法登录【0008】");
            }
            return createJWTToken(jwt);
        }
    }

    /**
     * 检查当前token是否还能继续使用
     * true：可以  false：不建议
     */
    private static boolean checkToken(Claims claims) {
        //颁发token时间
        Date issuedAt = claims.getIssuedAt();
        Date now = new Date();
        //获取JWT颁发时间10分钟后的时间
        Date date = DateUtil.JWTTokenExpirationTime(issuedAt, 10 * 60);
        if (now.after(date)) {
            //如果当前时间在JWT颁发时间10分钟后，需要重新颁发token
            return false;
        }
        return true;
    }

    /**
     * 创建新的jwt token
     */
    public static String createJWTToken(JWTInfo jwt) {
        //过期时间（单位：秒）
        // int expire = jwt.getType() == SystemType.SYSTEM.getValue() ? 30 * 60 : -1;
        int expire = 2 * 60 * 60;
        //jwt的颁发时间
        Date now = new Date();
        //JWT过期时间，设置在凌晨，使用概率比较低的时间点，避免用户正在使用token过期，数据丢失
        Date expireDate = DateUtil.JWTTokenExpirationTime(now, expire);
        String subject;
        try {
            subject = objectMapper.writeValueAsString(jwt);
        } catch (JsonProcessingException ex) {
            throw new JwtTokenException("非法登录【0001】");
        }
        JwtBuilder builder = Jwts.builder()
                //如果有私有声明，一定要先设置这个自己创建的私有的声明，这个是给builder的claim赋值，一旦写在标准的声明赋值之后，就是覆盖了那些标准的声明的
                // .setClaims(claims)
                //设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                .setId(UUIDUtil.longuuid32())
                .setIssuedAt(now)
                //sub(Subject)：代表这个JWT的主体，即它的所有人，这个是一个json格式的字符串，可以存放什么userId，roldid之类的，作为用户的唯一标志。
                .setSubject(subject)
                .signWith(SignatureAlgorithm.RS256, RSAUtil.loadPrivateKey(PRIVATE_KEY))
                .setExpiration(expireDate);

        return builder.compact();
    }

    /**
     * 获取jwt token中的用户信息
     */
    public static String getJWTInfoFromToken(String token) {
        Claims claims = getClaimFromToken(token);
        return claims.getSubject();
    }

    /**
     * 获取jwt的payload部分
     */
    private static Claims getClaimFromToken(String token) {
        try {
            return Jwts.parser().setSigningKey(RSAUtil.loadPublicKey(PUBLIC_KEY)).parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException ex) {
            throw new JwtTokenException("登录超时，请重新登录");
        } catch (UnsupportedJwtException ex) {
            throw new JwtTokenException("非法登录【0003】");
        } catch (MalformedJwtException ex) {
            throw new JwtTokenException("非法登录【0004】");
        } catch (SignatureException ex) {
            throw new JwtTokenException("非法登录【0005】");
        } catch (IllegalArgumentException ex) {
            throw new JwtTokenException("非法登录【0006】");
        }
    }

}
