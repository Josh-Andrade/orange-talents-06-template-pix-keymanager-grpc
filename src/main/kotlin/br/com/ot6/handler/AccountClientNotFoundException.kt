package br.com.ot6.handler

import io.grpc.Status

class AccountClientNotFoundException(override val message: String) : Exception(message, Status.NOT_FOUND) {

}
