package br.com.ot6.bcb

import br.com.ot6.AccountType

enum class AccountTypeBcb(val equivalent: AccountType) {
    CACC(AccountType.CONTA_CORRENTE),
    SVGS(AccountType.CONTA_POUPANCA);

    companion object{
        fun getAccountTypeBcb(accountType: AccountType?): AccountTypeBcb?{
            values().forEach { accountTypeRequest ->
                if(accountTypeRequest.equivalent == accountType)
                    return accountTypeRequest
            }
            return null
        }
    }
}
