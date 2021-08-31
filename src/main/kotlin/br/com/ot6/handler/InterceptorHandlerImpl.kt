package br.com.ot6.handler

import br.com.ot6.managekey.register.RegisterKeyEndPoint
import com.google.rpc.BadRequest
import io.grpc.Status
import io.grpc.protobuf.StatusProto
import io.grpc.stub.StreamObserver
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import org.slf4j.LoggerFactory
import javax.inject.Singleton
import javax.validation.ConstraintViolationException

@Singleton
class InterceptorHandlerImpl : MethodInterceptor<RegisterKeyEndPoint, Any> {

    val logger = LoggerFactory.getLogger(this::class.java)

    override fun intercept(context: MethodInvocationContext<RegisterKeyEndPoint, Any?>): Any? {

        try {
            return context.proceed()
        } catch (e: ConstraintViolationException) {
            GrpcEndpointsArguments(context).response()
                .onError(
                    StatusProto.toStatusRuntimeException(getStatusProto(e))
                )
            return null
        } catch (e: Exception) {
          GrpcEndpointsArguments(context).response()
                .onError(
                    e.status
                        .withDescription(e.message)
                        .asRuntimeException()
                )
            return null
        }
    }

    private fun getStatusProto(e: ConstraintViolationException) = com.google.rpc.Status.newBuilder()
        .setCode(Status.INVALID_ARGUMENT.code.value())
        .setMessage(e.constraintViolations.map { "${it.propertyPath.last().name}: ${it.message}" }.toString())
        .addDetails(com.google.protobuf.Any.pack(getDetailsWithFieldViolations(e)))
        .build()

    private fun getDetailsWithFieldViolations(e: ConstraintViolationException) = BadRequest.newBuilder()
        .addAllFieldViolations(getFieldViolations(e))
        .build()

    private fun getFieldViolations(e: ConstraintViolationException) = e.constraintViolations.map {
        BadRequest.FieldViolation.newBuilder()
            .setField(it.propertyPath.last().name)
            .setDescription(it.message)
            .build()
    }

}

private class GrpcEndpointsArguments(private val context: MethodInvocationContext<RegisterKeyEndPoint, Any?>) {

    fun response(): StreamObserver<*> {
        return context.parameterValues[1] as StreamObserver<*>
    }
}
