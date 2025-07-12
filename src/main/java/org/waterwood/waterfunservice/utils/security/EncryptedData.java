package org.waterwood.waterfunservice.utils.security;

public record EncryptedData(String displayPrefix,
                            String encryptedValue,
                            String displaySuffix) {
    public String concatDisplay(){
        if(displayPrefix == null){
            return displaySuffix;
        }
        if(displaySuffix == null){
            return displayPrefix;
        }
        return displayPrefix + displaySuffix;
    }
}
