package com.shahin.core.network.books.model.serialization

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.JsonParseException
import com.shahin.core.network.books.model.BookItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test


class BookItemDeserializerTest {

    private lateinit var gson: Gson

    @Before
    fun setUp() {
        gson = GsonBuilder()
            .registerTypeAdapter(BookItem::class.java, BookItemDeserializer())
            .create()
    }

    @Test
    fun test_deserializing_a_valid_book_item_Json_response() {
        val json = """
            {
                "id": 1,
                "title": "Book title",
                "description": "This is a test book",
                "author": "Author",
                "release_date": "2023-01-01",
                "image": "image_url"
            }
        """
        val bookItem = gson.fromJson(json, BookItem::class.java)

        assertNotNull(bookItem)
        assertEquals(1, bookItem.id)
        assertEquals("Book title", bookItem.title)
        assertEquals("This is a test book", bookItem.description)
        assertEquals("Author", bookItem.author)
        assertEquals("2023-01-01", bookItem.releaseDate)
        assertEquals("image_url", bookItem.image)
    }

    @Test
    fun test_deserializing_a_valid_book_item_Json_response_when_id_is_a_negative_value() {
        val json = """
            {
                "id": -5,
                "title": "Book title",
                "description": "This is a test book",
                "author": "Author",
                "release_date": "2023-01-01",
                "image": "image_url"
            }
        """
        val bookItem = gson.fromJson(json, BookItem::class.java)

        assertNotNull(bookItem)
        assertEquals(-5, bookItem.id)
        assertEquals("Book title", bookItem.title)
        assertEquals("This is a test book", bookItem.description)
        assertEquals("Author", bookItem.author)
        assertEquals("2023-01-01", bookItem.releaseDate)
        assertEquals("image_url", bookItem.image)
    }

    @Test
    fun test_deserializing_a_book_item_Json_with_missing_fields() {
        val json = """
            {
                "id": 2,
                "title": "Book title"
            }
        """
        val bookItem = gson.fromJson(json, BookItem::class.java)

        assertNotNull(bookItem)
        assertEquals(2, bookItem.id)
        assertEquals("Book title", bookItem.title)
        assertNull(bookItem.description)
        assertNull(bookItem.author)
        assertNull(bookItem.releaseDate)
        assertNull(bookItem.image)
    }

    @Test
    fun test_deserializing_a_book_item_when_title_field_name_is_the_alternative_typo() {
        val json = """
            {
                "id": -5,
                "titlee": "Title typo",
                "description": "Description",
                "author": "Author",
                "release_date": "2023-02-02",
                "image": "image_url"
            }
        """
        val bookItem = gson.fromJson(json, BookItem::class.java)

        assertNotNull(bookItem)
        assertEquals(-5, bookItem.id) // negative id value is not a concern
        assertEquals("Title typo", bookItem.title)
        assertEquals("Description", bookItem.description)
        assertEquals("Author", bookItem.author)
        assertEquals("2023-02-02", bookItem.releaseDate)
        assertEquals("image_url", bookItem.image)
    }

    @Test
    fun test_deserializing_an_book_item_with_an_id_as_string_and_some_missing_fields() {
        val json = """
            {
                "id": "1",
                "title": "Title"
            }
        """
        val bookItem = gson.fromJson(json, BookItem::class.java)
        assertEquals(1, bookItem.id)
        assertEquals("Title", bookItem.title)
        assertNull(bookItem.description)
        assertNull(bookItem.author)
        assertNull(bookItem.releaseDate)
        assertNull(bookItem.image)
    }

    /**
     * In case we receive a completely invalid Json in our responses
     * The [JsonParseException] will be caught as [com.shahin.core.network.model.NetworkResponse.ClientError]
     * in [com.shahin.core.network.NetworkResponseWrapper]
     */
    @Test(expected = JsonParseException::class)
    fun test_deserialize_an_invalid_Json_altogether() {
        val json = """
                "id": 3,
                "titlee": "Title with Alternative Field Name",
                "description": "Description",
                "author": "Author",
                "release_date": "2023-02-02",
                "image": "image_url"
        """
        gson.fromJson(json, BookItem::class.java)
    }

    /**
     * In case we receive a completely invalid Json in our responses
     * The [JsonParseException] will be caught as [com.shahin.core.network.model.NetworkResponse.ClientError]
     * in [com.shahin.core.network.NetworkResponseWrapper]
     */
    @Test(expected = Exception::class)
    fun test_deserialize_an_response_where_id_is_not_provided() {
        val json = """
              {
                "titlee": "Title typo",
                "description": "Description",
                "author": "Author",
                "release_date": "2023-02-02",
                "image": "image_url"
              }
        """
        gson.fromJson(json, BookItem::class.java)
    }

}