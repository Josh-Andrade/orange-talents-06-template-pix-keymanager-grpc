package br.com.ot6.managekey.find

import br.com.ot6.*
import br.com.ot6.handler.ErrorInterceptor
import br.com.ot6.managekey.PixKeyRepository
import io.grpc.stub.StreamObserver
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.validation.Validator

@Singleton
@ErrorInterceptor
class FindKeyEndPoint(
    @Inject val validator: Validator,
    @Inject val repository: PixKeyRepository,
    @Inject val bcbClient: BcbClient)
    : KeyManagerConsultaGrpcServiceGrpc.KeyManagerConsultaGrpcServiceImplBase(){

    val logger = LoggerFactory.getLogger(this::class.java)

    override fun loadPixKey(request: FindPixKeyRequest?, responseObserver: StreamObserver<FindPixKeyResponse>?) {
        logger.info("Iniciando busca por chave pix")
        val pixKey = request?.toModel(validator)?.findPixKey(repository, bcbClient)
        responseObserver?.onNext(ConverterPixKeyToResponse.convert(pixKey))
        logger.info("Finalizando busca por chave pix")
        responseObserver?.onCompleted()
    }
}
