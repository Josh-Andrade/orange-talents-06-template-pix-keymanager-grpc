package br.com.ot6.managekey.delete

import br.com.ot6.DeletePixKeyRequest
import br.com.ot6.DeletePixKeyResponse
import br.com.ot6.KeyManagerDeletaGrpcServiceGrpc
import br.com.ot6.handler.ErrorInterceptor
import br.com.ot6.toModel
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
@ErrorInterceptor
class DeleteKeyEndPoint(@Inject val deleteKeyService: DeleteKeyService) :
    KeyManagerDeletaGrpcServiceGrpc.KeyManagerDeletaGrpcServiceImplBase() {

    val logger = LoggerFactory.getLogger(this::class.java)

    override fun delete(request: DeletePixKeyRequest?, responseObserver: StreamObserver<DeletePixKeyResponse>?) {
        logger.info("Iniciando processo de deleção de chave pix")
        val deletedKey = deleteKeyService.delete(request?.toModel())
        logger.info("Finalizando deleção de chave pix")
        responseObserver?.onNext(DeletePixKeyResponse.newBuilder().setMessage("Chave pix $deletedKey deletada").build())
        responseObserver?.onCompleted()
    }
}