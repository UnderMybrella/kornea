package dev.brella.kornea.io.js

import kotlinx.coroutines.await
import org.w3c.xhr.XMLHttpRequest
import kotlin.js.Promise

public suspend fun urlExists(url: String): Boolean =
        Promise { resolve: (Boolean) -> Unit, _: (Throwable) -> Unit ->
            val headRequest = XMLHttpRequest()
            headRequest.open("HEAD", url)
            headRequest.onreadystatechange = { if (headRequest.readyState == XMLHttpRequest.DONE) resolve(headRequest.status.toInt() in 200..299) }
            headRequest.send()
        }.await()

@ExperimentalUnsignedTypes
public suspend fun urlSize(url: String): ULong? =
        Promise { resolve: (ULong?) -> Unit, _: (Throwable) -> Unit ->
            val headRequest = XMLHttpRequest()
            headRequest.open("HEAD", url)
            headRequest.onreadystatechange = { if (headRequest.readyState == XMLHttpRequest.DONE) resolve(if (headRequest.status.toInt() == 200) headRequest.getResponseHeader("Content-Length")?.toULongOrNull() else null) }
            headRequest.send()
        }.await()