package com.otunba.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.otunba.exceptions.ApiException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class FileStore {
    private final AmazonS3 s3Client;

    public String save(String path, String filename, Optional<Map<String, String>> metadata, InputStream inputStream) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        metadata.ifPresent(map -> {
            if (!map.isEmpty())
                map.forEach(objectMetadata::addUserMetadata);
        });
        try {
            s3Client.putObject(path, filename, inputStream, objectMetadata);
        } catch (AmazonS3Exception e) {
            throw new ApiException("Unable to save image");
        }
        return "Image uploaded Successfully";
    }

    public byte[] download(String path, String key){
        try {
            S3Object s3Object = s3Client.getObject(path, key);
            S3ObjectInputStream inputStream = s3Object.getObjectContent();
            return IOUtils.toByteArray(inputStream);
        }catch (AmazonS3Exception | IOException e){
            throw new IllegalStateException("Unable to download image");
        }
    }
}
