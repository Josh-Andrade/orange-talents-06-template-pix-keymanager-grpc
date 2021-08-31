package br.com.ot6.managekey.find

import br.com.ot6.BcbClient
import br.com.ot6.handler.PixKeyInvalidException
import br.com.ot6.handler.PixKeyNotFoundException
import br.com.ot6.managekey.PixKeyRepository
import br.com.ot6.managekey.domain.PixKey
import br.com.ot6.validation.ValidUUID
import io.micronaut.core.annotation.Introspected
import io.micronaut.http.HttpStatus
import java.lang.IllegalArgumentException
import java.util.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

@Introspected
sealed class Filter {

    abstract fun findPixKey(repository: PixKeyRepository, bcbClient: BcbClient): PixKey

    @Introspected
    data class FilterByPixId(
        @field:NotBlank @field:ValidUUID val pixId: String,
        @field:NotBlank @field:ValidUUID val clientId: String): Filter() {

        override fun findPixKey(repository: PixKeyRepository, bcbClient: BcbClient): PixKey {
            return repository.findByIdAndClienteId(UUID.fromString(pixId), UUID.fromString(clientId))
                .orElseThrow { throw PixKeyNotFoundException() }
        }

    }

    @Introspected
    data class FilterByKey(@field:NotBlank @field:Size(max = 77) val key: String): Filter(){

        override fun findPixKey(repository: PixKeyRepository, bcbClient: BcbClient): PixKey {
            return repository.findByKey(key)
                .orElseGet{
                    val response = bcbClient.findPixKey(key)
                    when(response.status){
                        HttpStatus.OK -> response.body()?.toDomain()
                        else -> throw PixKeyNotFoundException()
                    }
                }
        }

    }

    @Introspected
    class Invalid : Filter() {
        override fun findPixKey(repository: PixKeyRepository, bcbClient: BcbClient): PixKey {
            throw PixKeyInvalidException("Chave pix invalida ou não informada")
        }

    }
}
