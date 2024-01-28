package cn.izualzhy

import scalaj.http.{Http, HttpOptions}

object FlinkRestTool extends App {
  private def cancelFlinkJob(url: String): Boolean = {
    Some(url)
      .exists(s => {
        val response = Http(s)
          .option(HttpOptions.allowUnsafeSSL)
          .option(HttpOptions.followRedirects(true))
          .method("PATCH")
          .timeout(5000, 5000)
          .asString
        println(s"cancel flink job response: $response")
        response.isSuccess
      }
      )
  }

  cancelFlinkJob("")
}
