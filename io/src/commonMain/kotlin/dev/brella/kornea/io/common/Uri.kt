package dev.brella.kornea.io.common

import dev.brella.kornea.errors.common.KorneaResult
import dev.brella.kornea.toolkit.common.clearToString

public data class Uri(val protocol: String, val authority: Uri.Authority?, val path: String, val query: String?, val fragment: String?) {
    public companion object {
        public const val PROTOCOL_HTTP: String = "http"
        public const val PROTOCOL_HTTPS: String = "https"
        public const val PROTOCOL_FTP: String = "ftp"
        public const val PROTOCOL_MAIL: String = "mailto"
        public const val PROTOCOL_FILE: String = "file"
        public const val PROTOCOL_DATA: String = "data"
        public const val PROTOCOL_IRC: String = "irc"

        private const val STATE_SCHEME = 0
        private const val STATE_AUTHORITY = 1
        private const val STATE_HOST = 2
        private const val STATE_PORT = 3
        private const val STATE_PATH = 4
        private const val STATE_QUERY = 5
        private const val STATE_FRAGMENT = 6

        public fun from(spec: String): KorneaResult<Uri> {
            val builder = StringBuilder()
            var scheme: String? = null
            var userinfo: String? = null
            var host: String? = null
            var port: String? = null
            var path: String? = null
            var query: String? = null
            var fragment: String? = null

            var state = STATE_SCHEME
            var index = 0

            do {
                when (state) {
                    STATE_SCHEME -> {
                        when (val char = spec[index]) {
                            ':' -> {
                                scheme = builder.clearToString()

                                if (index + 2 < spec.length && spec[index + 1] == '/' && spec[index + 2] == '/') {
                                    index += 2
                                    state = STATE_AUTHORITY
                                } else {
                                    state = STATE_PATH
                                }
                            }
                            else -> builder.append(char)
                        }
                    }
                    STATE_AUTHORITY -> {
                        when (val char = spec[index]) {
                            '@' -> {
                                userinfo = builder.clearToString()

                                state = STATE_HOST
                            }
                            ':' -> {
                                host = builder.clearToString()

                                state = STATE_PORT
                            }
                            '/' -> {
                                host = builder.clearToString()

                                state = STATE_PATH
                            }
                            '?' -> {
                                host = builder.clearToString()
                                path = "/"

                                state = STATE_QUERY
                            }
                            '#' -> {
                                host = builder.clearToString()
                                path = "/"

                                state = STATE_FRAGMENT
                            }
                            else -> builder.append(char)
                        }
                    }
                    STATE_HOST -> {
                        when (val char = spec[index]) {
                            ':' -> {
                                host = builder.clearToString()

                                state = STATE_PORT
                            }
                            '/' -> {
                                host = builder.clearToString()

                                state = STATE_PATH
                            }
                            '?' -> {
                                host = builder.clearToString()
                                path = "/"

                                state = STATE_QUERY
                            }
                            '#' -> {
                                host = builder.clearToString()
                                path = "/"

                                state = STATE_FRAGMENT
                            }
                            else -> builder.append(char)
                        }
                    }
                    STATE_PORT -> {
                        when (val char = spec[index]) {
                            '/' -> {
                                port = builder.clearToString()

                                state = STATE_PATH
                            }
                            '?' -> {
                                host = builder.clearToString()
                                path = "/"

                                state = STATE_QUERY
                            }
                            '#' -> {
                                host = builder.clearToString()
                                path = "/"

                                state = STATE_FRAGMENT
                            }
                            else -> builder.append(char)
                        }
                    }

                    STATE_PATH -> {
                        when (val char = spec[index]) {
                            '?' -> {
                                path = builder.clearToString()

                                state = STATE_QUERY
                            }
                            '#' -> {
                                path = builder.clearToString()

                                state = STATE_FRAGMENT
                            }
                            else -> builder.append(char)
                        }
                    }

                    STATE_QUERY -> {
                        when (val char = spec[index]) {
                            '#' -> {
                                if (fragment != null) return KorneaResult.errorAsIllegalArgument(-1, "Doubled up query")

                                query = builder.clearToString()

                                state = STATE_FRAGMENT
                            }
                            else -> builder.append(char)
                        }
                    }

                    STATE_FRAGMENT -> {
                        when (val char = spec[index]) {
                            '?' -> {
                                if (query != null) return KorneaResult.errorAsIllegalArgument(-1, "Doubled up fragment")

                                fragment = builder.clearToString()

                                state = STATE_QUERY
                            }
                            else -> builder.append(char)
                        }
                    }

                    else -> return KorneaResult.errorAsIllegalArgument(-1, "Unknown state $state")
                }
            } while (++index in spec.indices)

            when (state) {
                //We just got a regular string from the looks of things
                STATE_SCHEME -> return KorneaResult.errorAsIllegalArgument(-1, "No path provided")

                STATE_HOST -> host = builder.clearToString()
                STATE_PORT -> port = builder.clearToString()

                STATE_PATH -> path = builder.clearToString()
                STATE_QUERY -> query = builder.clearToString()
                STATE_FRAGMENT -> fragment = builder.clearToString()

                else -> return KorneaResult.errorAsIllegalArgument(-1, "Unknown finishing state $state")
            }

            if (scheme == null) return KorneaResult.errorAsIllegalArgument(-1, "No scheme provided")
            if (path == null) return KorneaResult.errorAsIllegalArgument(-1, "No path provided")

            val authority = if (host != null) Authority(userinfo, host, port?.toIntOrNull()) else null

            return KorneaResult.success(Uri(scheme, authority, path, query, fragment), null)
        }

        public inline fun fromFile(path: String): Uri = Uri(PROTOCOL_FILE, null, path, null, null)
    }

    public data class Authority(val userinfo: String?, val host: String, val port: Int?)

    override fun toString(): String = buildString {
        append(protocol)
        append(':')

        if (authority != null) {
            append("//")

            if (authority.userinfo != null) {
                append(authority.userinfo)
                append('@')
            }

            append(authority.host)

            if (authority.port != null) {
                append(':')
                append(authority.port)
            }
        }

        append(path)

        if (query != null) {
            append('?')
            append(query)
        }

        if (fragment != null) {
            append('#')
            append(fragment)
        }
    }
}