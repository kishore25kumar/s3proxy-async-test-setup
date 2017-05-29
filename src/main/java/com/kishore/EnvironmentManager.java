package com.kishore;

/**
 * Created by battula on 10/03/17.
 */
public final class EnvironmentManager {
    public static String getEndpoint() {
        return System.getenv("ENDPOINT");
    }

    public static int getIterations() {
        return Integer.parseInt(System.getenv("ITERATIONS"));
    }

    public static int getThreads() {
        return Integer.parseInt(System.getenv("THREADS"));
    }

    public static String getResultBlobPrefix() {
        return System.getenv("RESULT_BLOB_PREFIX");
    }

    public static String getTestType() {
        return System.getenv("TEST_TYPE");
    }
}
