package com.blinkbox.books.quartermaster.key.common

import java.util.UUID

import scala.util.Random

case class KeyId(value: String) extends AnyVal
case class KeyValue(value: String) extends AnyVal
case class Isbn(value: String) extends AnyVal
case class FileHash(value: String) extends AnyVal

case class Key(id: KeyId, isbn: Isbn, key: KeyValue, fileHash: FileHash, v1Path: Option[String])
case class CreateKeyRequest(isbn: Isbn, fileHash: FileHash)
case class AddPathRequest(v1Path: String)

object KeyId extends (String => KeyId) {
  def generate = new KeyId(UUID.randomUUID.toString.replaceAll("-", ""))
}

object KeyValue extends (String => KeyValue) {
  // TODO: Add more characters to possible password: abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789!\"$%^&*()_-=+[]{};:'@#~/?.>,<\\|`
  def generate = new KeyValue(Random.alphanumeric.take(32).mkString)
}