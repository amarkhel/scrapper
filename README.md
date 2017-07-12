# Apache Spark with Scala training course

To run:
SPARK_HOME/bin/spark-submit --conf spark.driver.userClassPathFirst=true --class "com.supergloo.SlackStreamingApp" --master spark://MASTER:7077 ./target/scala-2.11/spark-streaming-example-assembly-1.0.jar local[5] YOUR_SLACK_KEY output
xoxp-143294799713-143904560179-144052964484-e3cc8089434dd9c939c2ae6d6682513a

spark-submit --conf spark.driver.userClassPathFirst=true --class "com.supergloo.SlackStreamingApp" --master spark://192.168.0.102:7077 C:\Users\amarkhel\Downloads\spark-course-master\spark-course-master\spark-streaming\target\scala-2.11\spark-streaming-example-assembly-1.0.jar local[5] xoxp-143294799713-143904560179-144052964484-e3cc8089434dd9c939c2ae6d6682513a output