package net.mbonnin.photostream

import kotlinx.coroutines.runBlocking
import org.junit.Test

import org.junit.Assert.*

class TestFlickr {

    @Test
    fun testSearch() {
        runBlocking {
            val url = FlickrApi.search(48.8870698,2.304513)
            println("url=$url")
            assert(url != null)
        }
    }
}
