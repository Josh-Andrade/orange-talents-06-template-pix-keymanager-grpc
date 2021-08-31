package br.com.ot6.handler

import io.grpc.Status

class PixKeyInvalidException(override val message: String) : Exception(message, Status.NOT_FOUND) {

}
