package com.rjschulz

import io.kotest.core.spec.style.FunSpec
import io.kotest.matchers.ints.shouldBeExactly
import io.kotest.matchers.string.shouldMatch
import io.mockk.every
import io.mockk.mockk
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaType

class SearchTest : FunSpec({

    val repository = mockk<GameRepository>()
    val httpClient = mockk<OkHttpClient>()
    val service = GameService(repository, httpClient)
    val mockCall = mockk<Call>()

    test("Successful API response with 1 results returns correctly") {

        every { httpClient.newCall(any()) } returns mockCall

        val mockRequest: Request = Request.Builder()
            .url("https://some-url.com")
            .build()

        val response = Response.Builder()
            .request(mockRequest)
            .protocol(Protocol.HTTP_2)
            .code(200) // status code
            .message("")
            .body(
                ResponseBody.create(
                    // TODO: put example responses in test files and load
                    "application/json; charset=utf-8".toMediaType(),
                    "{" +
                            "\"error\": \"OK\"," +
                            "\"limit\": 10," +
                            "\"offset\": 0," +
                            "\"number_of_page_results\": 10," +
                            "\"number_of_total_results\": 446," +
                            "\"status_code\": 1," +
                            "\"results\": [" +
                            "{" +
                            "\"image\": {" +
                            "\"icon_url\": \"https://www.giantbomb.com/a/uploads/square_avatar/0/1099/3029917-7592091195-71I3p.png\"," +
                            "\"medium_url\": \"https://www.giantbomb.com/a/uploads/scale_medium/0/1099/3029917-7592091195-71I3p.png\"," +
                            "\"screen_url\": \"https://www.giantbomb.com/a/uploads/screen_medium/0/1099/3029917-7592091195-71I3p.png\"," +
                            "\"screen_large_url\": \"https://www.giantbomb.com/a/uploads/screen_kubrick/0/1099/3029917-7592091195-71I3p.png\"," +
                            "\"small_url\": \"https://www.giantbomb.com/a/uploads/scale_small/0/1099/3029917-7592091195-71I3p.png\"," +
                            "\"super_url\": \"https://www.giantbomb.com/a/uploads/scale_large/0/1099/3029917-7592091195-71I3p.png\"," +
                            "\"thumb_url\": \"https://www.giantbomb.com/a/uploads/scale_avatar/0/1099/3029917-7592091195-71I3p.png\"," +
                            "\"tiny_url\": \"https://www.giantbomb.com/a/uploads/square_mini/0/1099/3029917-7592091195-71I3p.png\"," +
                            "\"original_url\": \"https://www.giantbomb.com/a/uploads/original/0/1099/3029917-7592091195-71I3p.png\"," +
                            "\"image_tags\": \"All Images\"" +
                            "}," +
                            "\"name\": \"Pac-Man Stories\"," +
                            "\"resource_type\": \"game\"" +
                            "}" +
                            "]," +
                            "\"version\": \"1.0\"" +
                            "}"

                )
            )
            .build()

        every { mockCall.execute() } returns response

        val returnValue = service.searchGames("man")

        returnValue.size shouldBeExactly 1
        returnValue[0].name shouldMatch  "Pac-Man Stories"

    }

})