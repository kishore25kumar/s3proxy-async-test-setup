package com.kishore;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.StorageException;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;

/**
 * Created by battula on 17/05/17.
 */
public class AzureUploadTest implements Runnable {
    private final int iterations;
    private final long[][] totalTimes;
    private final int unitNumber;
    private final int[] errorsCount;
    AzureUploadTest(final int iterations, final long[][] totalTimes, final int[] errorsCount, final int unitNumber) {
        this.iterations = iterations;
        this.totalTimes = totalTimes;
        this.unitNumber = unitNumber;
        this.errorsCount = errorsCount;
        this.errorsCount[unitNumber] = 0;
    }

    private int getFileSizeInBytes() {
        return Integer.parseInt(System.getenv("FILE_SIZE")) * 1024 * 1024;
    }

    private String getBucketName() {
        return System.getenv("BUCKET_NAME");
    }

    private String getAccessId() {
        return System.getenv("AZURE_ACCESS_ID");
    }

    private String getAccessKey() {
        return System.getenv("AZURE_ACCESS_SECRET");
    }

    public void run() {
        final String storageConnectionString =
                "DefaultEndpointsProtocol=https;" +
                        "AccountName=" + this.getAccessId() + ";" +
                        "AccountKey=" + this.getAccessKey();
        CloudStorageAccount storageAccount = null;
        try {
            storageAccount = CloudStorageAccount.parse(storageConnectionString);
        } catch (URISyntaxException | InvalidKeyException e) {
            System.exit(1);
        }

        CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
        CloudBlobContainer blobContainer = null;
        try {
            blobContainer = blobClient.getContainerReference(this.getBucketName());
            blobContainer.createIfNotExists();
        } catch (URISyntaxException | StorageException e) {
            System.exit(1);
        }
        byte[] bytes = new byte[getFileSizeInBytes()];
        for (int i = 0; i < iterations; i += 1) {
            final long startTime = System.currentTimeMillis();
            try {
                CloudBlockBlob blob = blobContainer.getBlockBlobReference(Integer.toString(i));
                blob.upload(new ByteArrayInputStream(bytes), bytes.length);
                final long endTime = System.currentTimeMillis();
                totalTimes[unitNumber][i] = endTime - startTime;
            } catch (StorageException | URISyntaxException | IOException e) {
                this.errorsCount[this.unitNumber] = this.errorsCount[this.unitNumber] + 1;
                totalTimes[unitNumber][i] = 0;
                try {
                    Thread.sleep(10 * 1000);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
            }
        }

    }
}
