package br.com.ot6

import br.com.ot6.bcb.CreatePixKeyRequest
import br.com.ot6.bcb.DeletePixKey
import br.com.ot6.bcb.PixKeyDetailsResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.*
import io.micronaut.http.client.annotation.Client

@Client("\${http.services.bcb.url}")
interface BcbClient {

    @Post("/api/v1/pix/keys")
    @Produces(MediaType.APPLICATION_XML)
    @Consumes(MediaType.APPLICATION_XML)
    fun registerPixKey(@Body createPixKeyRequest: CreatePixKeyRequest?): HttpResponse<PixKeyDetailsResponse>

    @Delete("/api/v1/pix/keys/{key}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    fun deletePixKey(@QueryValue key: String, @Body deletePixKey: DeletePixKey): HttpResponse<DeletePixKey>

    @Get("/api/v1/pix/keys/{key}")
    @Consumes(MediaType.APPLICATION_XML)
    @Produces(MediaType.APPLICATION_XML)
    fun findPixKey(@QueryValue key: String): HttpResponse<PixKeyDetailsResponse>

}