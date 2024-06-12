package com.otunba.aws;

public enum Bucket {
    BUZZ_IMAGE("buzz-profile-image");
    private final String bucketName;

    Bucket(String bucketName) {
        this.bucketName = bucketName;
    }

    public String getBucketName() {
        return bucketName;
    }
}
