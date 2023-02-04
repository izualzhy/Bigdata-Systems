package cn.izualzhy

import java.io.FileInputStream


/**
 * Author: yingshin
 * Date: 2022/4/18 11:13
 * Package: cn.izualzhy
 * Description:
 *
 */
object CalciteUTXMLTest extends App {
  val xml = scala.xml.XML.load(getClass.getClassLoader.getResource("RelOptRulesTest.xml"))

//  (xml \ "TestCase" \ "Resource").foreach{i =>
//    println(i.attribute("name").get)
//    println(i.attribute("name").get.text.equals("sql"))
//  }
//  (xml \ "TestCase" \ "Resource")
//    .filter(_.attribute("name").get.text == "sql")
//    .map(_.text.trim)
//    .filter(_.contains("join"))
//    .foreach(i => println(s"------\n${i}"))
  (xml \ "TestCase" \ "Resource")
    .filter(_.attribute("name").get.text == "planAfter")
    .map(_.text.trim)
    .filter(i => i.contains("LogicalFilter") && i.contains("LogicalJoin"))
    .foreach(i => println(s"------\n${i}"))

}
