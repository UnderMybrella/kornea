package dev.brella.kornea.io.jvm.files

import dev.brella.kornea.io.common.Url
import java.io.File
import java.net.URI

public inline fun Url.Companion.fromFile(file: File): Url = Url(Url.Companion.PROTOCOL_FILE, null, file.path, null, null)
public inline fun Url.Companion.fromUri(uri: URI): Url = Url(uri.scheme, if (uri.userInfo == null && uri.host == null && uri.port == -1) null else Url.Authority(uri.userInfo, uri.host, uri.port.takeUnless { it == -1 }), uri.path, uri.query, uri.fragment)