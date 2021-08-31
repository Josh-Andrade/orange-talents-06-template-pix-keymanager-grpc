package br.com.ot6.managekey.find

import br.com.ot6.*
import br.com.ot6.bcb.*
import br.com.ot6.bcb.Owner
import br.com.ot6.itau.ClientAccountResponse
import br.com.ot6.itau.Instituicao
import br.com.ot6.itau.Titular
import br.com.ot6.managekey.PixKeyRepository
import br.com.ot6.managekey.domain.PixKey
import io.grpc.ManagedChannel
import io.grpc.Status
import io.grpc.StatusRuntimeException
import io.micronaut.context.annotation.Factory
import io.micronaut.grpc.annotation.GrpcChannel
import io.micronaut.grpc.server.GrpcServerChannel
import io.micronaut.http.HttpResponse
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class FindKeyEndPointTest(
    private val grpcClient: KeyManagerConsultaGrpcServiceGrpc.KeyManagerConsultaGrpcServiceBlockingStub,
    private val repository: PixKeyRepository){

    @field:Inject
    private lateinit var bcbClient: BcbClient

    private lateinit var request: FindPixKeyRequest

    private lateinit var clientAccountResponse: ClientAccountResponse

    private lateinit var pixKeyDetailsResponse: PixKeyDetailsResponse

    private lateinit var pixKey: PixKey

    @BeforeEach
    fun setup(){
        repository.deleteAll()
        clientAccountResponse = ClientAccountResponse(
            AccountType.CONTA_CORRENTE,
            Instituicao("ITAÚ UNIBANCO S.A.", "60701190"),
            "0001",
            "291900",
            Titular(
                UUID.fromString("c56dfef4-7901-44fb-84e2-a2cefb157890"),
                "Rafael M C Ponte",
                "02467781054"
            )
        )

        pixKeyDetailsResponse = PixKeyDetailsResponse(
            KeyTypeBcb.EMAIL,
            "rafael.ponte@zup.com.br",
            BankAccount("60701190",
                "0001",
                "291900",
                AccountTypeBcb.CACC),
            Owner(
                PersonType.NATURAL_PERSON,
                "Rafael M C Ponte",
                "02467781054"),
            LocalDateTime.now()
        )

        pixKey = repository.save(PixKey(pixKeyDetailsResponse, clientAccountResponse))
    }

    @Test
    fun `deve retornar uma chave pix pelo id`(){
        request = FindPixKeyRequest.newBuilder()
            .setFilterPixId(FindPixKeyRequest.FilterForPixId.newBuilder()
                .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb157890")
                .setPixKeyId(pixKey.id.toString())
                .build())
            .build()
        val loadPixKey = grpcClient.loadPixKey(request)

        assertEquals(pixKey.id.toString(), loadPixKey.pixKeyId)
        assertEquals("c56dfef4-7901-44fb-84e2-a2cefb157890", loadPixKey.clientId)
    }

    @Test
    fun `deve retornar uma chave pix pelo valor da chave`(){
        request = FindPixKeyRequest.newBuilder()
            .setKey("rafael.ponte@zup.com.br")
            .build()

        val loadPixKey = grpcClient.loadPixKey(request)

        assertEquals("rafael.ponte@zup.com.br", loadPixKey.key)
    }

    @Test
    fun `deve retornar uma chave pix do banco central pelo valor da chave`(){
        request = FindPixKeyRequest.newBuilder()
            .setKey("yuri@zup.com.br")
            .build()
        Mockito.`when`(bcbClient.findPixKey("yuri@zup.com.br")).thenReturn(
            HttpResponse.ok(
                PixKeyDetailsResponse(
                    KeyTypeBcb.EMAIL,
                    "yuri@zup.com.br",
                    BankAccount("60701190", "0001", "291900", AccountTypeBcb.CACC),
                    Owner(PersonType.NATURAL_PERSON, "Yuri", "02467781054"),
                    LocalDateTime.now()
                )
            )
        )
        val loadPixKey = grpcClient.loadPixKey(request)

        assertEquals("yuri@zup.com.br", loadPixKey.key)
    }

    @Test
    fun `nao deve retornar uma chave pix com o id invalido`(){
        request = FindPixKeyRequest.newBuilder()
            .setFilterPixId(FindPixKeyRequest.FilterForPixId.newBuilder()
                .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb15789")
                .setPixKeyId(pixKey.id.toString())
                .build())
            .build()

        val error = org.junit.jupiter.api.assertThrows<StatusRuntimeException> {
            grpcClient.loadPixKey(request)
        }
        assertEquals(Status.NOT_FOUND.code, error.status.code)
        assertEquals("NOT_FOUND: Chave Pix não encontrada", error.message)
    }


    @MockBean(BcbClient::class)
    fun bcbClient(): BcbClient {
        return Mockito.mock(BcbClient::class.java)
    }

}

@Factory
class FindKeyServer {
    @Singleton
    fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
            KeyManagerConsultaGrpcServiceGrpc.KeyManagerConsultaGrpcServiceBlockingStub {
        return KeyManagerConsultaGrpcServiceGrpc.newBlockingStub(channel)
    }
}