package com.kishore;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

/**
 * Created by battula on 10/03/17.
 */
public class Main {
//    private static final String RESULTS_BUCKET = "battula-results";
    private static final SimpleDateFormat SDF = new SimpleDateFormat("MMM dd,yyyy HH:mm:ss");
//    private static final long TEST_START_TIME = SDF.format(new Date(System.currentTimeMillis()));
    private static final long TEST_START_TIME = System.currentTimeMillis();
    public static void main(String[] args)
            throws InterruptedException {
        final int iterations = EnvironmentManager.getIterations();
        final int threads = EnvironmentManager.getThreads();
        final long[][] totalTimes = new long[threads][iterations];
        final Thread[] testUnits = new Thread[threads];
        final int[] errorsCount = new int[threads];
        final String testType = EnvironmentManager.getTestType();
        for (int i = 0; i < threads; i += 1) {
            if ("download".equals(testType)) {
                testUnits[i] = new Thread(new S3DownloadTest(iterations, totalTimes, errorsCount, i));
            } else if ("upload".equals(testType)) {
                testUnits[i] = new Thread(new AzureUploadTest(iterations, totalTimes, errorsCount, i));
            } else {
                System.exit(1);
            }
        }

        for (int i = 0; i < threads; i += 1) {
            testUnits[i].start();
        }

        for (int i = 0; i < threads; i += 1) {
            testUnits[i].join();
        }

        computeResults(iterations, threads, totalTimes, errorsCount);
        System.exit(1);

    }

    private static void computeResults(final int iterations,
                                       final int threads,
                                       final long[][] totalTimes,
                                       final int[] errorsCount) {
        final long testEndTime = System.currentTimeMillis();
        final long[] totalTimesSingleArray = new long[iterations * threads];
        long totalTime = 0;
        for (int i = 0 ; i < threads; i += 1) {
            for (int j = 0; j < iterations; j += 1) {
                totalTimesSingleArray[iterations * i + j] = totalTimes[i][j];
                totalTime += totalTimes[i][j];
            }
        }

        Arrays.sort(totalTimesSingleArray);
        final int totalErrors = sum(errorsCount);
        final int totalIterations = iterations * threads - totalErrors;
        final double avg = totalIterations == 0 ? 0: totalTime / (totalIterations * 1.0);
        // We are shifting 99% by total errors because, we are assigning 0 if it is an error, This will help to leave
        // all indexes.
        // -1 because indexes start from 0
        final int ninty_nine_percentile_index = (int) Math.round((totalIterations * 99) / 100.0) + totalErrors - 1;
        final double latency = (totalIterations / ((testEndTime - TEST_START_TIME) * 1.0)) * 1000;

        final StringBuilder response = new StringBuilder();
        response.append("Start time: " + SDF.format(new Date(TEST_START_TIME)) + "\n");
        response.append("End time: " + SDF.format(new Date(System.currentTimeMillis())) + "\n");
        response.append("Total time: " + totalTime + "\n");
        response.append("total iterations: " + totalIterations + "\n");
        response.append("ninty nine percentile index: " + ninty_nine_percentile_index + "\n");
        response.append("average: " + avg + "\n");
        response.append("99%tile: " + totalTimesSingleArray[ninty_nine_percentile_index] + "\n");
        response.append("latency: " + latency + "\n");
        response.append("Errors: " + totalErrors + "\n");
        System.out.println(response);
//        uploadResult(response.toString(), iterations, threads);
    }

    private static int sum(final int[] elements) {
        int sum = 0;
        for (final int element: elements) {
            sum += element;
        }

        return sum;
    }

//    private static void uploadResult(final String response, final int iterations, final int threads) {
//        final AmazonS3Client s3Client = new AmazonS3Client(AWSCredentialProvider.getCredentials());
//        final String fileName = EnvironmentManager.getResultBlobPrefix() + "-" + iterations + "-" + threads + "-" + TEST_START_TIME;
//        s3Client.putObject(RESULTS_BUCKET, fileName, response);
//    }
}
