package br.com.ot6.bcb

import br.com.ot6.managekey.domain.PixKey
import java.time.LocalDateTime

data class PixKeyDetailsResponse(
    val keyType: KeyTypeBcb,
    val key: String?,
    val bankAccount: BankAccount,
    val owner: Owner,
    val createdAt: LocalDateTime?
) {
    fun toDomain(): PixKey {
        return PixKey(key,
            bankAccount.accountType?.equivalent,
            keyType.equivalent,
            null,
            createdAt,
            br.com.ot6.managekey.domain.BankAccount(bankAccount.branch, bankAccount.accountNumber, bankAccount.participant),
            br.com.ot6.managekey.domain.Owner(owner.name, owner.taxIdNumber))
    }


}
