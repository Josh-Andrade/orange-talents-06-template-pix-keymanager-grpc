package br.com.ot6.handler

import io.grpc.Status

class PixKeyNotFoundException():
    Exception(
        "Chave Pix não encontrada",
        Status.NOT_FOUND) {
}