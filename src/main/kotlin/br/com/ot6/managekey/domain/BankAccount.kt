package br.com.ot6.managekey.domain

import br.com.ot6.bcb.BankAccount
import javax.persistence.Embeddable

@Embeddable
data class BankAccount(val branch:String?, val accountNumber: String?, val participant: String?) {

    constructor(bankAccount: BankAccount, nome: String?) : this(bankAccount.branch, bankAccount.accountNumber, "${bankAccount.participant} $nome")
}