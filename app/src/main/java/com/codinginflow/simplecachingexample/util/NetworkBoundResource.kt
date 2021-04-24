package com.codinginflow.simplecachingexample.util

import kotlinx.coroutines.flow.*

inline fun <ResultType, RequestType> networkBoundResource(
        crossinline query: () -> Flow<ResultType>,
        crossinline fetch: suspend () -> RequestType,
        crossinline saveFetchResult: suspend (RequestType) -> Unit,
        crossinline shouldFetch: (ResultType) -> Boolean = { true }
) = flow {
    val data = query().first()

    val flow = if (shouldFetch(data)) {
        // TODO: show a snackbar that data is being updated
        emit(Resource.Loading(data))

        try {
            saveFetchResult(fetch())
            query().map {
                Resource.Success(it)
            }
        } catch (throwable: Throwable) {
            //TODO: show snackbar that update failed
            query().map { Resource.Error(throwable, it) }
        }
    } else {
        query().map {
            Resource.Success(it)
        }
    }

    emitAll(flow)
}