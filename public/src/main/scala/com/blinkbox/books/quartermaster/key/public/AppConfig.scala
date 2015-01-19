package com.blinkbox.books.quartermaster.key.public

import com.blinkbox.books.config._
import com.typesafe.config.Config

case class AppConfig(api: ApiConfig, db: DBSettings)

object AppConfig {
  def apply(config: Config): AppConfig = AppConfig(
    ApiConfig(config,  "service.quartermaster.key.admin"),
    DBSettings(config, "service.quartermaster.key.common.db")
  )
}