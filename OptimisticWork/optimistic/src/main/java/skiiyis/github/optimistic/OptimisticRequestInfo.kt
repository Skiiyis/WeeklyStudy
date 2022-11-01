package skiiyis.github.optimistic

data class OptimisticRequestInfo(
    val method: String,
    val baseUrl: String,
    val header: Set<String>?
)