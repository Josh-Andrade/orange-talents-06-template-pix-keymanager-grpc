package br.com.ot6

import br.com.ot6.itau.ClientAccountResponse
import br.com.ot6.itau.ClientResponse
import io.micronaut.data.annotation.Query
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client
import java.util.*

@Client("\${http.services.erp-itau.url}")
interface ErpItauClient {

    @Get("/api/v1/clientes/{clienteId}/contas?tipo={tipo}")
    @Consumes(MediaType.APPLICATION_JSON)
    fun buscarContaClient(@QueryValue clienteId: String?, @QueryValue tipo: AccountType?):
            HttpResponse<ClientAccountResponse?>

    @Get("/api/v1/clientes/{clienteId}")
    fun buscarDadosClient(@QueryValue clienteId: String?): HttpResponse<ClientResponse>
}