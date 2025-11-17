package org.waterwood.waterfunservice.infrastructure.utils.security;

import org.waterwood.waterfunservice.infrastructure.security.EncryptionDataKey;

public class PartialEncryptionHelper {
    private static final int PHONE_PREFIX_LENGTH = 3;
    private static final int EMAIL_PREFIX_LENGTH = 1;

    public static EncryptedData encryptPhone(String phone, EncryptionDataKey dekKey){
        if (phone == null || phone.length() <= PHONE_PREFIX_LENGTH) {
            return new EncryptedData(null,phone,null);
        }
        String prefix = phone.substring(0, PHONE_PREFIX_LENGTH);
        String toEncrypt = phone.substring(PHONE_PREFIX_LENGTH);
        String encryptedPart = EncryptionHelper.encryptField(toEncrypt, dekKey);
        return new EncryptedData(prefix,encryptedPart,null); // prefix + base64
    }

    public static EncryptedData encryptEmail(String email, EncryptionDataKey dekKey){
        if (email == null || !email.contains("@")) {
            return new EncryptedData(null,email,null);
        }
        int atIndex = email.indexOf("@");
        String firstChar = email.substring(0, EMAIL_PREFIX_LENGTH);
        String toEncrypt = email.substring(EMAIL_PREFIX_LENGTH, atIndex);
        String suffix = email.substring(atIndex);

        String encryptedPart = EncryptionHelper.encryptField(toEncrypt, dekKey);
        return new EncryptedData(firstChar,encryptedPart,suffix); // prefix + base64 @ suffix
    }

    public static String maskPhone(String phone) {
        if (phone == null || phone.length() <= PHONE_PREFIX_LENGTH + 3) {
            return phone;
        }
        String prefix = phone.substring(0, PHONE_PREFIX_LENGTH);
        String suffix = phone.substring(phone.length() - 3);
        return prefix + "****" + suffix;
    }

    public static String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= EMAIL_PREFIX_LENGTH) {
            return email;
        }
        String firstChar = email.substring(0, EMAIL_PREFIX_LENGTH);
        String suffix = email.substring(atIndex);
        return firstChar + "****" + suffix;
    }

    public static String getPhonePrefix(String phone){
        return phone.substring(0, PHONE_PREFIX_LENGTH);
    }

    public static String getEmailDisplay(String email){
        int atIndex = email.indexOf("@");
        String prefix = email.substring(0, EMAIL_PREFIX_LENGTH);
        String suffix = email.substring(atIndex);
        return prefix + suffix;
    }

    public static int getPhonePrefixLength(){
        return PHONE_PREFIX_LENGTH;
    }
    public static int getEmailPrefixLength(){
        return EMAIL_PREFIX_LENGTH;
    }
}
