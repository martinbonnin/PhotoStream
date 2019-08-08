package net.mbonnin.photostream

import io.ktor.client.HttpClient
import io.ktor.client.features.json.JsonFeature
import io.ktor.client.features.json.serializer.KotlinxSerializer
import io.ktor.client.request.get
import kotlinx.serialization.*
import kotlinx.serialization.internal.HexConverter
import kotlinx.serialization.internal.StringDescriptor
import kotlinx.serialization.json.Json

object FlickrApi {

    private val apiKey = "043517c3b345ffd47ed08b7c664116e9"

    // The flickr API sometimes puts a Int and sometimes a String in 'width' and 'height'...
    // This is unfortunate but this custom class wraps it so that we can deserialize correctly.
    @Serializable
    class IntOrString(val value: Int) {
        @Serializer(forClass = IntOrString::class)
        companion object : KSerializer<IntOrString> {
            override val descriptor: SerialDescriptor =
                StringDescriptor.withName("IntOrString")

            override fun serialize(encoder: Encoder, obj: IntOrString) {
                encoder.encodeString(obj.value.toString())
            }

            override fun deserialize(decoder: Decoder): IntOrString {
                // In json, everything is ultimately a string
                // https://github.com/Kotlin/kotlinx.serialization/blob/master/runtime/commonMain/src/kotlinx/serialization/json/internal/StreamingJsonInput.kt#L111
                return IntOrString(decoder.decodeString().toInt())
            }
        }
    }

    @Serializable
    class Photo(val id: String)
    @Serializable
    class Photos(val photo: List<Photo>)
    @Serializable
    class SearchResult(val photos: Photos)
    @Serializable
    class Size(val width: IntOrString, val height: IntOrString, val source: String)
    @Serializable
    class Sizes(val size: List<Size>)
    @Serializable
    class SizeResult(val sizes: Sizes)

    private val ktorClient by lazy {
        HttpClient {
            install(JsonFeature) {
                serializer = KotlinxSerializer(Json.nonstrict).apply {
                    setMapper(SearchResult::class, SearchResult.serializer())
                    setMapper(SizeResult::class, SizeResult.serializer())
                }
            }
        }
    }

    /**
     * This will return the biggest image of the first result of the geo query
     */
    suspend fun search(latitude: Double, longitude: Double): String? {
        val searchResult = try {
            ktorClient.get<SearchResult>("https://www.flickr.com/services/rest/?method=flickr.photos.search&api_key=$apiKey&lat=$latitude&lon=$longitude&radius=0.05&format=json&nojsoncallback=1")
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        val photo = searchResult.photos.photo.firstOrNull()
        if (photo == null) {
            return null
        }

        val sizeResult = try {
            ktorClient.get<SizeResult>("https://www.flickr.com/services/rest/?method=flickr.photos.getSizes&api_key=$apiKey&photo_id=${photo.id}&format=json&nojsoncallback=1")
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        val orderdBySize = sizeResult.sizes.size.sortedBy {
            it.width.value * it.height.value
        }

        return orderdBySize.lastOrNull()?.source
    }
}