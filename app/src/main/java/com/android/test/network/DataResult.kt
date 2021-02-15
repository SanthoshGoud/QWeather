package com.android.test.network

/**
 * Generic data results that will be returned by the builder, the builder is responsible
 * to assign respective entity or entities as a result
 * T could be the response JSON/XML  model that to be given to the resisted UI Activity/class
 *
 * @param <T>
</T> */
class DataResult<T> {
    var successful = false
    var result: String? = null
    var message: String? = null
    var statusCode = 0
    var entity: T? = null
    var entities: List<T>? = null
    var UUID: String? = null
}