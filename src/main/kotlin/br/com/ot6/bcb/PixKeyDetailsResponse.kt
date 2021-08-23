package br.com.ot6.bcb

import java.time.LocalDateTime

data class PixKeyDetailsResponse(
    val keyType: KeyTypeBcb,
    val key: String?,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime?
) {

}
