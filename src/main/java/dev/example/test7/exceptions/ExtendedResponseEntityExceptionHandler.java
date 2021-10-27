package dev.example.test7.exceptions;

import dev.example.test7.exceptions.error_bodies.ErrorBody;
import dev.example.test7.exceptions.error_mappers.MapperErrorFieldsByMethodArgumentNotValid;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@RestControllerAdvice // Аннотация обработки исключений Spring
@Log4j2
public class ExtendedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    private static final String CUSTOM_MSG_TEMPLATE_NOT_SUPPORTED = "%s is not supported.%s Type given: [%s]";
    private static final String CUSTOM_MSG_TEMPLATE_ALLOWED = " Allowed types: %s.";
    private static final String CUSTOM_MSG_TEMPLATE_MISSING = "%s is missing.%s Given: [%s]";
    private static final String CUSTOM_MSG_TEMPLATE_EXPECTED = " Expected: %s.";


    /**
     * 400 BAD_REQUEST
     */
    @Override
    // дескриптор ошибки для @Valid //будет выбрасываться при @valid
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        ErrorBody body = new ErrorBody()
                .setTimestamp(LocalDateTime.now())
                .setCustomMessage("(MethodArgumentNotValidException) дескриптор ошибки @Valid")
                .setDebugMessage(ex.getMessage())
                .setStatus(status.value())
                .setStatusName(status.name())
                .setErrors(
                        new MapperErrorFieldsByMethodArgumentNotValid()
                                .getErrors(ex)
                );

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    /**
     * 405 METHOD_NOT_ALLOWED
     * <p>
     * POST/GET
     */
    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        pageNotFoundLogger.warn(ex.getMessage());

        Set<HttpMethod> supportedMethods = ex.getSupportedHttpMethods();
        String allowed = "";

        if (!CollectionUtils.isEmpty(supportedMethods)) {
            headers.setAllow(supportedMethods);
            allowed += String.format(
                    CUSTOM_MSG_TEMPLATE_ALLOWED,
                    supportedMethods.toString()
            );
        }

        ErrorBody body = new ErrorBody()
                .setTimestamp(LocalDateTime.now())
                .setCustomMessage(String.format(
                        CUSTOM_MSG_TEMPLATE_NOT_SUPPORTED,
                        "(HttpRequestMethodNotSupportedException) Type method",
                        allowed,
                        ex.getMethod()
                ))
                .setDebugMessage(ex.getMessage())
                .setStatus(status.value())
                .setStatusName(status.name());

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    /**
     * 415 UNSUPPORTED_MEDIA_TYPE
     * <p>
     * Content-Type
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
        String allowed = "";

        if (!CollectionUtils.isEmpty(mediaTypes)) {
            headers.setAccept(mediaTypes);
            allowed += String.format(
                    CUSTOM_MSG_TEMPLATE_ALLOWED,
                    mediaTypes.toString()
            );

            if (request instanceof ServletWebRequest) {
                ServletWebRequest servletWebRequest = (ServletWebRequest) request;

                if (HttpMethod.PATCH.equals(servletWebRequest.getHttpMethod())) {
                    headers.setAcceptPatch(mediaTypes);
                }
            }
        }

        ErrorBody body = new ErrorBody()
                .setTimestamp(LocalDateTime.now())
                .setCustomMessage(String.format(
                        CUSTOM_MSG_TEMPLATE_NOT_SUPPORTED,
                        "(HttpMediaTypeNotSupportedException) (Media) Content-Type",
                        allowed,
                        ex.getContentType()
                ))
                .setDebugMessage(ex.getMessage())
                .setStatus(status.value())
                .setStatusName(status.name());

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    /**
     * 406 NOT_ACCEPTABLE
     * <p>
     * Чтобы протестировать, необходимо указать в методе котроллера
     * produces = {MediaType.APPLICATION_XML_VALUE},
     * а в постмане Accept="application/json" (Content-Type="application/json")
     * т.к. красивый ответ в данном метооде при переопределении
     * предполагается получать именно в "application/json"
     * <p>
     * При других кейсах возможно будет приходить либо пустое тело ошибки,
     * либо исключение прямо из томката в text/html (ловила))
     */
    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(
            HttpMediaTypeNotAcceptableException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        List<MediaType> mediaTypes = ex.getSupportedMediaTypes();
        String allowed = "";
        if (!CollectionUtils.isEmpty(mediaTypes)) {
            allowed += String.format(
                    CUSTOM_MSG_TEMPLATE_ALLOWED,
                    mediaTypes.toString()
            );
        }

        ErrorBody body = new ErrorBody()
                .setTimestamp(LocalDateTime.now())
                .setCustomMessage(String.format(
                        CUSTOM_MSG_TEMPLATE_NOT_SUPPORTED,
                        "(HttpMediaTypeNotAcceptableException) (Media) Acceptable MIME type",
                        allowed,
                        request.getHeader(HttpHeaders.ACCEPT)
                ))
                .setDebugMessage(ex.getMessage())
                .setStatus(status.value())
                .setStatusName(status.name());

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    /**
     * 500 INTERNAL_SERVER_ERROR
     * <p>
     * Для тестирования - запрос должен быть вида
     * "/get-user/id.{id}/cat.{cat}"
     * иначе,
     * при запросе вида "/get-user/{id}/{cat}" будет возвращаться 404
     */
    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(
            MissingPathVariableException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        //todo: вытащить все параметры
        final String variableName = ex.getVariableName();
        String expected = "";
        if (!variableName.isEmpty()) {
            expected += String.format(
                    CUSTOM_MSG_TEMPLATE_EXPECTED,
                    variableName
            );
        }

        ErrorBody body = new ErrorBody()
                .setTimestamp(LocalDateTime.now())
                .setCustomMessage(String.format(
                        CUSTOM_MSG_TEMPLATE_MISSING,
                        "(MissingPathVariableException) Path variable",
                        expected,
                        ""
                ))
                .setDebugMessage(ex.getMessage())
                .setStatus(status.value())
                .setStatusName(status.name());

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    /**
     * 400 BAD_REQUEST
     * <p>
     * /get-user?idR=8&cat=
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        //todo: отображать в сообщении все пропущенные параметры
        final String param = ex.getParameterName();
        String expected = "";
        if (!param.isEmpty()) {
            expected += String.format(
                    CUSTOM_MSG_TEMPLATE_EXPECTED,
                    param
            );
        }

        ErrorBody body = new ErrorBody()
                .setTimestamp(LocalDateTime.now())
                .setCustomMessage(String.format(
                        CUSTOM_MSG_TEMPLATE_MISSING,
                        "(MissingServletRequestParameter) Servlet request parameter",
                        expected,
                        ""
                ))
                .setDebugMessage(ex.getMessage())
                .setStatus(status.value())
                .setStatusName(status.name());

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    /**
     * 400 BAD_REQUEST
     * <p>
     * missing session attribute, missing required header ???
     * TODO: не удалось установить при каких условиях выпадает эта ошибка!
     */
    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(
            ServletRequestBindingException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        ErrorBody body = new ErrorBody()
                .setTimestamp(LocalDateTime.now())
                .setCustomMessage("ServletRequestBindingException")
                .setDebugMessage(ex.getMessage())
                .setStatus(status.value())
                .setStatusName(status.name());

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    /**
     * 500 INTERNAL_SERVER_ERROR
     * <p>
     * Для тестирования
     * - создать КОНВЕРТЕР, например StringToUserDTOConverter
     * - НЕ РЕГИСТРИРОВАТЬ его в конфигах!
     * - создать метод с параметром @RequestParam("user") UserDTO user
     * - вызвать в постмане "/string-to-user?user=1234,Peter,true"
     */
    @Override
    protected ResponseEntity<Object> handleConversionNotSupported(
            ConversionNotSupportedException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        ErrorBody body = new ErrorBody()
                .setTimestamp(LocalDateTime.now())
                .setCustomMessage("ConversionNotSupportedException")
                .setDebugMessage(ex.getMessage())
                .setStatus(status.value())
                .setStatusName(status.name());

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    /**
     * 400 BAD_REQUEST
     * <p>
     * Например:
     * - есть конвертер
     * - конвертер Зарегистрирован!
     * - метод с параметром @RequestParam("user") UserDTO user
     * - вызываем "/string-to-user?user=1234,Peter," БЕЗ 1 параметра
     * <p>
     * Либо:
     * - запрос вида /get-user?id={id}&cat={cat}
     * - в {cat} ожидается Integer, а передаем String
     */
    @Override
    protected ResponseEntity<Object> handleTypeMismatch(
            TypeMismatchException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        ErrorBody body = new ErrorBody()
                .setTimestamp(LocalDateTime.now())
                .setCustomMessage("TypeMismatchException")
                .setDebugMessage(ex.getMessage())
                .setStatus(status.value())
                .setStatusName(status.name());

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    /**
     * 400 BAD_REQUEST
     *
     * post/json
     * - параметр валидации int - передаем 1.3
     * - без закрывающей скобки
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        ErrorBody body = new ErrorBody()
                .setTimestamp(LocalDateTime.now())
                .setCustomMessage("HttpMessageNotReadableException")
                .setDebugMessage(ex.getMessage())
                .setStatus(status.value())
                .setStatusName(status.name());

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    /**
     * 500 INTERNAL_SERVER_ERROR
     * <p>
     * TODO: не удалось установить при каких условиях выпадает эта ошибка!
     */
    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(
            HttpMessageNotWritableException ex,
            HttpHeaders headers,
            HttpStatus status,
            WebRequest request
    ) {
        ErrorBody body = new ErrorBody()
                .setTimestamp(LocalDateTime.now())
                .setCustomMessage("HttpMessageNotWritableException")
                .setDebugMessage(ex.getMessage())
                .setStatus(status.value())
                .setStatusName(status.name());

        return super.handleExceptionInternal(ex, body, headers, status, request);
    }

    /**
     * Customize the response for MissingServletRequestPartException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex      the exception
     * @param headers the headers to be written to the response
     * @param status  the selected response status
     * @param request the current request
     * @return a {@code ResponseEntity} instance
     */
    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleMissingServletRequestPart(ex, headers, status, request);
    }

    /**
     * Customize the response for BindException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex      the exception
     * @param headers the headers to be written to the response
     * @param status  the selected response status
     * @param request the current request
     * @return a {@code ResponseEntity} instance
     */
    @Override
    protected ResponseEntity<Object> handleBindException(BindException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleBindException(ex, headers, status, request);
    }

    /**
     * Customize the response for NoHandlerFoundException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex      the exception
     * @param headers the headers to be written to the response
     * @param status  the selected response status
     * @param request the current request
     * @return a {@code ResponseEntity} instance
     * @since 4.0
     */
    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        return super.handleNoHandlerFoundException(ex, headers, status, request);
    }

    /**
     * Customize the response for AsyncRequestTimeoutException.
     * <p>This method delegates to {@link #handleExceptionInternal}.
     *
     * @param ex         the exception
     * @param headers    the headers to be written to the response
     * @param status     the selected response status
     * @param webRequest the current request
     * @return a {@code ResponseEntity} instance
     * @since 4.2.8
     */
    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatus status, WebRequest webRequest) {
        return super.handleAsyncRequestTimeoutException(ex, headers, status, webRequest);
    }

}
