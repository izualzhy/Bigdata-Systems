package cn.izualzhy

case class User(id: Int,
                name: String,
                age: Int)

object UserMain extends App {
  val user1 = User(1, "Jeff Dean", 2)
  val user2 = user1.copy(name = "Sanjay", age = 6)
  println(user1)
  println(user2)
}
