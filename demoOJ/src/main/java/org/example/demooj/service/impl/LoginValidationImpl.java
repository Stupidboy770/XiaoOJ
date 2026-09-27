package org.example.demooj.service.impl;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Resource;
import org.example.demooj.entity.User;
import org.example.demooj.mapper.UserMapper;
import org.example.demooj.service.LoginValidation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class LoginValidationImpl implements LoginValidation {
    @Value("${secretKey}")
    private String secretKey;
    @Value("${expireTime}")
    private long expireTime;
    @Resource
    private UserMapper userMapper;

    @Override
    public String loginUser(String userId, String password) {
        User user=userMapper.getUserById(userId);
        if(user==null)return null;
        if(matchPassword(password,user.getPassword()))
            return generateToken(user.getName(),user.getUserId());
        return null;
    }

    @Override
    public boolean isAdmin(String userId) {
        User user=userMapper.getUserById(userId);
        if(user==null)return false;
        return user.isAdmin();
    }

    @Override
    public Integer getDisAdmin() {
        return userMapper.getInteger();
    }

    private SecretKey getSecretKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private String generateToken(String name,String userId){

        Map<String,Object> claims = new HashMap<>();

        claims.put("user",name);
        claims.put("userId",userId);

        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(new Date(System.currentTimeMillis()+expireTime))
                .signWith(getSecretKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private static final Pattern OLD_MD5_PATTERN = Pattern.compile("^[0-9a-fA-F]{32}$");
    private static final int SALT_LENGTH = 4;   // 盐字节数
    private static final int HASH_LENGTH = 20;  // SHA-1 输出长度

    private boolean matchPassword(String rawPassword,String storedHash){
        if (rawPassword == null || storedHash == null || storedHash.isEmpty()) {
            return false;
        }

        // 1. 检测是否为旧 MD5 格式（32位十六进制）
        if (OLD_MD5_PATTERN.matcher(storedHash).matches()) {
            String md5OfInput = md5Hex(rawPassword);
            return md5OfInput.equalsIgnoreCase(storedHash);
        }

        // 2. 新格式：Base64 解码
        byte[] decoded;
        try {
            decoded = Base64.getDecoder().decode(storedHash);
        } catch (IllegalArgumentException e) {
            return false; // 无效 Base64
        }
        // 新格式应为 24 字节：20字节SHA1 + 4字节盐
        if (decoded.length != HASH_LENGTH + SALT_LENGTH) {
            return false;
        }

        // 提取盐（后4字节）
        byte[] saltBytes = new byte[SALT_LENGTH];
        System.arraycopy(decoded, HASH_LENGTH, saltBytes, 0, SALT_LENGTH);
        String salt = new String(saltBytes, StandardCharsets.US_ASCII);

        // 3. 用同样方法重新计算哈希
        String md5Hex = md5Hex(rawPassword);
        String combined = md5Hex + salt;
        byte[] sha1Bytes = sha1Binary(combined);

        // 构建期望的编码结果
        byte[] expected = new byte[HASH_LENGTH + SALT_LENGTH];
        System.arraycopy(sha1Bytes, 0, expected, 0, HASH_LENGTH);
        System.arraycopy(saltBytes, 0, expected, HASH_LENGTH, SALT_LENGTH);
        String expectedHash = Base64.getEncoder().encodeToString(expected);

        // 恒定时间比较（防止时序攻击）
        return MessageDigest.isEqual(expectedHash.getBytes(StandardCharsets.UTF_8),
                storedHash.getBytes(StandardCharsets.UTF_8));
    }

    private static String md5Hex(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder(32);
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 algorithm not available", e);
        }
    }

    private static byte[] sha1Binary(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            return md.digest(input.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-1 algorithm not available", e);
        }
    }
}
