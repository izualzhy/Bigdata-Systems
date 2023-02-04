package cn.izualzhy

import java.io.Serializable
import java.lang.reflect.Modifier
import java.util

import org.apache.calcite.linq4j.tree.{Blocks, Expressions}
import org.codehaus.commons.compiler.CompilerFactoryFactory

object ExpressionsHelloWorld extends App {
  val className = "MyHelloWorld"
  val funcName = "hello"
  val params = Expressions.parameter(classOf[String], "who")
  // hello 函数实现
  val func = Expressions.call(
    // System.out
    Expressions.field(null, classOf[System].getField("out")),
    "println",
    Expressions.add(
      Expressions.add(
        Expressions.constant("Hello World, "),
        params),
      Expressions.constant("!")
    )
  )
  // hello 函数整体
  val funcBody = Expressions.methodDecl(
    Modifier.PUBLIC | Modifier.STATIC,
    java.lang.Void.TYPE,
    funcName,
    util.Arrays.asList(params),
    Blocks.toFunctionBlock(func)
  )
  // MyHelloWorld 类实现
  val classHelloWorld = Expressions.classDecl(
    Modifier.PUBLIC,
    className,
    null,
    util.Arrays.asList(classOf[Serializable]),
    util.Arrays.asList(funcBody)
  )
  val helloWorldSrc = Expressions.toString(classHelloWorld)
  println(helloWorldSrc)

  val sc = CompilerFactoryFactory.getDefaultCompilerFactory.newSimpleCompiler
  sc.cook(helloWorldSrc)
  val helloWorldClass = sc.getClassLoader.loadClass(className)
  helloWorldClass.getMethod(funcName, classOf[String]).invoke(null,  "Expressions")
}
