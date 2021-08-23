package br.com.ot6.managekey

import br.com.ot6.BcbClient
import br.com.ot6.ErpItauClient
import br.com.ot6.bcb.DeletePixKey
import br.com.ot6.handler.ChavePixBcbDeleteException
import br.com.ot6.handler.PixKeyNotFoundException
import br.com.ot6.itau.ClientResponse
import br.com.ot6.managekey.domain.PixKey
import br.com.ot6.managekey.dto.DeletePixKeyDto
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Singleton
@Validated
class DeleteKeyService(
    @Inject val itauClient: ErpItauClient,
    @Inject val bcbClient: BcbClient,
    @Inject val repository: PixKeyRepository
) {

    val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun delete(@Valid deletePixKeyDto: DeletePixKeyDto?): String? {
        val key = deleteFromBcb(findPixKey(deletePixKeyDto), findClientFromItau(deletePixKeyDto!!))
        repository.deleteById(deletePixKeyDto.pixId)
        return key
    }

    private fun findPixKey(deletePixKeyDto: DeletePixKeyDto?): PixKey? {
        logger.info("Verificando se chave pix existe")
        return repository.findByIdAndClienteId(deletePixKeyDto!!.pixId, deletePixKeyDto.clientId)
            .orElseThrow() {
                throw PixKeyNotFoundException("Chave pix não encontrada")
            }
    }

    private fun findClientFromItau(deletePixKeyDto: DeletePixKeyDto): HttpResponse<ClientResponse> {
        logger.info("Buscando dados do cliente")
        return itauClient.buscarDadosClient(deletePixKeyDto.clientId.toString())
    }

    private fun deleteFromBcb(
        optionalPixKey: PixKey?,
        clientResponse: HttpResponse<ClientResponse>
    ): String? {
        logger.info("Deletando chave pix no banco central do brasil")
        val deletePixKey = bcbClient.deletePixKey(
            optionalPixKey?.key.toString(),
            DeletePixKey(optionalPixKey?.key.toString(), clientResponse.body()!!.instituicao.ispb)
        )
        if (deletePixKey.status != HttpStatus.OK) {
            throw ChavePixBcbDeleteException(
                "Ocorreu um problema ao tentar deletar a chave pix com o Banco central",
                deletePixKey.status
            )
        }
        return deletePixKey.body()?.key
    }
}
