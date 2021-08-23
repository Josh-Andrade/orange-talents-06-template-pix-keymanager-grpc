package br.com.ot6.managekey.dto

import io.micronaut.core.annotation.Introspected
import java.util.*
import javax.validation.constraints.NotNull

@Introspected
data class DeletePixKeyDto(
    @field:NotNull val clientId: UUID,
    @field:NotNull val pixId: UUID
) {

}
