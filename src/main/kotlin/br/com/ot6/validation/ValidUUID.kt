package br.com.ot6.validation

import io.micronaut.core.annotation.AnnotationValue
import io.micronaut.validation.validator.constraints.ConstraintValidator
import io.micronaut.validation.validator.constraints.ConstraintValidatorContext
import javax.inject.Singleton
import javax.validation.Constraint
import javax.validation.Payload
import javax.validation.ReportAsSingleViolation
import javax.validation.constraints.Pattern
import kotlin.annotation.AnnotationRetention.*
import kotlin.annotation.AnnotationTarget.*
import kotlin.reflect.KClass

@ReportAsSingleViolation
@Retention(RUNTIME)
@Constraint(validatedBy = [ValidateUUID::class])
@Target(FIELD, CONSTRUCTOR, PROPERTY, VALUE_PARAMETER)
annotation class ValidUUID(
    val message: String = "Formato de UUID invalido",
    val groups: Array<KClass<Any>> = [],
    val payload: Array<KClass<Payload>> = []
)

@Singleton
class ValidateUUID : ConstraintValidator<ValidUUID, String>{
    override fun isValid(
        value: String?,
        annotationMetadata: AnnotationValue<ValidUUID>,
        context: ConstraintValidatorContext
    ): Boolean {
        return value?.matches("^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$".toRegex())!!
    }

}
