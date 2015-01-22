package com.blinkbox.books.quartermaster.key.common

import com.blinkbox.books.slick.TablesContainer

import scala.slick.driver.JdbcProfile
import scala.slick.lifted.ProvenShape.proveShapeOf

trait KeyTables[Profile <: JdbcProfile] extends TablesContainer[Profile] {
  import driver.simple._

  implicit lazy val keyIdMapper = MappedColumnType.base[KeyId, String](_.value, KeyId.apply)
  implicit lazy val keyValueMapper = MappedColumnType.base[KeyValue, String](_.value, KeyValue.apply)
  implicit lazy val IsbnMapper = MappedColumnType.base[Isbn, String](_.value, Isbn.apply)
  implicit lazy val FileHashMapper = MappedColumnType.base[FileHash, String](_.value, FileHash.apply)

  val keys = TableQuery[KeyTable]

  class KeyTable(tag: Tag) extends Table[Key](tag, "keys") {
    def id = column[KeyId]("id", O.PrimaryKey, O.NotNull)
    def isbn = column[Isbn]("isbn", O.NotNull)
    def key = column[KeyValue]("key", O.NotNull)
    def fileHash = column[FileHash]("file_hash", O.NotNull)
    def v1Path = column[Option[String]]("v1_path")
    def * = (id, isbn, key, fileHash, v1Path) <> (Key.tupled, Key.unapply _)
  }
}

object KeyTables {
  def apply[Profile <: JdbcProfile](_driver: Profile) = new KeyTables[Profile] {
    override val driver = _driver
  }
}
