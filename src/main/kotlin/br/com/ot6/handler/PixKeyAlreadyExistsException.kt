package br.com.ot6.handler

import io.grpc.Status

class PixKeyAlreadyExistsException(override val message: String) : ExceptionHandler(message, Status.ALREADY_EXISTS){

}
