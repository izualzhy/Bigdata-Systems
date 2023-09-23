package cn.izualzhy

import com.typesafe.config.ConfigFactory
import scala.collection.JavaConverters._


object configParserSampler extends App {
  val configStr =
    """
      |service {
      |  host = "127.0.0.1"
      |  port = 8001
      |}
      |db {
      |   dev {
      |      url = "dev.url"
      |   }
      |   online {
      |      url = "online.url"
      |   }
      |}
      |models {
      |   kv-model {
      |      version-1 {
      |         k = "hello"
      |      }
      |      version-2 {
      |         k = "world"
      |      }
      |   }
      |}
      |resolve {
      |       "emr-header-2.cluster-123456" = 1.3.2.5
      |       "emr-header-1.cluster-123456" = 1.3.2.4
      |       "emr-header-2.cluster-121" = 1.3.3.4
      |       "emr-header-1.cluster-121" = 1.3.3.5
      |}""".stripMargin
  val config = ConfigFactory.parseString(configStr)
  println(config.getString("service.host"))
  println(config.getConfig("service").entrySet().asScala.map(entry =>
    entry.getKey.trim.stripSuffix("\"").stripPrefix("\"") -> entry.getValue.render().trim.stripSuffix("\"").stripPrefix("\"")).toMap)

  val modelConfig = config.getConfig("models")
  println(modelConfig.getConfig("kv-model").getConfig("version-1").getString("k"))
  println(config.getString("models.kv-model.version-1.k"))

  // 获取 models.kv-model 对象
  val kvModelConfig = config.getConfig("models.kv-model")
  kvModelConfig.root().entrySet().asScala.foreach(entry => {
    println(s"key:${entry.getKey} value:${entry.getValue.render()}")
  })

  val resolveConfig = config.getConfig("resolve")
  resolveConfig.root().entrySet().asScala.foreach(entry => {
    println(s"key:${entry.getKey} value:${entry.getValue}")
  })

  val x = resolveConfig.entrySet().asScala.map(entry =>
    entry.getKey.trim.stripSuffix("\"").stripPrefix("\"") -> entry.getValue.render().trim.stripSuffix("\"").stripPrefix("\"")).toMap
  println(s"x:${x}")

  var url = "http://emr-header-1.cluster-123456:20888/proxy/application_1651489943447_35672/"
  println(s"url:${url}")
  x.foreach(entry => {
    println(s"${entry._1} ${entry._2}")
    url = url.replace(entry._1, entry._2)
  })
  println(s"url:${url}")

  val urlV2 = "http://emr-header-1.cluster-123456:20888/proxy/application_1651489943447_35672/"
  val newUrlV2 = urlV2.replace("emr-header-1.cluster-123456", "1.3.2.4")
  println(urlV2)
  println(newUrlV2)

}
