package webscrapper

object Properties {
  val WINDOWS_PATH =  "C:\\Users\\amarkhel\\Downloads\\spark-course-master\\spark-course-master\\spark-streaming\\src\\main\\resources\\"
  
  val WINDOWS_PATH_SHORT =  "C:\\Users\\amarkhel\\Downloads\\spark-course-master\\spark-course-master\\spark-streaming\\"
   
  val LINUX_PATH =  "/root/amarkhel/"
  
  val path:String = if(System.getProperty("os.name").startsWith("Windows")) WINDOWS_PATH else LINUX_PATH
  
  val gamePath:String = if(System.getProperty("os.name").startsWith("Windows")) WINDOWS_PATH_SHORT else LINUX_PATH
}