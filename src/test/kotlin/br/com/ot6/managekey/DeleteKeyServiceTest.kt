package br.com.ot6.managekey

import br.com.ot6.*
import br.com.ot6.bcb.*
import br.com.ot6.itau.ClientAccountResponse
import br.com.ot6.itau.ClientResponse
import br.com.ot6.itau.Instituicao
import br.com.ot6.itau.Titular
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
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.Mockito
import java.time.LocalDateTime
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@MicronautTest(transactional = false)
internal class DeleteKeyServiceTest(
    private val grpcClient: KeyManagerDeletaGrpcServiceGrpc.KeyManagerDeletaGrpcServiceBlockingStub,
    private val repository: PixKeyRepository
){

    @field:Inject
    lateinit var itauClient: ErpItauClient;

    @field:Inject
    lateinit var bcbClient: BcbClient;

    private lateinit var deletePixKeyRequestValid: DeletePixKeyRequest

    private lateinit var deletePixKeyRequestNotValid: DeletePixKeyRequest

    private lateinit var clientAccountResponse: ClientAccountResponse

    private lateinit var pixKeyDetailsResponse: PixKeyDetailsResponse

    private lateinit var clientResponse: ClientResponse

    private lateinit var deletePixKey: DeletePixKey

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
            BankAccount("Rafael M C Ponte",
                "0001",
                "291900",
                AccountTypeBcb.CACC),
            Owner(PersonType.NATURAL_PERSON,
                "Rafael M C Ponte",
                "02467781054"),
            LocalDateTime.now()
        )

        val pixKey = repository.save(PixKey(pixKeyDetailsResponse, clientAccountResponse))

        deletePixKeyRequestValid = DeletePixKeyRequest.newBuilder()
            .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setPixKeyId(pixKey.id.toString())
            .build()
        deletePixKeyRequestNotValid = DeletePixKeyRequest.newBuilder()
            .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb157893")
            .setPixKeyId(pixKey.id.toString())
            .build()

        clientResponse = ClientResponse(
            Instituicao("ITAÚ UNIBANCO S.A.",
                "60701190")
        )

        deletePixKey = DeletePixKey("rafael.ponte@zup.com.br", "60701190")
    }

    @Test
    fun `deve deletar chave pix existente e pertencente ao usuario`(){
        Mockito.`when`(itauClient.buscarDadosClient("c56dfef4-7901-44fb-84e2-a2cefb157890"))
            .thenReturn(HttpResponse.ok(clientResponse))

        Mockito.`when`(bcbClient.deletePixKey("rafael.ponte@zup.com.br", deletePixKey))
            .thenReturn(HttpResponse.ok(deletePixKey))

        val response = grpcClient.delete(deletePixKeyRequestValid)

        with(response){
            assertEquals(0, repository.count())
            assertEquals("Chave pix rafael.ponte@zup.com.br deletada", message)
        }

    }

    @Test
    fun `nao deve deletar chave pix existente e que nao pertencente ao usuario`(){

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.delete(deletePixKeyRequestNotValid)
        }

        with(error){
            assertEquals("NOT_FOUND: Chave pix não encontrada", message)
            assertEquals(Status.NOT_FOUND.code, status.code)
        }
    }

    @Test
    fun `nao deve deletar chave pix inexistente`(){

        val request = DeletePixKeyRequest.newBuilder()
            .setClienteId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setPixKeyId("c56dfef4-7901-44fb-84e2-a2cefb157543")
            .build()
        val error = assertThrows<StatusRuntimeException> {
            grpcClient.delete(request)
        }

        with(error){
            assertEquals("NOT_FOUND: Chave pix não encontrada", message)
            assertEquals(Status.NOT_FOUND.code, status.code)
        }
    }

    @Test
    fun `nao deve deletar chave pix porque ela nao existe no banco central`(){
        Mockito.`when`(itauClient.buscarDadosClient("c56dfef4-7901-44fb-84e2-a2cefb157890"))
            .thenReturn(HttpResponse.ok(clientResponse))

        Mockito.`when`(bcbClient.deletePixKey("rafael.ponte@zup.com.br", deletePixKey))
            .thenReturn(HttpResponse.notFound(deletePixKey))

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.delete(deletePixKeyRequestValid)
        }

        with(error){
            assertEquals("NOT_FOUND: Ocorreu um problema ao tentar deletar a chave pix com o Banco central", message)
            assertEquals(Status.NOT_FOUND.code, status.code)
        }
    }

    @MockBean(ErpItauClient::class)
    fun itauClient(): ErpItauClient {
        return Mockito.mock(ErpItauClient::class.java)
    }

    @MockBean(BcbClient::class)
    fun bcbClient(): BcbClient {
        return Mockito.mock(BcbClient::class.java)
    }


}

@Factory
class DeleteKeyServer {

    @Singleton
    fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
            KeyManagerDeletaGrpcServiceGrpc.KeyManagerDeletaGrpcServiceBlockingStub {

        return KeyManagerDeletaGrpcServiceGrpc.newBlockingStub(channel)
    }
}