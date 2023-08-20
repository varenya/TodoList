package com.todoist.config

import org.http4k.cloudnative.env.Environment
import org.http4k.cloudnative.env.EnvironmentKey
import org.http4k.cloudnative.env.Port
import org.http4k.core.Uri
import org.http4k.lens.port
import org.http4k.lens.uri

val systemEnv = Environment.ENV
val appProperties = Environment.fromResource("app.properties")

val portLens = EnvironmentKey.port().required("PORT")
val dbUrl = EnvironmentKey.uri().required("DB_URL")

fun getWebAppConfig(env: String): AppConfig {
    val envSpecificProperties = Environment.fromResource("app-${env}.properties")
    val finalEnv =
        envSpecificProperties overrides
                appProperties overrides
                systemEnv

    val dbConfig = DbConfig(dbUrl = dbUrl(finalEnv))
    val webConfig = WebConfig(port = portLens(finalEnv))
    return AppConfig(dbConfig, webConfig)
}

data class DbConfig(val dbUrl: Uri)
data class WebConfig(val port: Port)
data class AppConfig(val dbConfig: DbConfig, val webConfig: WebConfig)