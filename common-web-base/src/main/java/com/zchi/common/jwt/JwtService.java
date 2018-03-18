package com.zchi.common.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.jwt.Jwt;
import org.springframework.security.jwt.JwtHelper;
import org.springframework.security.jwt.crypto.sign.InvalidSignatureException;
import org.springframework.security.jwt.crypto.sign.MacSigner;

/**
 * Created by zchi on 16/7/1.
 */
public class JwtService<E> {

    public static final String JWT_HEADER_KEY = "htjc-token";
    protected Logger logger = LogManager.getLogger(getClass());
    final static byte[] HMAC_KEY;

    static {
        int[] keyInts = new int[] {3, 35, 53, 75, 43, 15, 165, 188, 131, 126, 6, 101, 119, 123, 166, 143, 90, 179, 40, 230, 240, 84, 201, 40, 169, 15, 132, 178, 210, 80, 46, 191, 211, 251, 90, 146, 210, 6, 71, 239, 150, 138, 180, 195, 119, 98, 61, 34, 61, 46, 33, 114, 5, 46, 79, 8, 192, 205, 154, 245, 103, 208, 128, 163};
        HMAC_KEY = new byte[keyInts.length];

        for (int i=0; i < keyInts.length; i++) {
            HMAC_KEY[i] = (byte)keyInts[i];
        }
    }
    static final MacSigner hmac = new MacSigner(HMAC_KEY);

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     *
     * @param claims token内需要装载的信息
     * @return jwt
     */
    public Jwt encode(E claims) {
        try {
            String jsonStr = objectMapper.writeValueAsString(claims);
            return JwtHelper.encode(jsonStr,hmac);
        } catch (JsonProcessingException e) {
            logger.error("json encode error",e);
        }
        return null;
    }

    /**
     * 将token解析
     * @param token
     * @return jwt
     */
    public Jwt decode(String token) {
        return JwtHelper.decode(token);
    }

    /**
     * 将token验证并解析
     * @param token
     * @return jwt
     * @throws InvalidSignatureException 验证失败
     */
    public Jwt decodeAndVerify(String token) throws InvalidSignatureException {
        return JwtHelper.decodeAndVerify(token,hmac);
    }


}
