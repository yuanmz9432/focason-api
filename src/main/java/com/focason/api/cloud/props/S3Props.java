package com.focason.api.cloud.props;



import lombok.Data;

@Data
public class S3Props
{
    private String bucketName;
    private String prefix;
    private long preSignedUrlValidMinutes;
}
