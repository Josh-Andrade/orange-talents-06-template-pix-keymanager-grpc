package br.com.ot6.managekey

import br.com.ot6.*
import br.com.ot6.handler.ErrorInterceptor
import io.grpc.Status
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorInterceptor
class RegisterKeyEndPoint(
    @Inject val registerKeyService: RegisterKeyService
) : KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceImplBase() {

    val logger = LoggerFactory.getLogger(this::class.java)

    override fun register(request: RegisterPixKeyRequest?,
                          responseObserver: StreamObserver<RegisterPixKeyResponse>?) {

        logger.info("Iniciando processo de registro de nova chave pix")
        val pixKeyRegister = registerKeyService.register(request?.toModel())

        responseObserver?.onNext(
            RegisterPixKeyResponse
            .newBuilder()
                .setPixId(pixKeyRegister?.id.toString())
                .build())

        logger.info("Finalizando chamada gRPC")
        responseObserver?.onCompleted()
    }

}
