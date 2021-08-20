package com.xtra.api.security;

import com.xtra.api.service.system.UserAuthService;
import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

class TfaTest {
    @Autowired
    UserAuthService userAuthService;

    @Test
    public void test(){
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        String result =  base32.encodeToString(bytes);
        System.out.println("Result: " + result);

        byte[] bytes1 = base32.decode(result);
        String hexKey = Hex.encodeHexString(bytes);
        String finalResult = TOTP.getOTP(hexKey);
        System.out.println("finalResult: " + finalResult);

    }
    @Test
    public void test2(){
        String secretKey = "RT6HTNXMNODHOCS5HKTOW2NFCJWWB75Z";
        String email = "test@gmail.com";
        String companyName = "My Awesome Company";
        String barCodeUrl = userAuthService.getGoogleAuthenticatorBarCode(secretKey, email, companyName);
        System.out.println(barCodeUrl);
        //otpauth://totp/My%20Awesome%20Company%3Atest%40gmail.com?secret=RT6HTNXMNODHOCS5HKTOW2NFCJWWB75Z&issuer=My%20Awesome%20Company
    }
}