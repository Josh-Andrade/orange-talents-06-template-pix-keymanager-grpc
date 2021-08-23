package br.com.ot6.validation

import br.com.ot6.managekey.dto.NewPixKeyDto
import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint

@MustBeDocumented
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS)
@Constraint(validatedBy = [KeyValidator::class])
annotation class ValidKey(val message: String = "Formato da chave pix invalido")


@Singleton
class KeyValidator : ConstraintValidator<ValidKey, NewPixKeyDto> {
    override fun isValid(
        value: NewPixKeyDto?,
        annotationMetadata: AnnotationValue<ValidKey>,
        context: ConstraintValidatorContext
    ): Boolean {
        return value?.keyType?.valid(value.key)!!
    }

}