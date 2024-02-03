package cn.izualzhy.blog

import org.apache.calcite.schema.impl.ScalarFunctionImpl
import org.apache.calcite.schema.{Schema, SchemaFactory, SchemaPlus}

import java.util

class BlogSchemaFactory extends SchemaFactory {

  override def create(parentSchema: SchemaPlus, name: String, operand: util.Map[String, AnyRef]): Schema = {
    parentSchema.add("BLOG_SUBSTR", ScalarFunctionImpl.create(classOf[BlogScalarFunction], "blogSubstr"))
    parentSchema.getFunctionNames.forEach(println)
    new BlogSchema
  }
}
