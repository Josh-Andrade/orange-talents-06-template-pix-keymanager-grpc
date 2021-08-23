package br.com.ot6.bcb

import br.com.ot6.itau.ClientAccountResponse
import br.com.ot6.managekey.dto.NewPixKeyDto

data class CreatePixKeyRequest(
    val keyType: KeyTypeBcb,
    val key: String?,
    val bankAccount: BankAccount,
    val owner: Owner
) {
    constructor(
        clientAccountResponse: ClientAccountResponse?,
        dto: NewPixKeyDto?
    ) : this(
        KeyTypeBcb.getBcbTypeKey(dto?.keyType),
            dto?.key, BankAccount(
            clientAccountResponse?.instituicao?.ispb,
            clientAccountResponse?.agencia,
            clientAccountResponse?.numero,
            AccountTypeBcb.getAccountTypeBcb(clientAccountResponse?.tipo)),
        Owner(
            PersonType.NATURAL_PERSON,
            clientAccountResponse?.titular?.nome,
            clientAccountResponse?.titular?.cpf)
    )
}
