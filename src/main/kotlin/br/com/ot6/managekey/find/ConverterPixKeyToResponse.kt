package br.com.ot6.managekey.find

import br.com.ot6.Account
import br.com.ot6.FindPixKeyResponse
import br.com.ot6.Owner
import br.com.ot6.itau.Instituicao
import br.com.ot6.managekey.domain.PixKey
import com.google.protobuf.Timestamp
import java.time.ZoneOffset

class ConverterPixKeyToResponse {

    companion object{
        fun convert(pixkey: PixKey?): FindPixKeyResponse {
            return FindPixKeyResponse.newBuilder()
                .setClientId(pixkey?.clienteId.toString() ?: "")
                .setPixKeyId(pixkey?.id.toString() ?: "")
                .setKeyType(pixkey?.keyType?.returnEquivalentEnum())
                .setKey(pixkey?.key)
                .setOwner(
                    Owner.newBuilder()
                    .setDocument(pixkey?.owner?.document)
                    .setName(pixkey?.owner?.name)
                    .build())
                .setAccount(
                    Account.newBuilder()
                    .setBankName(Instituicao.find(pixkey?.bankAccount?.participant!!))
                    .setAccountType(pixkey.accountType)
                    .setAccountNumber(pixkey.bankAccount.accountNumber)
                    .setAgency(pixkey.bankAccount.branch)
                    .build())
                .setCreatedAt(
                    pixkey.createdAt.let {
                        val instant = pixkey.createdAt!!.toInstant(ZoneOffset.UTC)
                        Timestamp.newBuilder()
                            .setNanos(instant.nano)
                            .setSeconds(instant.epochSecond)
                            .build()
                    }
                    )
                .build()
        }
    }
}
