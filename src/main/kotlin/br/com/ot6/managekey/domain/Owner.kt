package br.com.ot6.managekey.domain

import br.com.ot6.bcb.Owner
import javax.persistence.Embeddable

@Embeddable
data class Owner(
    val name: String?,
    val document: String?
) {
    constructor(owner: Owner) : this(owner.name, owner.taxIdNumber)
}