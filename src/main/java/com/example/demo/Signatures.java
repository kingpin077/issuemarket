package com.example.demo;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.GeneralSecurityException;
import java.security.SignatureException;

public class Signatures {

    // 암호화 알고리즘 제공자를 설정하는 상수
    private static final String PROVIDER = "BC";

    // 사용할 해시 알고리즘 HMAC-SHA256의 상수 정의
    private static final String HMAC_SHA256 = "HmacSHA256";

    // 주어진 데이터를 HMAC-SHA256 알고리즘으로 서명 생성하는 메서드 (timestamp, HTTP method, 리소스를 사용)
    public static String of(String timestamp, String method, String resource, String key) throws SignatureException {

        return of(timestamp + "." + method + "." + resource, key);
    }

    // 주어진 데이터와 키를 사용하여 서명을 생성하는 메서드
    public static String of(String data, String key) throws SignatureException {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            System.out.println("Hmac Hash256.");
            mac.init(new SecretKeySpec(key.getBytes(), "HmacSHA256"));      // 시크릿 키를 사용하여 HMAC 초기화

            return DatatypeConverter.printBase64Binary(mac.doFinal(data.getBytes()));       // 데이터를 HMAC 해시 처리 후 Base64로 인코딩하여 반환
        } catch (GeneralSecurityException e) {
            throw new SignatureException("Failed to generate signature.", e);   // 보안 관련 예외 발생 시 SignatureException으로 래핑하여 처리
        }
    }
}
