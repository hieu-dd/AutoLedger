package org.bakarot.autoledger

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
