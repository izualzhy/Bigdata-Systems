package cn.izualzhy.blog

import com.google.common.collect.ImmutableMap
import org.apache.calcite.schema.Table
import org.apache.calcite.schema.impl.AbstractSchema

import java.nio.file.{Path, Paths}

class BlogSchema extends AbstractSchema {
  private val blogRepositoryPath: Path = {
    val currentPath = Paths.get("").toAbsolutePath
    currentPath.getParent.getParent.resolve("yingshin.github.io/_posts")
  }

  override protected def getTableMap: java.util.Map[String, Table] = {
    val builder = ImmutableMap.builder[String, Table]

    builder.put("BLOG", new BlogTable(blogRepositoryPath.toAbsolutePath.toString))

    builder.build()
  }
}

object BlogSchema {
  def main(args: Array[String]): Unit = {
//    val blogSchema = new BlogSchema()
  }
}
