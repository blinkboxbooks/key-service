package com.blinkbox.books.quartermaster.key.common

import com.blinkbox.books.config.DatabaseConfig
import com.blinkbox.books.slick.{DatabaseComponent, DatabaseSupport, MySQLDatabaseSupport, TablesContainer}
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.Future
import scala.concurrent.ExecutionContext
import scala.slick.driver.MySQLDriver
import scala.slick.jdbc.JdbcBackend.Database


trait KeyStore {
  def getOrCreateKey(isbn: Isbn, fileHash: FileHash): Future[Key]
  def addV1Path(id: KeyId, path: String): Future[Key]
  def getKey(id: KeyId): Future[Option[Key]]
}

class DbKeyStore[DB <: DatabaseSupport](db: DB#Database, tables: KeyTables[DB#Profile], exceptionFilter: DB#ExceptionFilter, implicit val exc: ExecutionContext) extends KeyStore with StrictLogging {

  import tables._
  import driver.simple._

  override def getOrCreateKey(isbn: Isbn, fileHash: FileHash): Future[Key] = Future {
    db.withSession { implicit session =>
      tables.keys
        .filter(k => k.isbn === isbn && k.fileHash === fileHash)
        .firstOption.getOrElse(createKey(isbn, fileHash))
    }
  } transform(identity, exceptionFilter.default)

  override def addV1Path(id: KeyId, path: String): Future[Key] = Future {
    db.withSession { implicit session =>
      val key = queryById(id).firstOption

      key.map { k =>
        k.v1Path.fold(updateKeyWithV1Path(k, path))(_ => throw new KeyHasV1PathException)
      } getOrElse(throw new NoSuchKeyException)
    }
  } transform(identity, exceptionFilter.default)

  override def getKey(id: KeyId): Future[Option[Key]] = Future {
    db.withSession(queryById(id).firstOption(_))
  } transform(identity, exceptionFilter.default)

  private def queryById(id: KeyId) = tables.keys.filter(k => k.id === id)

  private def createKey(isbn: Isbn, fileHash: FileHash)(implicit session: DB#Session): Key = {
    val key: Key = new Key(KeyId.generate, isbn, KeyValue.generate, fileHash, None)
    tables.keys.insert(key)
    key
  }

  private def updateKeyWithV1Path(key: Key, path: String)(implicit session: DB#Session): Key = {
    val updatedKey = key.copy(v1Path = Some(path))
    queryById(key.id).update(updatedKey)
    updatedKey
  }
}

class DefaultDatabaseComponent(config: DatabaseConfig) extends DatabaseComponent {

  override type Tables = TablesContainer[DB.Profile]

  override val driver = MySQLDriver
  override val DB = new MySQLDatabaseSupport
  override val db = Database.forURL(
    driver = "com.mysql.jdbc.Driver",
    url = config.jdbcUrl,
    user = config.user,
    password = config.pass)
  override val tables = KeyTables[DB.Profile](driver)
}

class NoSuchKeyException extends Exception("KeyNotFound")
class KeyHasV1PathException extends Exception("KeyHasV1Path")