package cn.izualzhy.blog

class BlogScalarFunction {
  def blogSubstr(s: java.lang.String, start: java.lang.Integer, length: java.lang.Integer): String = {
    if (s == null) return null
    s.substring(start, start + length)
  }
}
