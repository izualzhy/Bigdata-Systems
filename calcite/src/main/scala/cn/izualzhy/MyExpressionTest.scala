package cn.izualzhy

import java.util.Arrays

import org.apache.calcite.linq4j.tree.{Expressions, FunctionExpression, ParameterExpression}

object MyExpressionTest extends App {

  class MyTestClass {
    def foo(): Unit = {
      println("Hello World")
    }

    def bar(who: String): Int = {
      val s = who + ": Hello World!"
      println(s)
      s.length
    }
  }

  val o = new MyTestClass
  //  val a = Expressions.call(classOf[MyTestClass], "foo", o)
  //  println(Expressions.toString(a))

  // A parameter for the lambda expression.
  val paramExpr: ParameterExpression = Expressions.parameter(java.lang.Double.TYPE, "arg")
  // This expression represents a lambda expression
  // that adds 1 to the parameter value.

  val lambdaExpr = Expressions.lambda(Expressions.add(paramExpr, Expressions.constant(2d)), Arrays.asList(paramExpr))
  println(Expressions.toString(lambdaExpr))

//  val result = lambdaExpr.compile().dynamicInvoke(1.0).asInstanceOf[java.lang.Double]
  val res = lambdaExpr.compile.dynamicInvoke(1: java.lang.Double).asInstanceOf[Double]
  println(res)

}
