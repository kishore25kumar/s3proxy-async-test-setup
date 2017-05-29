# Steps to run test runner

## Jars explanation
You will find 3 jars in jars folder.

**test-runner.jar** - This is used for downloading files in parallel using multiple threads. This is also used for populating the files for testing. The source code for the jar exists in the current repo.

**outputstream-s3proxy.jar** - This is s3 proxy with output stream implementation. s3proxy git repo: https://github.com/kishore25kumar/s3proxy/tree/async-output-stream.


**httpasync-s3proxy.jar** - This is s3 proxy with http async library implementation. s3proxy git repo: https://github.com/kishore25kumar/s3proxy/tree/async-http-lib. Jclouds repo: https://github.com/SpandanThakur/jclouds/tree/multipart-api 

### Prerequisites:
Data has to be populated into azure bucket. This can be done through test runner.

Please set the following environment variables before running the test runner

```
ITERATIONS=100000 (This will populate 100000 files in azure)
THREADS=1 (Keep it 1 only as we try to create files names using 0, 1, 2 ... iterations)
FILE_SIZE=1 (File size in MB)
BUCKET_NAME=test (Bucket where files need to be updated)
TEST_TYPE=upload
AZURE_ACCESS_ID=<AZURE ACCESS ID>
AZURE_ACCESS_SECRET=<AZURE_ACCESS_SECRET>
```
Once the above environment variables are set then run the following command
```
java -jar jars/test-runner.jar
```

### Running the bench mark tests to download file against s3proxy

Both implementations of s3proxy jars are present in jars folder. For running test runner in both cases is same.

Please set the following environment variables before running the test runner.
```
ITERATIONS=10000 (Each thread will download these many files)
THREADS=10 (Test runner parallely will spawn 10 threads and will start giving requests)
BUCKET_NAME=test (Where the files exists)
ENDPOINT=http://127.0.0.1:8080/ (S3 proxy endpoint)
TEST_TYPE=download
```

Once the above environment variables are set then run the following command
```
java -jar jars/test-runner.jar
```
This will download all the files from 0 ... 100,000. Make sure the proxy is running.

### Running s3 proxy
For both implementations the the instructions are same.

Please set the following env before running the proxy.
```
JETTY_THREADS=6
```
This is the minimum threads we need to set in order to start the jetty server. This is equivalent to 1 worker thread as jetty requires 1 acceptor + 4 selectors by default. If you set this value less than 6 then you will get an exception.

Please use the following jvm arguments for s3proxy
```
-DLOG_LEVEL=ERROR -Ds3proxy.endpoint=http://127.0.0.1:8080 -Ds3proxy.authorization=none -Djclouds.provider=azureblob -Djclouds.identity=<Azure creds> -Djclouds.credential=<Azure creds> -Djclouds.version=2015-12-11 -Djclouds.so-timeout=0 -Djclouds.connection-timeout=0
```

In the above jvm arguments Please modify the jclouds.identity and jclouds.credentials to proper azure credentials.

Then run the following command
```
java -jar <proxy-pefix>-s3proxy.jar <JVM arguments> --properties /dev/null
```
proxy-prefix will be either "outputstream" or "httpasync".

### Test run results
At the end of test run you will see output something similar as below

```
Start time: May 29,2017 11:43:13
End time: May 29,2017 11:43:26
Total time: 12547
total iterations: 10
ninty nine percentile index: 9
average: 1254.7
99%tile: 5645
latency: 0.7672830507174097
Errors: 0
```

### Note

* I ran both the test runner and s3proxy in the same VM and same region as storage account.
* It will take around 30-40 minutes for http async library implementation and 60 minutes for output stream implementation.