package br.com.ot6.handler

import io.grpc.Status

abstract class ExceptionHandler(override val message: String, val status: Status): Throwable(){
}