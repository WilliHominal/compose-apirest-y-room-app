package com.warh.jc_practice3

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.warh.jc_practice3.datasource.RestDataSource
import com.warh.jc_practice3.model.User
import com.warh.jc_practice3.model.UserDao
import com.warh.jc_practice3.repository.UserRepositoryImpl
import kotlinx.coroutines.runBlocking
import okhttp3.OkHttpClient
import okhttp3.mockwebserver.Dispatcher
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import okhttp3.mockwebserver.RecordedRequest
import okio.buffer
import okio.source
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.nio.charset.StandardCharsets

private val user1 = User("name1", "lastname1", "city1", "http://..1")
private val user2 = User("name2", "lastname2", "city2", "http://..2")

class UserRepositoryTest {

    private val mockWebServer = MockWebServer().apply {
        url("/")
        dispatcher = myDispatcher
    }

    private val restDataSource = Retrofit.Builder()
        .baseUrl(mockWebServer.url("/"))
        .client(OkHttpClient.Builder().build())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(RestDataSource::class.java)

    private val userRepository = UserRepositoryImpl(restDataSource, MockUserDao())

    @get:Rule
    val rule = InstantTaskExecutorRule()

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    @Test
    fun `Users on the DB are retrieved correctly`() {
        val users = userRepository.getAllUser()
        assertEquals(2, users.value?.size)
    }

    @Test
    fun `User was deleted correctly`() {
        runBlocking {
            userRepository.deleteUser(user1)

            val users = userRepository.getAllUser()
            assertEquals(1, users.value?.size)
        }
    }

    @Test
    fun `User was fetched correctly`() {
        runBlocking {
            val newUser = userRepository.getNewUser()

            val users = userRepository.getAllUser()
            assertEquals(3, users.value?.size)
            assertEquals("Dave", newUser.name)
            assertEquals("Alexander", newUser.lastName)
            assertEquals("Coffs Harbour", newUser.city)
            assertEquals("https://randomuser.me/api/portraits/thumb/men/41.jpg", newUser.thumbnail)
        }
    }
}

class MockUserDao: UserDao{

    private val users = MutableLiveData(listOf(user1, user2))

    override fun insert(user: User) {
        users.value = users.value?.toMutableList()?.apply { add(user) }
    }

    override fun getAll(): LiveData<List<User>>  = users

    override fun delete(user: User) {
        users.value = users.value?.toMutableList()?.apply { remove(user) }
    }
}

val myDispatcher: Dispatcher = object : Dispatcher(){
    override fun dispatch(request: RecordedRequest): MockResponse {
        return when (request.path){
            "/?inc=name" -> MockResponse().apply { addResponse("api_name.json") }
            "/?inc=location" -> MockResponse().apply { addResponse("api_location.json") }
            "/?inc=picture" -> MockResponse().apply { addResponse("api_picture.json") }
            else -> MockResponse().setResponseCode(404)
        }
    }
}

fun MockResponse.addResponse(filePath: String): MockResponse {
    val inputStream = javaClass.classLoader?.getResourceAsStream(filePath)
    val source = inputStream?.source()?.buffer()
    source?.let {
        setResponseCode(200)
        setBody(it.readString(StandardCharsets.UTF_8))
    }
    return this
}