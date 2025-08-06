package com.github.muellerma.nfcreader

import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.MockitoAnnotations
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.whenever
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

//@RunWith(AndroidJUnit4::class) crashing despite installation
@RunWith(JUnit4::class)
class ApiTest {

    @Mock
    lateinit var api: ApiService

    @Mock
    lateinit var call: Call<TechSupportRequest>

    lateinit var repository: TechSupportRepository

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)
        repository = TechSupportRepository(api)
    }

    @Test
    fun `sendDataToApi callback test success`() {
        val request = TechSupportRequest("test@example.com", "123", "https://somelink.com", "NTAG216", 0)
        val callbackCaptor = argumentCaptor<Callback<TechSupportRequest>>()

        whenever(api.sendTechSupport(request)).thenReturn(call)

        var success: Boolean? = null
        var error: Throwable? = null

        repository.sendDataToApi(request) { s, e ->
            success = s
            error = e
        }

        verify(call).enqueue(callbackCaptor.capture())

        // Simulate successful response
        val response = Response.success(request)
        callbackCaptor.firstValue.onResponse(call, response)

        assertTrue(success == true)
        assertNull(error)
    }

    @Test
    fun `sendDataToApi callback failure`() {
        val request = TechSupportRequest("test@example.com", "123", "https://notlink.com", "NTAG215", 0)
        val callbackCaptor = argumentCaptor<Callback<TechSupportRequest>>()

        whenever(api.sendTechSupport(request)).thenReturn(call)

        var success: Boolean? = null
        var error: Throwable? = null

        repository.sendDataToApi(request) { s, e ->
            success = s
            error = e
        }

        verify(call).enqueue(callbackCaptor.capture())

        //Simulate an network error
        val throwable = Throwable("Network error")
        callbackCaptor.firstValue.onFailure(call, throwable)

        assertFalse(success == true)
        assertEquals("Network error", error?.message)
    }

    class TechSupportRepository(private val api: ApiService) {

        fun sendDataToApi(request: TechSupportRequest, callback: (Boolean, Throwable?) -> Unit) {
            api.sendTechSupport(request).enqueue(object : Callback<TechSupportRequest> {
                override fun onResponse(call: Call<TechSupportRequest>, response: Response<TechSupportRequest>) {
                    callback(response.isSuccessful, null)
                }

                override fun onFailure(call: Call<TechSupportRequest>, t: Throwable) {
                    callback(false, t)
                }
            })
        }
    }
}

