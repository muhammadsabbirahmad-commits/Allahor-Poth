package com.sabbirsamol.allahorpoth

import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

object OnlinePrayerFetcher {
    private val districtCoordinates = mapOf(
        "ঢাকা" to Pair(23.8103, 90.4125),
        "ফরিদপুর" to Pair(23.6071, 89.8428),
        "গোপালগঞ্জ" to Pair(23.0051, 89.8266),
        "জামালপুর" to Pair(24.9255, 89.9573),
        "কিশোরগঞ্জ" to Pair(24.4449, 90.7766),
        "মাদারীপুর" to Pair(23.1643, 90.1995),
        "মানিকগঞ্জ" to Pair(23.8631, 90.0053),
        "মুন্সিগঞ্জ" to Pair(23.5422, 90.5305),
        "ময়মনসিংহ" to Pair(24.7471, 90.4203),
        "নারায়ণগঞ্জ" to Pair(23.6238, 90.5000),
        "নরসিংদী" to Pair(23.9322, 90.7151),
        "নেত্রকোণা" to Pair(24.8709, 90.7273),
        "রাজবাড়ী" to Pair(23.7574, 89.6433),
        "শরীয়তপুর" to Pair(23.2104, 90.3423),
        "শেরপুর" to Pair(25.0205, 90.0153),
        "টাঙ্গাইল" to Pair(24.2513, 89.9167),
        "বগুড়া" to Pair(24.8465, 89.3777),
        "জয়পুরহাট" to Pair(25.0940, 89.0200),
        "নওগাঁ" to Pair(24.7936, 88.9318),
        "নাটোর" to Pair(24.4205, 89.0003),
        "নবাবগঞ্জ" to Pair(24.5948, 88.2745),
        "পাবনা" to Pair(24.0032, 89.2458),
        "রাজশাহী" to Pair(24.3636, 88.6241),
        "সিরাজগঞ্জ" to Pair(24.4533, 89.7006),
        "দিনাজপুর" to Pair(25.6217, 88.6355),
        "গাইবান্ধা" to Pair(25.3287, 89.5280),
        "কুড়িগ্রাম" to Pair(25.8054, 89.6362),
        "লালমনিরহাট" to Pair(25.9167, 89.4416),
        "নীলফামারী" to Pair(25.9317, 88.8560),
        "পঞ্চগড়" to Pair(26.3411, 88.5542),
        "রংপুর" to Pair(25.7439, 89.2752),
        "ঠাকুরগাঁও" to Pair(26.0330, 88.4616),
        "বাগেরহাট" to Pair(22.6515, 89.7939),
        "চুয়াডাঙ্গা" to Pair(23.6401, 88.8418),
        "যশোর" to Pair(23.1664, 89.2081),
        "ঝিনাইদহ" to Pair(23.5450, 89.1726),
        "খুলনা" to Pair(22.8456, 89.5403),
        "কুষ্টিয়া" to Pair(23.9012, 89.1204),
        "মাগুরা" to Pair(23.4873, 89.4211),
        "মেহেরপুর" to Pair(23.7622, 88.6318),
        "নড়াইল" to Pair(23.1725, 89.5126),
        "সাতক্ষীরা" to Pair(22.7185, 89.0705),
        "বরগুনা" to Pair(22.1556, 90.1260),
        "বরিশাল" to Pair(22.7010, 90.3535),
        "ভোলা" to Pair(22.6823, 90.6482),
        "ঝালকাঠি" to Pair(22.6416, 90.2013),
        "পটুয়াখালী" to Pair(22.3596, 90.3298),
        "পিরোজপুর" to Pair(22.5841, 90.0014),
        "বান্দরবান" to Pair(21.9965, 92.2152),
        "ব্রাহ্মণবাড়িয়া" to Pair(23.9571, 91.1119),
        "চাঁদপুর" to Pair(23.2321, 90.6661),
        "চট্টগ্রাম" to Pair(22.3569, 91.7832),
        "কুমিল্লা" to Pair(23.4610, 91.1850),
        "কক্সবাজার" to Pair(21.4272, 92.0058),
        "ফেনী" to Pair(23.0185, 91.3984),
        "খাগড়াছড়ি" to Pair(23.1192, 91.9847),
        "লক্ষ্মীপুর" to Pair(22.9447, 90.8282),
        "নোয়াখালী" to Pair(22.8695, 91.0994),
        "রাঙামাটি" to Pair(22.6533, 92.1751),
        "হবিগঞ্জ" to Pair(24.3745, 91.4152),
        "মৌলভীবাজার" to Pair(24.4829, 91.7774),
        "সুনামগঞ্জ" to Pair(25.0658, 91.3950),
        "সিলেট" to Pair(24.8949, 91.8687)
    )

    fun fetchTimingsForDistrict(district: String): Map<String, String>? {
        val coords = districtCoordinates[district] ?: Pair(22.7185, 89.0705)
        try {
            val url = URL("https://api.aladhan.com/v1/timings?latitude=${coords.first}&longitude=${coords.second}&method=1")
            val connection = url.openConnection() as HttpsURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            connection.readTimeout = 5000

            if (connection.responseCode == 200) {
                val response = connection.inputStream.bufferedReader().use { it.readText() }
                val json = JSONObject(response)
                val data = json.getJSONObject("data")
                val timings = data.getJSONObject("timings")

                val map = mutableMapOf<String, String>()
                map["Fajr"] = timings.getString("Fajr").take(5)
                map["Sunrise"] = timings.getString("Sunrise").take(5)
                map["Dhuhr"] = timings.getString("Dhuhr").take(5)
                map["Asr"] = timings.getString("Asr").take(5)
                map["Sunset"] = timings.getString("Sunset").take(5)
                map["Maghrib"] = timings.getString("Maghrib").take(5)
                map["Isha"] = timings.getString("Isha").take(5)
                connection.disconnect()
                return map
            }
            connection.disconnect()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}
