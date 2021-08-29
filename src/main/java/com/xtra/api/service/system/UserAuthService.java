package com.xtra.api.service.system;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.xtra.api.model.line.Line;
import com.xtra.api.model.user.Reseller;
import com.xtra.api.repository.UserRepository;
import de.taimos.totp.TOTP;
import org.apache.commons.codec.binary.Base32;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.BufferedImageHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.SecureRandom;

@EnableScheduling
@Service
public class UserAuthService {
    private static UserRepository repository;

    @Autowired
    public UserAuthService(UserRepository repository) {
        UserAuthService.repository = repository;
    }

    public static com.xtra.api.model.user.User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !(auth.getPrincipal() instanceof String)) {
            var principal = auth.getPrincipal();
            return repository.findByUsername(((User) principal).getUsername()).orElseThrow(() -> new AccessDeniedException("access denied"));
        }
        throw new AccessDeniedException("access denied");
    }

    public static Line getCurrentLine() {
        return (Line) getCurrentUser();
    }

    public static Reseller getCurrentReseller() {
        return (Reseller) getCurrentUser();
    }

    public static String getTOTPCode(String secretKey) {
        Base32 base32 = new Base32();
        byte[] bytes = base32.decode(secretKey);
        String hexKey = Hex.encodeHexString(bytes);
        return TOTP.getOTP(hexKey);
    }

    public static String getGoogleAuthenticatorBarCode(String secretKey, String account, String issuer) {
        try {
            return "otpauth://totp/"
                    + URLEncoder.encode(issuer + ":" + account, "UTF-8").replace("+", "%20")
                    + "?secret=" + URLEncoder.encode(secretKey, "UTF-8").replace("+", "%20")
                    + "&issuer=" + URLEncoder.encode(issuer, "UTF-8").replace("+", "%20");
        } catch (UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static BufferedImage createQRCode(String barCodeData, int height, int width)
            throws WriterException {
        BitMatrix matrix = new QRCodeWriter().encode(barCodeData, BarcodeFormat.QR_CODE,
                width, height);
        return MatrixToImageWriter.toBufferedImage(matrix);
    }

    public BufferedImage getQRCode() throws WriterException {
        com.xtra.api.model.user.User currentUser = getCurrentUser();
        if (currentUser.isUsing2FA())
            throw new RuntimeException("2FA is already activated.");
        if (currentUser.getEmail() == null)
            throw new RuntimeException(" User does not have the email address.");

        //Generating Secret
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[20];
        random.nextBytes(bytes);
        Base32 base32 = new Base32();
        String secret = base32.encodeToString(bytes);
        currentUser.set_2FASec(secret);
        repository.save(currentUser);

        String otpAuth = getGoogleAuthenticatorBarCode(secret, currentUser.getEmail(), "xtra");
        return createQRCode(otpAuth, 200, 200);
    }

    @Bean
    public HttpMessageConverter<BufferedImage> imageConverter() {
        return new BufferedImageHttpMessageConverter();
    }

    public HttpStatus verify2FA(long totp) {
        com.xtra.api.model.user.User currentUser = getCurrentUser();
        if (!currentUser.isUsing2FA() && getTOTPCode(currentUser.get_2FASec()).equals(String.valueOf(totp))) {
            currentUser.setUsing2FA(true);
            repository.save(currentUser);
            return HttpStatus.OK;
        } else return HttpStatus.BAD_REQUEST;
    }

    public void disable2FA() {
        com.xtra.api.model.user.User currentUser = getCurrentUser();
        currentUser.setUsing2FA(false);
        currentUser.set_2FASec(null);
        repository.save(currentUser);
    }
}
