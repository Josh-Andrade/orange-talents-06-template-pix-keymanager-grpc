package br.com.ot6.handler

import io.grpc.Status

abstract class Exception(override val message: String, val status: Status): Throwable(){
}