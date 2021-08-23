package br.com.ot6.itau

import br.com.ot6.AccountType

data class ClientAccountResponse(
    val tipo: AccountType,
    val instituicao: Instituicao,
    val agencia: String,
    val numero: String,
    val titular: Titular
) {

}
