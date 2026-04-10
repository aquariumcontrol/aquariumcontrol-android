/*
 * Copyright 2025 Uwe Martin
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.laimburggasse.aquariumcontrol.controller.domain

/**
 * Checks if the provided string is not empty and compatible to
 * IPv4 address, IPv6 address or DNS hostname.
 *
 * @param address The string to check
 * - IPv4 Address:
 * Four decimal numbers (0-255) separated by dots (.).
 * Example: 192.168.1.100
 * - IPv6 Address:
 * Eight groups of hexadecimal numbers (0-FFFF) separated by colons.
 * Can contain leading zeros and double colons to represent consecutive groups of zeros.
 * Example: 2001:0db8:85a3:0000:0000:8a2e:0370:7334, 2001:db8::1
 * - DNS Hostname:
 * Example: www.example.com, my-server.example.org, localhost
 */
fun isHostName(address: String): Boolean {
    if (address.isBlank()) {
        return false
    } else {
        val ipv4Regex: Regex =
            $$"^(([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])\\.){3}([0-9]|[1-9][0-9]|1[0-9]{2}|2[0-4][0-9]|25[0-5])$".toRegex()
        val ipv6Regex = "^([0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}$".toRegex()
        val ipv6CompressedRegex: Regex =
            $$"^(([0-9a-fA-F]{1,4}:){0,1}(([0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4})?::([0-9a-fA-F]{1,4}:){0,6}[0-9a-fA-F]{1,4})?$".toRegex()
        val dnsRegex: Regex =
            "^([a-zA-Z0-9]([a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?\\.)+[a-zA-Z]{2,}$".toRegex()

        return address.matches(ipv4Regex) ||
                address.matches(ipv6Regex) ||
                address.matches(ipv6CompressedRegex) ||
                address.matches(dnsRegex)
    }
}

/**
 * Checks if the provided port number is valid.
 *
 * @param port The port number to check
 */
fun isValidPortNumber(port: Int): Boolean {
    return port in 0..65535
}