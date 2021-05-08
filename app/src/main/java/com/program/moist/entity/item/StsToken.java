package com.program.moist.entity.item;

/**
 * Author: SilentSherlock
 * Date: 2021/5/8
 * Description: describe the class
 */
public class StsToken {
    private String accessKeyId;
    private String accessKeySecret;
    private String securityToken;

    public StsToken(String key, String secret, String token) {
        this.accessKeyId = key;
        this.accessKeySecret = secret;
        this.securityToken = token;
    }

    @Override
    public String toString() {
        return "StsToken{" +
                "accessKeyId='" + accessKeyId + '\'' +
                ", accessKeySecret='" + accessKeySecret + '\'' +
                ", securityToken='" + securityToken + '\'' +
                '}';
    }

    public StsToken(){

    }

    public String getAccessKeyId() {
        return accessKeyId;
    }

    public void setAccessKeyId(String accessKeyId) {
        this.accessKeyId = accessKeyId;
    }

    public String getAccessKeySecret() {
        return accessKeySecret;
    }

    public void setAccessKeySecret(String accessKeySecret) {
        this.accessKeySecret = accessKeySecret;
    }

    public String getSecurityToken() {
        return securityToken;
    }

    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }
}
