package br.com.ot6.handler

import io.grpc.Status

class PixKeyAlreadyExistsException(override val message: String) :
    Exception(message, Status.ALREADY_EXISTS){

}
