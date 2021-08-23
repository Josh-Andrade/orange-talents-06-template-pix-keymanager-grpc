package br.com.ot6.managekey

import br.com.ot6.managekey.domain.PixKey
import io.micronaut.data.annotation.Repository
import io.micronaut.data.jpa.repository.JpaRepository
import java.util.*

@Repository
interface PixKeyRepository : JpaRepository<PixKey, UUID> {

    fun existsByKey(key: String?): Boolean
    fun findByIdAndClienteId(pixId: UUID, clientId: UUID): Optional<PixKey>
}