package com.blinkbox.books.config

import com.typesafe.config.Config
import com.typesafe.config.ConfigException.BadValue

case class DBSettings(url: String, user: String, pass: String)

object DBSettings {
  def apply(config: Config, prefix: String): DBSettings = {
    val url = config.getUri(s"$prefix.url")
    val port = if (url.getPort != -1) url.getPort else 3306
    val Array(user, pass) = if (url.getUserInfo != null) {
      url.getUserInfo.split(":") match {
        case x @ Array(u, p) => x
        case _ => throw new BadValue(config.origin(), s"$prefix.url", "Username and password information is missing.")
      }
    } else throw new BadValue(config.origin(), s"$prefix.url", "Username and password information is missing.")
    DBSettings(
      s"jdbc:${url.getScheme}://${url.getHost}:$port${url.getPath}",
      user,
      pass
    )
  }
}
