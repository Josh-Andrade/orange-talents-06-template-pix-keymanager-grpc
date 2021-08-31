package br.com.ot6.shared

import br.com.ot6.KeyType
import io.micronaut.validation.validator.constraints.EmailValidator
import org.hibernate.validator.internal.constraintvalidators.hv.br.CPFValidator

enum class KeyType {
    CHAVE_ALEATORIA {
        override fun valid(key: String?): Boolean = key.isNullOrBlank()
        override fun returnEquivalentEnum(): KeyType = KeyType.CHAVE_ALEATORIA
    },
    CPF{
        override fun valid(key: String?): Boolean {
            if(key.equals("00000000000") ||
                key.equals("11111111111") ||
                key.equals("22222222222") ||
                key.equals("33333333333") ||
                key.equals("44444444444") ||
                key.equals("55555555555") ||
                key.equals("66666666666") ||
                key.equals("77777777777") ||
                key.equals("88888888888") ||
                key.equals("99999999999")){
                return false
            }
            return CPFValidator().run {
                initialize(null)
                isValid(key, null)
            }
        }

        override fun returnEquivalentEnum(): KeyType = KeyType.CPF
    },
    EMAIL {
        override fun valid(key: String?): Boolean {
            return EmailValidator().run {
                initialize(null)
                isValid(key, null)
            }
        }

        override fun returnEquivalentEnum(): KeyType = KeyType.EMAIL
    },
    CELULAR{
        override fun valid(key: String?): Boolean {
            return key?.matches("^\\+[1-9][0-9]\\d{1,14}\$".toRegex())!!
        }

        override fun returnEquivalentEnum(): KeyType = KeyType.CELULAR
    };




    abstract fun valid(key: String?): Boolean

    abstract fun returnEquivalentEnum() : KeyType
}