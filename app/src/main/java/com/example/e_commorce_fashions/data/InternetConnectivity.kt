package com.example.e_commorce_fashions.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.InetSocketAddress
import java.net.Socket

class InternetConnectivityChecker {
    suspend fun hasConnection(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                // Connect to a reliable DNS server (e.g., Google DNS) to check for internet
                val socket = Socket()
                val socketAddress = InetSocketAddress("8.8.8.8", 53) // Google's public DNS server

                socket.connect(socketAddress, 1500) // Timeout in milliseconds
                socket.close()

                true
            } catch (e: Exception) {
                false
            }
        }
    }
}