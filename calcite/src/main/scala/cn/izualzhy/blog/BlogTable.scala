package cn.izualzhy.blog

import org.apache.calcite.DataContext
import org.apache.calcite.linq4j.{AbstractEnumerable, Enumerable, Enumerator}
import org.apache.calcite.schema.ScannableTable

class BlogTable(blogPostPath: String) extends BlogAbstractTable with ScannableTable {
  override def scan(root: DataContext): Enumerable[Array[AnyRef]] = {
    new AbstractEnumerable[Array[AnyRef]] {
      override def enumerator(): Enumerator[Array[AnyRef]] = {
        new BlogEnumerator(blogPostPath)
      }
    }
  }
}
