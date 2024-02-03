package cn.izualzhy.blog

import cn.izualzhy.JustTest.i

import java.nio.file.{Files, Paths}
import scala.collection.JavaConverters._
import scala.io.Source

case class PostMeta(url: String, title: String, date: String, tags: String) {
  def toArray: Array[AnyRef] = Array(url, title, date, tags)
}

object BlogPostMetaExtractor {

  def extractMetaInformation(filePath: String): Seq[PostMeta] = {
    val dir = Paths.get(filePath)
    if (!Files.isDirectory(dir)) {
      Seq.empty
    } else {
      val paths = Files.walk(dir).iterator().asScala.toSeq

      paths.filter{i => {
          Files.isRegularFile(i) && i.getFileName.toString.endsWith(".markdown")
        }}
        .flatMap { path =>
          try {
            val fileName = path.getFileName.toString
            val url = fileName.substring(11, fileName.length - ".markdown".length)

            val source = Source.fromFile(path.toFile)

            val lines = source.getLines().dropWhile(_ != "---").drop(1).takeWhile(_ != "---") // Drop the first "---" and stop at the second "---"
            source.close()

            val title = lines.find(_.startsWith("title"))
              .map(i => i.substring(i.indexOf(":") + 1).trim.stripPrefix("\"").stripSuffix("\""))
              .head

            val date = lines.find(_.startsWith("date"))
              .map(i => i.substring(i.indexOf(":") + 1).trim.stripPrefix("\"").stripSuffix("\""))
              .head

            val tags = lines.find(_.startsWith("tags:"))
              .map(i => i.substring(i.indexOf(":") + 1).trim.stripPrefix("\"").stripSuffix("\""))
              .head

            Some(PostMeta(url, title, date, tags))
          } catch {
            case e: Exception => {
              println(s"error with path:${path} e:${e}")
              throw e
            }
          }
        }
    }
  }

  def main(args: Array[String]): Unit = {
    val metaInfos = extractMetaInformation("path/to/your/_posts")
    metaInfos.foreach { meta =>
      println(s"URL: ${meta.url}, Title: ${meta.title}, Date: ${meta.date}, Tags: ${meta.tags.mkString(", ")}")
    }
  }
}
