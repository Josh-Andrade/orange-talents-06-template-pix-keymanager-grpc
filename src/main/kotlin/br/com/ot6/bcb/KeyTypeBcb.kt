package br.com.ot6.bcb

import br.com.ot6.shared.KeyType

enum class KeyTypeBcb(val equivalent: KeyType) {
    PHONE(KeyType.CELULAR),
    RANDOM(KeyType.CHAVE_ALEATORIA),
    CPF(KeyType.CPF),
    EMAIL(KeyType.EMAIL);

    companion object{
        fun getBcbTypeKey(keyType: KeyType?): KeyTypeBcb{
            values().forEach { keyTypeBcb ->
                if(keyTypeBcb.equivalent == keyType)
                    return keyTypeBcb
            }
            return RANDOM
        }
    }
}
