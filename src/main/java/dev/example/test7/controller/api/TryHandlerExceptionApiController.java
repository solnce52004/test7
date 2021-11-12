package dev.example.test7.controller.api;

import dev.example.test7.dto.StudentDTO;
import dev.example.test7.dto.UserDTO;
import dev.example.test7.helper.MarshalHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import jakarta.xml.bind.JAXBException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@Log4j2
//@SessionAttributes(names = {"user"})
@Api(
        value = "onlinestore",
        description = "Проверка переопределенных обработчиков исключений"
)

public class TryHandlerExceptionApiController {

    @ApiOperation(
            value = "Проверка исключения при передачи невалидных данных модели",
            response = ResponseEntity.class
    )
    //постом передаем невалидные данные
    @PostMapping(
            value = "/dep1/set-user",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDTO> methodArgumentNotValid_400(
            @Valid @RequestBody UserDTO user
    ) {
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    //вместо POST передаем GET
    @PostMapping(
            value = "/dep2/set-user",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDTO> httpRequestMethodNotSupported_405(
            @Valid @RequestBody UserDTO user
    ) {
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    //Content-Type передаем не JSON
    @PostMapping(
            value = "/dep3/set-user",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDTO> httpMediaTypeNotSupported_415(
            @Valid @RequestBody UserDTO user
    ) {
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    //в постмане Accept="application/json" (Content-Type="application/json")
    @PostMapping(
            value = "/dep4/set-user",
            produces = {
                    MediaType.APPLICATION_XML_VALUE,//406
                    MediaType.APPLICATION_PDF_VALUE//406
            },
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDTO> httpMediaTypeNotAcceptable_406(
            @Valid @RequestBody UserDTO user
    ) {
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(
            value = "/dep5/get-user/level.{level}/cat.{cat}/id.{id}",
//            value = "/get-user/id={id}/cat={cat}",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE})
    public UserDTO missingPathVariable_500(
            @PathVariable(name = "id") Long id,
            @PathVariable(name = "cat") Integer category,
            @PathVariable(name = "level") Integer level
    ) {
        log.info(category);
        return new UserDTO("user_" + id, "1231232132");
    }

    @GetMapping(
            value = "/dep6/get-user",
            produces = MediaType.APPLICATION_JSON_VALUE)
    public UserDTO missingServletRequestParameter_500(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "cat") Integer category
    ) {
        log.info(category);
        return new UserDTO("user_" + id, "1231232132");
    }

    //в постмане в боди не передается юзер
    @PostMapping(
            value = "/dep7/get-user",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> servletRequestBinding_400(
            @RequestAttribute UserDTO user
    ) {
        return ResponseEntity.ok().build();
    }

    //********
    @Autowired
    @Qualifier("mvcConversionService")
    ConversionService conversionService;

    //string-to-user?user=1234,Peter,true
    // НЕ регистрировать КОНВЕРТЕР в конфигах
    @GetMapping("/dep8/string-to-user")
    public ResponseEntity<Object> conversionNotSupported_500(
            @RequestParam("user") UserDTO user
    ) {
        conversionService.convert("25, , ", UserDTO.class);
        return ResponseEntity.ok(user);
    }

    //string-to-user?user=1234,Peter
    //регистрировать, но не передавать 1 парам
    @GetMapping("/dep9/string-to-user")
    public ResponseEntity<Object> typeMismatch_400(
            @RequestParam("user") UserDTO user
    ) {
        conversionService.convert("25, , ", UserDTO.class);
        return ResponseEntity.ok(user);
    }
    //********

    ///////
    //TODO: не  удалось установить при каких условиях выпадает эта ошибка для XML
    @PostMapping(
            value = "/dep10/user-to-xml",
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public ResponseEntity<Object> httpMessageNotReadable_400_xml(
            @RequestBody UserDTO user) throws JAXBException {
        return ResponseEntity.ok(MarshalHelper.marshalOutputStream(user));
    }

    ///////
    //TODO: не удалось установить при каких условиях выпадает эта ошибка для XML
    @PostMapping(
            value = "/dep11/user-to-xml",
            produces = MediaType.APPLICATION_XML_VALUE
    )
    public ResponseEntity<Object> httpMessageNotWritable_500_xml(
            @RequestBody UserDTO user) {
        try {
            MarshalHelper.marshalToFile(user);
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    ///////

    //постом передаем невалидные данные - в json не указываем значение одного параметра/скобку
    @PostMapping(
            value = "/dep12/set-user",
            produces = MediaType.APPLICATION_JSON_VALUE,
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDTO> httpMessageNotReadable_400(
            @Valid @RequestBody UserDTO user
    ) {
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    //нет getter/setter/noargs
    @GetMapping(
            value = "/dep13/student/{id}",
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<StudentDTO> httpMessageNotWritable_500(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(new StudentDTO(id, "Pet"));
    }

    // название ключей в запросе и в методе не совпадают
    @PostMapping(
            value = "/dep14/user",
            consumes = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.MULTIPART_FORM_DATA_VALUE
            },
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> missingServletRequestPart_400(
            @RequestPart(name = "file888") MultipartFile file
    ) {
        return ResponseEntity.ok(file.getOriginalFilename());
    }

    //TODO: поймать не удалось...
    @PostMapping(
            value = "/dep15/user",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> bindException_400(
            @Valid @RequestBody UserDTO user,
//            RedirectAttributes redirectAttrs,
            BindingResult bindingResult
    ) {
        System.out.println(bindingResult.getAllErrors());
        return ResponseEntity.ok(user.getUsername());
    }

    // spring.mvc.throw-exception-if-no-handler-found=true
    // вызываем несуществующий метод
    @PostMapping(
            value = "/dep16/user",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<Object> noHandlerFound_400(
            @Valid @RequestBody UserDTO user
    ) {
        return ResponseEntity.ok(user);
    }
}
