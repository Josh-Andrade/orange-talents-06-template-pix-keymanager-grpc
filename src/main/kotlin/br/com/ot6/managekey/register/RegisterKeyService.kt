package br.com.ot6.managekey.register

import br.com.ot6.BcbClient
import br.com.ot6.ErpItauClient
import br.com.ot6.bcb.CreatePixKeyRequest
import br.com.ot6.handler.AccountClientNotFoundException
import br.com.ot6.handler.PixKeyAlreadyExistsException
import br.com.ot6.itau.ClientAccountResponse
import br.com.ot6.managekey.PixKeyRepository
import br.com.ot6.managekey.domain.PixKey
import br.com.ot6.managekey.dto.NewPixKeyDto
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.validation.Validated
import org.slf4j.LoggerFactory
import javax.inject.Inject
import javax.inject.Singleton
import javax.transaction.Transactional
import javax.validation.Valid

@Validated
@Singleton
class RegisterKeyService(
    @Inject val itauClient: ErpItauClient,
    @Inject val bcbClient: BcbClient,
    @Inject val repository: PixKeyRepository
) {

    val logger = LoggerFactory.getLogger(this::class.java)

    @Transactional
    fun register(@Valid dto: NewPixKeyDto?): PixKey? {
        verifyIfPixKeyExists(dto)
        return registerPixKeyOnBcbAndSave(dto, findClientAccountFromItau(dto))
    }

    private fun registerPixKeyOnBcbAndSave(
        dto: NewPixKeyDto?,
        accountClient: ClientAccountResponse?
    ): PixKey {
        try {
            logger.info("Registrando chave pix no banco central, tipo: ${dto?.keyType}")
            val bcbResponse = bcbClient.registerPixKey(CreatePixKeyRequest(accountClient, dto))
            return repository.save(PixKey(bcbResponse.body(), accountClient))
        } catch (e: HttpClientResponseException) {
            throw PixKeyAlreadyExistsException("Chave pix já existe no banco central")
        }
    }

    private fun findClientAccountFromItau(dto: NewPixKeyDto?): ClientAccountResponse? {
        logger.info("Buscando dados do cliente ${dto?.clientId} no ITAU")
        val accountClientResponse = itauClient.buscarContaClient(dto?.clientId, dto?.accountType)

        if (accountClientResponse.body() == null) {
            throw AccountClientNotFoundException("Cliente ${dto?.clientId} não encontrado")
        }
        return accountClientResponse.body()
    }

    private fun verifyIfPixKeyExists(dto: NewPixKeyDto?) {
        logger.info("Verificando se chave pix existe")
        if (repository.existsByKey(dto?.key)) {
            throw PixKeyAlreadyExistsException("Chave pix já existe")
        }
    }
}