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
      |}""".stripMargin
  val config = ConfigFactory.parseString(configStr)
  println(config.getString("service.host"))

  val modelConfig = config.getConfig("models")
  println(modelConfig.getConfig("kv-model").getConfig("version-1").getString("k"))
  println(config.getString("models.kv-model.version-1.k"))

  // 获取 models.kv-model 对象
  val kvModelConfig = config.getConfig("models.kv-model")
  kvModelConfig.root().entrySet().asScala.foreach(entry => {
    println(s"key:${entry.getKey} value:${entry.getValue}")
  })
}
