package br.com.ot6.handler

import io.grpc.Status
import io.micronaut.http.HttpStatus

class ChavePixBcbDeleteException(override val message: String, status: HttpStatus)
    : ExceptionHandler(message,
    when(status){
        HttpStatus.NOT_FOUND -> Status.NOT_FOUND
        else -> Status.PERMISSION_DENIED
    }
) {

}
