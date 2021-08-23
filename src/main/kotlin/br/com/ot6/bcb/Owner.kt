package br.com.ot6.bcb

import br.com.ot6.managekey.domain.Owner

data class Owner(
    val type: PersonType,
    val name: String?,
    val taxIdNumber: String?
) {

    fun toModel(): Owner {
        return Owner(this)
    }
}
