package lab.wilump.restclient

import labs.wilump.support.testing.dummyapi.JsonPlaceHolderApi
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatThrownBy
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.HttpClientErrorException
import org.springframework.web.client.RestClient

class RestClientTest {

    private lateinit var restClient: RestClient

    @BeforeEach
    fun setUp() {
        restClient = RestClient.create()
    }

    @Test
    fun `RestClient를 통해 ResponseEntity를 반환받는다`() {
        val response: ResponseEntity<String> = restClient.get()
            .uri("https://www.google.com")
            .retrieve()
            .toEntity(String::class.java)

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
        assertThat(response.body).isNotBlank
    }

    @Test
    fun `RestClient를 통해 HttpStatus를 반환받는다`() {
        val response: ResponseEntity<Void> = restClient.get()
            .uri("https://www.google.com")
            .retrieve()
            .toBodilessEntity()

        assertThat(response.statusCode).isEqualTo(HttpStatus.OK)
    }

    @Test
    fun `RestClient를 통해 ResponseBody를 반환받는다`() {
        val api = JsonPlaceHolderApi.GET_USERS

        val response: ResponseEntity<Array<JsonPlaceHolderApi.User>> = restClient.get()
            .uri(api.url)
            .retrieve()
            .toEntity(api.responseFormat)

        val statusCode = response.statusCode.value()
        assertThat(statusCode).isEqualTo(api.statusCode)

        val body = response.body?.toList()
        if (body != null) {
            assertThat(body.size).isEqualTo(10)
        }

        /**
         * body[0].id == 1
         * body[0].name == "Leanne Graham"
         * body[0].address.street == "Kulas Light"
         * body[0].address.geo.lat == "-37.3159"
         * body[0].company.name == "Romaguera-Crona"
         */
        val firstElement = body?.get(0)
        if (firstElement != null) {
            assertThat(firstElement.id).isEqualTo(1)
            assertThat(firstElement.name).isEqualTo("Leanne Graham")
            assertThat(firstElement.address.street).isEqualTo("Kulas Light")
            assertThat(firstElement.address.geo.lat).isEqualTo("-37.3159")
            assertThat(firstElement.company.name).isEqualTo("Romaguera-Crona")
        }
    }

    @Test
    fun `RestClient를 통해 요청 시 에러가 발생하면 HttpClientErrorException 예외가 발생한다`() {
        assertThatThrownBy {
            restClient.get()
                .uri("https://www.google.com/404")
                .retrieve()
                .toBodilessEntity()
        }.isInstanceOf(HttpClientErrorException::class.java)
    }

    @Test
    fun `RestClient를 통해 요청 시 에러가 발생했을 때 응답 상태 코드를 조회할 수 있다`() {
        runCatching {
            restClient.get()
                .uri("https://www.google.com/404")
                .retrieve()
                .toBodilessEntity()
        }.onFailure {
            val exception = it as HttpClientErrorException
            assertThat(exception).isInstanceOf(HttpClientErrorException::class.java)
            assertThat(exception.statusCode).isEqualTo(HttpStatus.NOT_FOUND)
        }
    }
}