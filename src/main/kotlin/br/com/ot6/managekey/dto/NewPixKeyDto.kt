package br.com.ot6.managekey.dto

import br.com.ot6.AccountType
import br.com.ot6.shared.KeyType
import br.com.ot6.validation.ValidKey
import br.com.ot6.validation.ValidUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull
import javax.validation.constraints.Size

@ValidKey
@Introspected
data class NewPixKeyDto(
    @field:ValidUUID @field:NotBlank(message = "Cliente id não deve ser nulo") val clientId: String?,
    @field:Size(max= 77, message = "tamanho da chave pix deve ter no máximo 77 caracteres") val key: String?,
    @field:NotNull val keyType: KeyType?,
    @field:NotNull val accountType: AccountType?
) {

}
