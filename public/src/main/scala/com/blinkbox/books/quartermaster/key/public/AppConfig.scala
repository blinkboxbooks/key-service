package com.blinkbox.books.quartermaster.key.public

import com.blinkbox.books.config._
import com.typesafe.config.Config

case class AppConfig(api: ApiConfig, db: DatabaseConfig)

object AppConfig {
  def apply(config: Config): AppConfig = AppConfig(
    ApiConfig(config,  "service.quartermaster.key.admin"),
    DatabaseConfig(config, "service.quartermaster.key.common.db")
  )
}