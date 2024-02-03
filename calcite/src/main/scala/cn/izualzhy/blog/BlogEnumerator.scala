package cn.izualzhy.blog

import org.apache.calcite.linq4j.Enumerator

//[Array[AnyRef]
class BlogEnumerator(blogPostPath: String) extends Enumerator[Array[AnyRef]] {
  private val blogPostMetaList: Seq[PostMeta] = BlogPostMetaExtractor.extractMetaInformation(blogPostPath)
  private var currentIndex: Int = -1 // 使用索引来跟踪当前元素，初始值为 -1

  override def current(): Array[AnyRef] = {
    if (currentIndex >= 0 && currentIndex < blogPostMetaList.size) {
      blogPostMetaList(currentIndex).toArray
    } else {
      throw new NoSuchElementException("No current element")
    }
  }

  override def moveNext(): Boolean = {
    if (currentIndex + 1 < blogPostMetaList.size) {
      currentIndex += 1
      true
    } else {
      false
    }
  }

  override def reset(): Unit = {
    currentIndex = -1 // 将索引重置为 -1，从而实现重置遍历的效果
  }

  override def close(): Unit = {
    // 在这里实现资源释放逻辑，如果有的话
  }
}
