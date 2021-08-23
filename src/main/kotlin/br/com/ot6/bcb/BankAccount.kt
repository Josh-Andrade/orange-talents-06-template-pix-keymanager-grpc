package br.com.ot6.bcb

import br.com.ot6.managekey.domain.BankAccount

data class BankAccount(
    val participant: String?,
    val branch: String?,
    val accountNumber: String?,
    val accountType: AccountTypeBcb?
) {

    fun toModel(nome:String?): BankAccount {
        return BankAccount(this, nome)
    }
}
