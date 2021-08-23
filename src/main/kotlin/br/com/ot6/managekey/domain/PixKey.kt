package br.com.ot6.managekey.domain

import br.com.ot6.AccountType
import br.com.ot6.bcb.PixKeyDetailsResponse
import br.com.ot6.itau.ClientAccountResponse
import br.com.ot6.shared.KeyType
import org.hibernate.annotations.GenericGenerator
import java.time.LocalDateTime
import java.util.*
import javax.persistence.*
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

@Entity
data class PixKey(
    @Column(unique = true, nullable = false) @field:NotBlank val key: String?,
    @Enumerated(EnumType.STRING) @field:NotNull val accountType: AccountType?,
    @Enumerated(EnumType.STRING) @field:NotNull val keyType: KeyType?,
    @Column(nullable = false) @field:NotBlank val clienteId: UUID?,
    @Column(nullable = false) @field:NotBlank val createdAt: LocalDateTime?,
    @Column(nullable = false) @field:NotNull @field:Embedded val bankAccount: BankAccount?,
    @Column(nullable = false) @field:NotBlank val owner: Owner?,
) {
    constructor(response: PixKeyDetailsResponse?, accountResponse: ClientAccountResponse?) : this(
        response?.key,
        response?.bankAccount?.accountType?.equivalent,
        response?.keyType?.equivalent,
        accountResponse?.titular?.id,
        response?.createdAt,
        response?.bankAccount?.toModel(accountResponse?.instituicao?.nome),
        response?.owner?.toModel()
    )


    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    var id: UUID? = null


}