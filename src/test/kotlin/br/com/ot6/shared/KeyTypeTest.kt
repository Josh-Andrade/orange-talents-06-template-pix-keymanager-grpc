package br.com.ot6.shared

import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.util.*

@MicronautTest
internal class KeyTypeTest{

    @Test
    fun `deve ser valido se o formato do celular estiver correto`(){
        with(KeyType.CELULAR){
            assertTrue(valid("+55999999999"))
        }
    }

    @Test
    fun `nao deve ser valido se o formato do celular estiver correto`(){
        with(KeyType.CELULAR){
            assertFalse(valid("+55999_99999"))
        }
    }

    @Test
    fun `deve validar se o cpf for valido`(){
        with(KeyType.CPF){
            assertTrue(valid("17105521074"))
        }
    }

    @Test
    fun `nao deve validar se o cpf for invalido`(){
        with(KeyType.CPF){
            assertFalse(valid("00000200000"))
        }
    }

    @Test
    fun `nao deve validar se o cpf for constituido de numeros repetidos`(){
        with(KeyType.CPF){
            assertFalse(valid("00000000000"))
        }
    }

    @Test
    fun `deve validar se o email for valido`(){
        with(KeyType.EMAIL){
            assertTrue(valid("joshua.almeida@zup.com.br"))
        }
    }

    @Test
    fun `nao deve validar se o email for invalido`(){
        with(KeyType.EMAIL){
            assertFalse(valid("joshua.almeidazup.com.br"))
        }
    }

    @Test
    fun `deve validar se o valor da chave esta vazio quando tipo for chave aleatoria`(){
        with(KeyType.CHAVE_ALEATORIA){
            assertTrue(valid(""))
        }
    }

    @Test
    fun `nao deve validar se o valor da chave esta preenchido quando tipo for chave aleatori`(){
        with(KeyType.CHAVE_ALEATORIA){
            assertFalse(valid(UUID.randomUUID().toString()))
        }
    }
}