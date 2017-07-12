package webscrapper

import org.jsoup.Jsoup
import java.net.URL
import java.io.IOException
import java.io.InputStream
import org.jsoup.nodes.Document
import scala.util.Try
import scala.util.Success
import scala.util.Failure
import java.io.File

object GameSource {

  private val UTF8 = "utf-8"

  def fromUrl(implicit url: String) : Option[Document] = {
    Some(load(Jsoup.connect(url).header("Accept-Encoding", "gzip, deflate")
    .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
    .maxBodySize(0).timeout(100000).validateTLSCertificates(false).get()))
  }

  def fromResource(implicit file: String) : Option[Document] = {
    Some(load(
      using(classLoader.getResourceAsStream(file)){
        val d = classLoader.getResourceAsStream(file)
        Jsoup.parse(_, UTF8, file)
    }))
  }
  
  def fromFile(file: File) : Option[Document] = {
    implicit val s =""
    Some(load(
        Jsoup.parse(file, UTF8)
      ))
  }

  private def load(operation: => Document)(implicit param:String) = {
    require(param != null)
    Try(operation) match {
      case Success(value) => value
      case Failure(e) => {
        e.printStackTrace()
        throw new IllegalArgumentException("game source not found")
      }
    }
  }

  private def using[A <: { def close(): Unit }, B](param: A)(f: A => B): B =
    try {
      f(param)
    } finally {
      param.close
    }
    
  private def classLoader = Thread.currentThread.getContextClassLoader
}