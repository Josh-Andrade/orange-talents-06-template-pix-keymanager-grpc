package br.com.ot6.handler

import io.micronaut.aop.Around
import io.micronaut.context.annotation.Type
import kotlin.annotation.AnnotationRetention.RUNTIME
import kotlin.annotation.AnnotationTarget.CLASS
import kotlin.annotation.AnnotationTarget.FUNCTION

@MustBeDocumented
@Target(CLASS, FUNCTION)
@Retention(RUNTIME)
@Type(InterceptorHandlerImpl::class)
@Around
annotation class ErrorInterceptor()
