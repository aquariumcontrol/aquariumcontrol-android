package com.laimburggasse.aquariumcontrol.common.util

/**
 * Custom common implementation of Base64 encoding and decoding for cross-platform usage.
 */
object Base64 {
    private const val ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"

    private const val EQUALCHAR = '='

    /**
     * Decodes a Base64 encoded string into a byte array.
     *
     * @param input The Base64 string to decode.
     * @return A [ByteArray] containing the decoded data.
     */
    fun decode(input: String): ByteArray {
        val cleaned = input.filter { it in ALPHABET || it == EQUALCHAR }
        val bytes = mutableListOf<Byte>()
        var i = 0
        while (i + 3 < cleaned.length) {
            val b1 = ALPHABET.indexOf(cleaned[i++])
            val b2 = ALPHABET.indexOf(cleaned[i++])
            val b3Char = cleaned[i++]
            val b4Char = cleaned[i++]

            bytes.add(((b1 shl 2) or (b2 shr 4)).toByte())
            
            if (b3Char != EQUALCHAR) {
                val b3 = ALPHABET.indexOf(b3Char)
                bytes.add(((b2 shl 4) or (b3 shr 2)).toByte())
                if (b4Char != EQUALCHAR) {
                    val b4 = ALPHABET.indexOf(b4Char)
                    bytes.add(((b3 shl 6) or b4).toByte())
                }
            }
        }
        val result = ByteArray(bytes.size)
        for (index in bytes.indices) {
            result[index] = bytes[index]
        }
        return result
    }

    /**
     * Encodes a byte array into a Base64 string.
     *
     * @param input The data to encode.
     * @return A Base64 representation of the input data.
     */
    fun encode(input: ByteArray): String {
        val result = StringBuilder()
        var i = 0
        while (i < input.size) {
            val b1 = input[i++].toInt() and 0xFF
            result.append(ALPHABET[b1 shr 2])
            if (i < input.size) {
                val b2 = input[i++].toInt() and 0xFF
                result.append(ALPHABET[((b1 and 0x03) shl 4) or (b2 shr 4)])
                if (i < input.size) {
                    val b3 = input[i++].toInt() and 0xFF
                    result.append(ALPHABET[((b2 and 0x0F) shl 2) or (b3 shr 6)])
                    result.append(ALPHABET[b3 and 0x3F])
                } else {
                    result.append(ALPHABET[(b2 and 0x0F) shl 2])
                    result.append("=")
                }
            } else {
                result.append(ALPHABET[(b1 and 0x03) shl 4])
                result.append("==")
            }
        }
        return result.toString()
    }
}
