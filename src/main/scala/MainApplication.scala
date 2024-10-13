import slick.jdbc.PostgresProfile.api._
import slick.lifted.ProvenShape

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object LibrarySystem {

  // Define case classes
  case class Author(name: String, birthYear: Int, id: Option[Int] = None)
  case class Book(title: String, authorId: Int, year: Int, id: Option[Int] = None)

  // Define Tables
  class Authors(tag: Tag) extends Table[Author](tag, "authors") {
    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def name: Rep[String] = column[String]("name")
    def birthYear: Rep[Int] = column[Int]("birth_year")

    def * : ProvenShape[Author] = (name, birthYear, id.?) <> (Author.tupled, Author.unapply)
  }

  class Books(tag: Tag) extends Table[Book](tag, "books") {
    def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
    def title: Rep[String] = column[String]("title")
    def authorId: Rep[Int] = column[Int]("author_id")
    def year: Rep[Int] = column[Int]("year")

    def author = foreignKey("author_fk", authorId, authors)(_.id)

    def * : ProvenShape[Book] = (title, authorId, year, id.?) <> (Book.tupled, Book.unapply)
  }

  // Database configuration
  val db = Database.forConfig("mydb")

  val authors = TableQuery[Authors]
  val books = TableQuery[Books]

  def setupSchema(): Unit = {
    val setup = DBIO.seq(
      (authors.schema ++ books.schema).create,
      authors += Author("George Orwell", 1903),
      authors += Author("Harper Lee", 1926),
      authors += Author("Aldous Huxley", 1894),
      authors += Author("F. Scott Fitzgerald", 1896),
      books += Book("1984", 1, 1949),
      books += Book("To Kill a Mockingbird", 2, 1960),
      books += Book("Brave New World", 3, 1932),
      books += Book("The Great Gatsby", 4, 1925)
    )

    Await.result(db.run(setup), Duration.Inf)
  }

  def getBooksByAuthor(authorName: String): Seq[Book] = {
    val query = for {
      a <- authors if a.name === authorName
      b <- books if b.authorId === a.id
    } yield b
    Await.result(db.run(query.result), Duration.Inf)
  }

  def getBooksBeforeYear(year: Int): Seq[Book] = {
    val query = books.filter(_.year < year)
    Await.result(db.run(query.result), Duration.Inf)
  }

  def getAuthorsByBirthYearRange(startYear: Int, endYear: Int): Seq[Author] = {
    val query = authors.filter(a => a.birthYear >= startYear && a.birthYear <= endYear)
    Await.result(db.run(query.result), Duration.Inf)
  }

  def filterBooks(criteria: Book => Boolean): Seq[Book] = {
    val allBooks = Await.result(db.run(books.result), Duration.Inf)
    allBooks.filter(criteria)
  }

  def main(args: Array[String]): Unit = {
    setupSchema()

    println("Books by George Orwell:")
    getBooksByAuthor("George Orwell").foreach(println)

    println("\nBooks published before 1940:")
    getBooksBeforeYear(1940).foreach(println)

    println("\nAuthors born between 1890 and 1900:")
    getAuthorsByBirthYearRange(1890, 1900).foreach(println)

    println("\nBooks published after 1950 using higher-order function:")
    filterBooks(book => book.year > 1950).foreach(println)
  }
}
