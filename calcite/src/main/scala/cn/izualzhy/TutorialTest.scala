package cn.izualzhy

import java.io.File
import java.sql.{DriverManager, ResultSet}
import java.util.Properties

import org.apache.calcite.adapter.csv.{CsvSchema, CsvTable}
import org.apache.calcite.jdbc.CalciteConnection

object TutorialTest extends App {
  val csvPath = getClass.getClassLoader.getResource("sales_from_calcite_sources").getPath
//  val csvSchema = new CsvSchema(new File(csvPath), CsvTable.Flavor.SCANNABLE)
  val csvSchema = new CsvSchema(new File(csvPath), CsvTable.Flavor.TRANSLATABLE)
  val properties = new Properties()
  properties.setProperty("caseSensitive", "false")
  val connection = DriverManager.getConnection("jdbc:calcite:", properties)
  val calciteConnection = connection.unwrap(classOf[CalciteConnection])
  val rootSchema = calciteConnection.getRootSchema
  rootSchema.add("sales", csvSchema)

  query("SELECT empno, name, deptno FROM sales.emps WHERE deptno > 20")

  def query(sql: String): Unit = {
    println(s"****** $sql ******")
    val statement = calciteConnection.createStatement()
    val resultSet = statement.executeQuery(sql)
    dumpResultSet(resultSet)
  }

  def dumpResultSet(resultSet: ResultSet): Unit = {
    val columnCnt = resultSet.getMetaData.getColumnCount

    while (resultSet.next()) {
      val rowString = (1 to columnCnt).map{i =>
        val label = resultSet.getMetaData.getColumnLabel(i)
        val value = resultSet.getObject(i).toString
        s"$label:$value"
      }.mkString(" ")
      println(rowString)
    }
  }
}
