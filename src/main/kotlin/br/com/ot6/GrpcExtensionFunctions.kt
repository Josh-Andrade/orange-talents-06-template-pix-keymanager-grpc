package br.com.ot6

import br.com.ot6.managekey.dto.DeletePixKeyDto
import br.com.ot6.managekey.dto.NewPixKeyDto
import java.util.*

fun RegisterPixKeyRequest.toModel(): NewPixKeyDto {
    return NewPixKeyDto(
        this.clientId,
        this.key,
        when (this.keyType) {
            KeyType.UNKNOWN_TIPO_CHAVE -> null
            else -> br.com.ot6.shared.KeyType.valueOf(keyType.name)
        },
        when(this.accountType){
            AccountType.UNKNOWN_TIPO_CONTA -> null
            else -> AccountType.valueOf(accountType.name)
        }
    )
}

fun DeletePixKeyRequest.toModel(): DeletePixKeyDto {
    return DeletePixKeyDto(UUID.fromString(this.clienteId), UUID.fromString(this.pixKeyId))
}