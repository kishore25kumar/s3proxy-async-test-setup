package com.kishore;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;

import java.util.Map;

/**
 * Created by battula on 03/02/17.
 */
public class AWSCredentialProvider {
    private static final String AWS_ACCESS_ID = "AWS_ACCESS_ID";
    private static final String AWS_ACCESS_SECRET = "AWS_ACCESS_SECRET";

    public static AWSCredentials getCredentials() {
        Map<String, String> env = System.getenv();
        return new BasicAWSCredentials(env.getOrDefault(AWS_ACCESS_ID, "dummy"),
                                       env.getOrDefault(AWS_ACCESS_SECRET, "dummy"));
    }
}
