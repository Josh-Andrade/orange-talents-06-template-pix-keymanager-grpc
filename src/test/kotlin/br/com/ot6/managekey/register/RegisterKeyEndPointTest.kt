package br.com.ot6.managekey.register

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
import io.micronaut.http.client.exceptions.HttpClientResponseException
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
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
internal class RegisterKeyEndPointTest(
    private val grpcClient: KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub,
    private val repository: PixKeyRepository
) {

    @field:Inject
    lateinit var itauClient: ErpItauClient;

    @field:Inject
    lateinit var bcbClient: BcbClient;

    lateinit var registerPixKeyRequest: RegisterPixKeyRequest
    lateinit var registerPixKeyRequestRandomKey: RegisterPixKeyRequest

    lateinit var clientAccountResponse: ClientAccountResponse

    lateinit var randomKey: String

    @BeforeEach
    fun setup() {
        repository.deleteAll()
        registerPixKeyRequest = RegisterPixKeyRequest
            .newBuilder()
            .setAccountType(AccountType.CONTA_CORRENTE)
            .setClientId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setKey("rafael.ponte@zup.com.br")
            .setKeyType(KeyType.EMAIL)
            .build()

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
        randomKey = UUID.randomUUID().toString()

        registerPixKeyRequestRandomKey = RegisterPixKeyRequest
            .newBuilder()
            .setAccountType(AccountType.CONTA_CORRENTE)
            .setClientId("c56dfef4-7901-44fb-84e2-a2cefb157890")
            .setKey("")
            .setKeyType(KeyType.CHAVE_ALEATORIA)
            .build()
    }

    @Test
    fun `deve registrar chave do tipo email`() {

        Mockito.`when`(
            itauClient.buscarContaClient(
                registerPixKeyRequest.clientId,
                registerPixKeyRequest.accountType
            )
        )
            .thenReturn(HttpResponse.ok(clientAccountResponse))

        val createPixKeyRequest = CreatePixKeyRequest(
            clientAccountResponse,
            registerPixKeyRequest.toModel()
        )


        Mockito.`when`(bcbClient.registerPixKey(createPixKeyRequest)).thenReturn(
            HttpResponse.ok(
                PixKeyDetailsResponse(
                    createPixKeyRequest.keyType,
                    randomKey,
                    createPixKeyRequest.bankAccount,
                    createPixKeyRequest.owner,
                    LocalDateTime.now()
                )
            )
        )

        val response = grpcClient.register(registerPixKeyRequest)

        assertTrue(repository.existsByKey(randomKey))
        assertEquals(1, repository.count())
        assertNotNull(response.pixId)
    }

    @Test
    fun `deve registrar chave do tipo aleatoria`() {
        Mockito.`when`(
            itauClient.buscarContaClient(
                registerPixKeyRequestRandomKey.clientId,
                registerPixKeyRequestRandomKey.accountType
            )
        )
            .thenReturn(HttpResponse.ok(clientAccountResponse))

        val createPixKeyRequest = CreatePixKeyRequest(
            clientAccountResponse,
            registerPixKeyRequestRandomKey.toModel()
        )

        Mockito.`when`(bcbClient.registerPixKey(createPixKeyRequest)).thenReturn(
            HttpResponse.ok(
                PixKeyDetailsResponse(
                    createPixKeyRequest.keyType,
                    randomKey,
                    createPixKeyRequest.bankAccount,
                    createPixKeyRequest.owner,
                    LocalDateTime.now()
                )
            )
        )


        val response = grpcClient.register(registerPixKeyRequestRandomKey)

        assertTrue(repository.existsByKey(randomKey))
        assertEquals(1, repository.count())
        assertNotNull(response.pixId)
    }

    @Test
    fun `deve gerar exception de chave existente`() {

        val createPixKeyResponse = PixKeyDetailsResponse(
            KeyTypeBcb.EMAIL,
            "rafael.ponte@zup.com.br",
            BankAccount("60701190", "0001", "291900", AccountTypeBcb.CACC),
            Owner(PersonType.NATURAL_PERSON, "Rafael M C Ponte", "02467781054"),
            LocalDateTime.now()
        )

        repository.save(
            PixKey(
                createPixKeyResponse,
                ClientAccountResponse(
                    AccountType.CONTA_CORRENTE,
                    Instituicao("ITAÚ UNIBANCO S.A.", "60701190"),
                    "0001",
                    "291900",
                    Titular(
                        UUID.fromString("c56dfef4-7901-44fb-84e2-a2cefb157890"),
                        "Rafael M C Ponte",
                        "02467781054"
                    )
                ),
            )
        )

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.register(registerPixKeyRequest)
        }

        with(error) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave pix já existe", status.description)
        }
    }

    @Test
    fun `deve gerar exception de chave existente ao tentar cadastrar no banco central`() {
        val createPixKeyRequest = CreatePixKeyRequest(
            clientAccountResponse,
            registerPixKeyRequest.toModel()
        )

        Mockito.`when`(
            itauClient.buscarContaClient(
                registerPixKeyRequest.clientId,
                registerPixKeyRequest.accountType
            )
        )
            .thenReturn(HttpResponse.ok(clientAccountResponse))

        Mockito.`when`(bcbClient.registerPixKey(createPixKeyRequest)).thenThrow(
            HttpClientResponseException::class.java
        )

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.register(registerPixKeyRequest)
        }

        println(error)

        with(error) {
            assertEquals(Status.ALREADY_EXISTS.code, status.code)
            assertEquals("Chave pix já existe no banco central", status.description)
        }
    }


    @Test
    fun `deve gerar exception para cliente nao encontrado`() {
        val registerPixKeyRequest = RegisterPixKeyRequest
            .newBuilder()
            .setAccountType(AccountType.CONTA_CORRENTE)
            .setClientId(UUID.randomUUID().toString())
            .setKey("rafael.ponte@zup.com.br")
            .setKeyType(KeyType.EMAIL)
            .build()

        Mockito.`when`(
            itauClient.buscarContaClient(
                registerPixKeyRequest.clientId,
                registerPixKeyRequest.accountType
            )
        )
            .thenReturn(HttpResponse.ok(null))

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.register(registerPixKeyRequest)
        }

        with(error) {
            assertEquals(Status.NOT_FOUND.code, status.code)
            assertEquals("Cliente ${registerPixKeyRequest?.clientId} não encontrado", status.description)
        }
    }

    @Test
    fun `deve retornar exception da bean validation por id em formato invalido`() {
        val registerPixKeyRequest = RegisterPixKeyRequest
            .newBuilder()
            .setAccountType(AccountType.CONTA_CORRENTE)
            .setClientId("id cliente do bom")
            .setKey("rafael.ponte@zup.com.br")
            .setKeyType(KeyType.EMAIL)
            .build()

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.register(registerPixKeyRequest)
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("[clientId: Formato de UUID invalido]", status.description)
        }

    }

    @Test
    fun `deve retornar exception da bean validation por email em formato invalido`() {
        val registerPixKeyRequest = RegisterPixKeyRequest
            .newBuilder()
            .setAccountType(AccountType.CONTA_CORRENTE)
            .setClientId(UUID.randomUUID().toString())
            .setKey("rafael.pontezup.com.br")
            .setKeyType(KeyType.EMAIL)
            .build()

        val error = assertThrows<StatusRuntimeException> {
            grpcClient.register(registerPixKeyRequest)
        }

        with(error) {
            assertEquals(Status.INVALID_ARGUMENT.code, status.code)
            assertEquals("[dto: Formato da chave pix invalido]", status.description)
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
class RegisterKeyServer {

    @Singleton
    fun blockingStub(@GrpcChannel(GrpcServerChannel.NAME) channel: ManagedChannel):
            KeyManagerRegistraGrpcServiceGrpc.KeyManagerRegistraGrpcServiceBlockingStub {

        return KeyManagerRegistraGrpcServiceGrpc.newBlockingStub(channel)
    }
}