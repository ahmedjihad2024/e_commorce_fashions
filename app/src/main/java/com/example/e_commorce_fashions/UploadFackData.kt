package com.example.e_commorce_fashions

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.e_commorce_fashions.app.utils.Constants.NEW_ARRIVAL_COLLECTION
import com.example.e_commorce_fashions.app.utils.Constants.POPULAR_COLLECTION
import com.example.e_commorce_fashions.app.utils.Constants.PRODUCTS_COLLECTION
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.local.ReferenceSet
import com.google.type.DateTime
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date


data class Product(
    val name: String,
    val category: DocumentReference,
    val price: Double,
    val description: String,
    val imageUrls: List<String>?,
    val sizes: List<String>,
    val colors: List<String>,
    val reviews: Int,
    val rating: Double,
    val available: Boolean,
    val timestamp: Timestamp = Timestamp.now()
)


//SimpleDateFormat(
//"dd/MM/yyyy hh:mm",
//java.util.Locale.getDefault()
//).format(
//System.currentTimeMillis()
//)
class UploadFakeData {
    val db = FirebaseFirestore.getInstance()

    // refrence filed in firebasefirst
    val ref1 = db.collection("categories").document("5FpTw4osncSO0T6MWMJV")
    val ref2 = db.collection("categories").document("7LfmXr7Zj8AXEl9tLjPr")
    val ref3 = db.collection("categories").document("ezniBJ0vXMA6J8S3Rw6o")

    val accessKey = "L8VUiALbO2Qk29LodTsyWJLAWVhzbXha_seodeDrIkU"
    val secretKey = "LmssDlZH_7VlPUaRFw8wysTCebDQPCNk-4kLbQLlxq8"

    val scope = CoroutineScope(Dispatchers.Main + CoroutineName("Scope"))

    val now = Calendar.getInstance()

    val data: List<Product> = listOf(
        Product(
            "Classic Black T-Shirt",
            ref1,
            19.99,
            "A classic black T-shirt made of 100% cotton. Comfortable and versatile for any occasion.",
            null,
            listOf("S", "M", "L", "XL"),
            listOf("#000000"),
            45,
            4.5,
            true
        ),
        Product(
            "White Sneakers",
            ref2,
            49.99,
            "Comfortable white sneakers perfect for casual wear. Made with breathable materials.",
            null,
            listOf("7", "8", "9", "10", "11"),
            listOf("#FFFFFF"),
            78,
            4.7,
            true
        ),
        Product(
            "Leather Wallet",
            ref3,
            29.99,
            "A premium leather wallet with multiple compartments. Stylish and durable for everyday use.",
            null,
            emptyList(),
            listOf("#8B4513"),
            30,
            4.2,
            false
        ),
        Product(
            "Blue Denim Jeans",
            ref1,
            39.99,
            "Stylish blue denim jeans with a comfortable fit. Perfect for casual outings or a day at the office.",
            null,
            listOf("S", "M", "L", "XL"),
            listOf("#0000FF"),
            60,
            4.4,
            true
        ),
        Product(
            "Sporty Running Shoes",
            ref2,
            59.99,
            "Lightweight and breathable running shoes designed for comfort and performance.",
            null,
            listOf("8", "9", "10"),
            listOf("#FF5733"),
            85,
            4.6,
            true
        ),
        Product(
            "Stylish Sunglasses",
            ref3,
            25.99,
            "Fashionable sunglasses with UV protection. Perfect accessory for sunny days.",
            null,
            emptyList(),
            listOf("#000000"),
            40,
            4.3,
            true
        ),
        Product(
            "Elegant Dress",
            ref1,
            89.99,
            "An elegant dress suitable for formal occasions. Made with high-quality fabric and attention to detail.",
            null,
            listOf("S", "M", "L"),
            listOf("#FF0000"),
            95,
            4.8,
            true
        ),
        Product(
            "Classic Wristwatch",
            ref2,
            199.99,
            "A classic wristwatch with a leather strap and a sleek design. Perfect for any formal or casual event.",
            null,
            emptyList(),
            listOf("#000000"),
            50,
            4.7,
            true
        ),
        Product(
            "Comfortable Hoodie",
            ref3,
            34.99,
            "A soft and cozy hoodie perfect for lounging or casual outings. Made from high-quality fleece.",
            null,
            listOf("M", "L", "XL"),
            listOf("#CCCCCC"),
            70,
            4.6,
            true
        ),
        Product(
            "Casual Backpack",
            ref1,
            44.99,
            "A versatile backpack suitable for everyday use. Features multiple compartments and a durable design.",
            null,
            emptyList(),
            listOf("#333333"),
            55,
            4.4,
            true
        ),
        Product(
            "Luxury Handbag",
            ref2,
            299.99,
            "A luxurious handbag made from premium leather. Stylish and spacious for all your essentials.",
            null,
            emptyList(),
            listOf("#C0C0C0"),
            40,
            4.9,
            true
        ),
        Product(
            "Modern Desk Lamp",
            ref3,
            59.99,
            "A sleek and modern desk lamp with adjustable brightness. Ideal for home office or study use.",
            null,
            emptyList(),
            listOf("#FFFFFF"),
            30,
            4.2,
            true
        ),
        Product(
            "Bluetooth Speaker",
            ref1,
            79.99,
            "Portable Bluetooth speaker with high-quality sound and long battery life. Perfect for any party or outdoor activity.",
            null,
            emptyList(),
            listOf("#FF5733"),
            65,
            4.5,
            true
        ),
        Product(
            "High-Quality Pen Set",
            ref2,
            19.99,
            "A premium pen set featuring a ballpoint pen and a rollerball pen. Ideal for professionals and gift-giving.",
            null,
            emptyList(),
            listOf("#000000"),
            25,
            4.0,
            true
        ),
        Product(
            "Elegant Necklace",
            ref3,
            129.99,
            "A stunning necklace with intricate design. Perfect for special occasions or as a statement piece.",
            null,
            emptyList(),
            listOf("#FFD700"),
            80,
            4.8,
            true
        ),
        Product(
            "Smartphone Case",
            ref1,
            14.99,
            "A durable and stylish smartphone case to protect your device from scratches and drops.",
            null,
            emptyList(),
            listOf("#0000FF"),
            35,
            4.3,
            true
        ),
        Product(
            "Yoga Mat",
            ref2,
            39.99,
            "Comfortable and non-slip yoga mat for your workouts. Made from eco-friendly materials.",
            null,
            emptyList(),
            listOf("#008000"),
            50,
            4.6,
            true
        ),
        Product(
            "Digital Camera",
            ref3,
            499.99,
            "High-resolution digital camera with various shooting modes. Ideal for photography enthusiasts.",
            null,
            emptyList(),
            listOf("#000000"),
            20,
            4.7,
            true
        ),
        Product(
            "Water Bottle",
            ref1,
            19.99,
            "Reusable water bottle with a sleek design and a leak-proof cap. Perfect for staying hydrated on the go.",
            null,
            emptyList(),
            listOf("#00BFFF"),
            60,
            4.4,
            true
        ),
        Product(
            "Portable Charger",
            ref2,
            29.99,
            "Compact and powerful portable charger to keep your devices powered up. Includes fast charging capabilities.",
            null,
            emptyList(),
            listOf("#FF4500"),
            45,
            4.5,
            true
        ),
        Product(
            "Wool Scarf",
            ref3,
            24.99,
            "Soft and warm wool scarf. Ideal for cold weather and stylish layering.",
            null,
            emptyList(),
            listOf("#8A2BE2"),
            35,
            4.2,
            true
        ),
        Product(
            "Leather Belt",
            ref1,
            34.99,
            "A high-quality leather belt with an adjustable buckle. Perfect for both casual and formal wear.",
            null,
            emptyList(),
            listOf("#A0522D"),
            40,
            4.3,
            true
        ),
        Product(
            "Gaming Headset",
            ref2,
            89.99,
            "Comfortable gaming headset with noise-canceling microphone and high-quality sound.",
            null,
            emptyList(),
            listOf("#FF6347"),
            70,
            4.7,
            true
        ),
        Product(
            "Cookware Set",
            ref3,
            129.99,
            "A complete cookware set including pots, pans, and utensils. Made from durable stainless steel.",
            null,
            emptyList(),
            listOf("#C0C0C0"),
            25,
            4.6,
            true
        ),
        Product(
            "Electric Toothbrush",
            ref1,
            59.99,
            "Advanced electric toothbrush with multiple brushing modes and long battery life.",
            null,
            emptyList(),
            listOf("#FF1493"),
            55,
            4.5,
            true
        ),
        Product(
            "Travel Suitcase",
            ref2,
            199.99,
            "Spacious and durable travel suitcase with four wheels for easy maneuverability.",
            null,
            emptyList(),
            listOf("#00008B"),
            30,
            4.6,
            true
        ),
        Product(
            "Wall Art",
            ref3,
            79.99,
            "Beautiful wall art print to enhance your living space. High-quality and ready to hang.",
            null,
            emptyList(),
            listOf("#FFD700"),
            20,
            4.4,
            true
        ),
        Product(
            "Outdoor Grill",
            ref1,
            299.99,
            "High-performance outdoor grill perfect for barbeques and gatherings.",
            null,
            emptyList(),
            listOf("#FF8C00"),
            15,
            4.7,
            true
        ),
        Product(
            "Smartwatch",
            ref2,
            199.99,
            "Feature-rich smartwatch with fitness tracking, notifications, and more.",
            null,
            emptyList(),
            listOf("#000000"),
            40,
            4.6,
            true
        ),
        Product(
            "Scented Candles",
            ref3,
            29.99,
            "Set of scented candles with various fragrances. Perfect for relaxation and home ambiance.",
            null,
            emptyList(),
            listOf("#FF69B4"),
            60,
            4.3,
            true
        ),
        Product(
            "Fitness Tracker",
            ref1,
            89.99,
            "Advanced fitness tracker with heart rate monitoring and activity tracking features.",
            null,
            emptyList(),
            listOf("#32CD32"),
            50,
            4.5,
            true
        ),
        Product(
            "Portable Air Conditioner",
            ref2,
            399.99,
            "Compact and efficient portable air conditioner for cooling small to medium-sized rooms.",
            null,
            emptyList(),
            listOf("#4682B4"),
            20,
            4.7,
            true
        ),
        Product(
            "Digital Thermometer",
            ref3,
            19.99,
            "Accurate digital thermometer for quick and reliable temperature readings.",
            null,
            emptyList(),
            listOf("#FF4500"),
            70,
            4.4,
            true
        ),
        Product(
            "Camping Tent",
            ref1,
            149.99,
            "Spacious and durable camping tent suitable for outdoor adventures.",
            null,
            emptyList(),
            listOf("#FFD700"),
            25,
            4.6,
            true
        ),
        Product(
            "Electric Kettle",
            ref2,
            39.99,
            "Fast boiling electric kettle with auto shut-off and temperature control.",
            null,
            emptyList(),
            listOf("#FF6347"),
            55,
            4.5,
            true
        ),
        Product(
            "Cordless Vacuum Cleaner",
            ref3,
            249.99,
            "Powerful cordless vacuum cleaner with long battery life and efficient suction.",
            null,
            emptyList(),
            listOf("#228B22"),
            30,
            4.7,
            true
        ),
        Product(
            "Wine Glass Set",
            ref1,
            39.99,
            "Elegant wine glass set for enjoying your favorite wines. Includes four glasses.",
            null,
            emptyList(),
            listOf("#FF1493"),
            45,
            4.4,
            true
        ),
        Product(
            "Cookbook",
            ref2,
            29.99,
            "Delicious cookbook featuring a variety of recipes from around the world.",
            null,
            emptyList(),
            listOf("#8B4513"),
            60,
            4.3,
            true
        ),
        Product(
            "Bean Bag Chair",
            ref3,
            79.99,
            "Comfortable bean bag chair perfect for lounging and relaxation.",
            null,
            emptyList(),
            listOf("#A9A9A9"),
            35,
            4.6,
            true
        ),
        Product(
            "Outdoor Swing",
            ref1,
            199.99,
            "Relaxing outdoor swing with a comfortable seat. Ideal for patios and gardens.",
            null,
            emptyList(),
            listOf("#FFD700"),
            20,
            4.7,
            true
        ),
        Product(
            "Electric Drill",
            ref2,
            89.99,
            "Versatile electric drill with multiple speed settings and accessories.",
            null,
            emptyList(),
            listOf("#0000FF"),
            50,
            4.5,
            true
        ),
        Product(
            "Table Lamp",
            ref3,
            39.99,
            "Stylish table lamp with adjustable brightness. Perfect for any room.",
            null,
            emptyList(),
            listOf("#FFFFFF"),
            40,
            4.4,
            true
        ),
        Product(
            "Digital Photo Frame",
            ref1,
            89.99,
            "High-resolution digital photo frame to display your favorite memories.",
            null,
            emptyList(),
            listOf("#FFD700"),
            30,
            4.6,
            true
        ),
        Product(
            "Fitness Yoga Block",
            ref2,
            14.99,
            "Durable yoga block for improving balance and flexibility during workouts.",
            null,
            emptyList(),
            listOf("#FF1493"),
            45,
            4.3,
            true
        ),
        Product(
            "Electric Griddle",
            ref3,
            99.99,
            "Large electric griddle with adjustable temperature control. Ideal for cooking breakfast meals.",
            null,
            emptyList(),
            listOf("#8B4513"),
            25,
            4.5,
            true
        ),
    )


    fun upload(context: Context) {
        scope.async {
            data.forEachIndexed { index, product ->
                now.add(Calendar.HOUR, index)
                db.collection(PRODUCTS_COLLECTION)
                    .add(product.copy(timestamp = Timestamp(now.time))).await()
            }
        }
    }

    fun uploadNewArrival() {
        scope.async {
            val products = db.collection(PRODUCTS_COLLECTION).limit(15)
                .orderBy("timestamp", Query.Direction.DESCENDING).get().await()
            products.documents.forEach { doc ->
                db.collection(NEW_ARRIVAL_COLLECTION).add(
                    mapOf("itemReference" to doc.reference)
                ).await()
            }
        }
    }

    fun uploadPopularData() {
        scope.async {
            val products = db.collection(PRODUCTS_COLLECTION).limit(30).get().await()
            products.documents.forEach { doc ->
                db.collection(POPULAR_COLLECTION).add(
                    mapOf("itemReference" to doc.reference)
                ).await()
            }
        }
    }

    fun fetchImageUrls() {
        Thread {
            try {
                val client = OkHttpClient()
                val request = Request.Builder()
                    .url("https://api.unsplash.com/search/photos?query=Jackets&orientation=portrait&client_id=$accessKey")
                    .build()
                val response = client.newCall(request).execute()
                val responseData = response.body?.string() ?: return@Thread

                // Parse JSON response
                val jsonObject = JSONObject(responseData)
                val jsonArray = jsonObject.getJSONArray("results")
                val imageUrls = mutableListOf<String>()
                for (i in 0 until jsonArray.length()) {
                    val photoObject = jsonArray.getJSONObject(i)
                    val urlsObject = photoObject.getJSONObject("urls")
                    val imageUrl = urlsObject.getString("regular")
                    imageUrls.add(imageUrl)
                }
                Log.d("my-data", "Image URLs: $imageUrls")
            } catch (e: Exception) {
                Log.e("my-data", "Failed to fetch image URLs", e)
            }
        }.start()
    }

}