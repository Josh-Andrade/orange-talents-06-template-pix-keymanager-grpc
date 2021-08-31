package br.com.ot6

import br.com.ot6.FindPixKeyRequest.FilterCase.*
import br.com.ot6.managekey.find.Filter
import br.com.ot6.managekey.dto.DeletePixKeyDto
import br.com.ot6.managekey.dto.NewPixKeyDto
import javax.validation.ConstraintViolationException
import javax.validation.Validator

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
    return DeletePixKeyDto(this.clienteId, this.pixKeyId)
}


fun FindPixKeyRequest.toModel(validator: Validator): Filter {
    val filter: Filter = when(filterCase){
        FILTERPIXID -> filterPixId.let {
            Filter.FilterByPixId(it.pixKeyId ,it.clienteId)
        }
        KEY -> Filter.FilterByKey(key)
        FILTER_NOT_SET -> Filter.Invalid()
    }
    val violations = validator.validate(filter)
    if(violations.isNotEmpty()){
        throw ConstraintViolationException(violations)
    }

    return filter
}
