package br.com.ot6.handler

import io.grpc.Status

class PixKeyNotFoundException(override val message: String): ExceptionHandler(message, Status.NOT_FOUND) {
}