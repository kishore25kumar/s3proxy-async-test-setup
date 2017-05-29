# Steps to run test runner

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