package com.kishore;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.S3ClientOptions;
import com.amazonaws.services.s3.model.AmazonS3Exception;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;

import java.io.IOException;

/**
 * Created by battula on 18/05/17.
 */
public class S3DownloadTest implements Runnable {
    private final int iterations;
    private final long[][] totalTimes;
    private final int unitNumber;
    private final int[] errorsCount;
    S3DownloadTest(final int iterations, final long[][] totalTimes, final int[] errorsCount, final int unitNumber) {
        this.iterations = iterations;
        this.totalTimes = totalTimes;
        this.unitNumber = unitNumber;
        this.errorsCount = errorsCount;
        this.errorsCount[unitNumber] = 0;
    }

    private String getBucketName() {
        return System.getenv("BUCKET_NAME");
    }

    public void run() {
        System.out.println("Version 1");
        final ClientConfiguration clientConfiguration = new ClientConfiguration();
        clientConfiguration.setRequestTimeout(60 * 60 * 1000);
        final AmazonS3Client s3Client = new AmazonS3Client(AWSCredentialProvider.getCredentials());
        s3Client.setEndpoint(EnvironmentManager.getEndpoint());
        s3Client.setS3ClientOptions(S3ClientOptions.builder().setPathStyleAccess(true).build());
        final String bucketName = this.getBucketName();
        int length = 0;
        for (int i = 0; i < iterations; i += 1) {
            final String fileName = Integer.toString(((unitNumber) * iterations + i));
            final long startTime = System.currentTimeMillis();
            try {
                final S3Object s3Object = s3Client.getObject(bucketName, fileName);
                final S3ObjectInputStream is = s3Object.getObjectContent();
                final byte[] bytes = new byte[1024];
                while ((length = is.read(bytes)) > 0) {
                    assert length == 1024;
                }
                final long endTime = System.currentTimeMillis();
                totalTimes[unitNumber][i] = endTime - startTime;
            } catch (final AmazonS3Exception | IOException s3Exception) {
                this.errorsCount[this.unitNumber] = this.errorsCount[this.unitNumber] + 1;
                totalTimes[unitNumber][i] = 0;
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}
