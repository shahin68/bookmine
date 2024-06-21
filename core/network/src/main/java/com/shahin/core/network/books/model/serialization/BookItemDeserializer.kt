package com.shahin.core.network.books.model.serialization

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import com.shahin.core.network.books.model.BookItem
import java.lang.reflect.Type

/**
 * Deserializer for [BookItem]
 *
 * This deserialization is utilized since we have a typo in one of our fields [titlee] instead of [title]
 * This also means that our remote response isn't a reliable source truth, which means we will only
 * use [BookItem] to parse the response and won't be directly using it while creating UI or Database.
 *
 * We will map [BookItem] to appropriate models.
 *
 * @throws JsonParseException In case we receive a completely invalid Json in our responses
 * The [JsonParseException] will be caught as [com.shahin.core.network.model.NetworkResponse.ClientError]
 * in [com.shahin.core.network.NetworkResponseWrapper]
 *
 * @throws [Exception] In case remote server provides a [BookItem] with missing id field
 * The [Exception] will be caught as [com.shahin.core.network.model.NetworkResponse.ClientError]
 */
class BookItemDeserializer : JsonDeserializer<BookItem> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?,
    ): BookItem {
        val jsonObject = json?.asJsonObject
        return BookItem(
            id = jsonObject?.get("id")?.asInt
                ?: throw Exception("Failed to deserialize response: Remote server did not provide an id for at least one book item"),
            title = jsonObject.get("title")?.asString
                ?: jsonObject.get("titlee")?.asString.orEmpty(),
            description = jsonObject.get("description")?.asString,
            author = jsonObject.get("author")?.asString,
            releaseDate = jsonObject.get("release_date")?.asString,
            image = jsonObject.get("image")?.asString
        )
    }
}