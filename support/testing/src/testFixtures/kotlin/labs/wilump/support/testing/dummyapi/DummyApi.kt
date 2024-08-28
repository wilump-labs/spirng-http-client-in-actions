package labs.wilump.support.testing.dummyapi

class DummyApi<T>(
    val url: String,
    val method: String,
    val statusCode: Int,
    val responseFormat: Class<T>
)