package dev.brella.kornea.io.jvm.files

import dev.brella.kornea.io.common.Uri
import java.io.File
import java.net.URI

public inline fun Uri.Companion.fromFile(file: File): Uri = Uri(Uri.Companion.PROTOCOL_FILE, null, file.path, null, null)
public inline fun Uri.Companion.fromUri(uri: URI): Uri = Uri(uri.scheme, if (uri.userInfo == null && uri.host == null && uri.port == -1) null else Uri.Authority(uri.userInfo, uri.host, uri.port.takeUnless { it == -1 }), uri.path, uri.query, uri.fragment)