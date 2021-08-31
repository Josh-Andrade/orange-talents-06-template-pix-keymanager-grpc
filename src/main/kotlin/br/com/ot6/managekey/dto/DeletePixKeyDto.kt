package br.com.ot6.managekey.dto

import br.com.ot6.validation.ValidUUID
import io.micronaut.core.annotation.Introspected
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size
import kotlin.math.max

@Introspected
data class DeletePixKeyDto(
    @field:ValidUUID @field:NotBlank val clientId: String?,
    @field:ValidUUID @field:NotBlank @field:Size(max = 77) val pixId: String?
) {

}
