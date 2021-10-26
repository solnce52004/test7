package dev.example.test7.controllers.api;

import dev.example.test7.dto.UserDTO;
import dev.example.test7.exceptions.custom_exceptions.ThereIsNoSuchUserException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1")
@Log4j2
@SessionAttributes(names = {"user"})

public class HomeApiController {

    @GetMapping(
            value = "/dep2/get-user",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public UserDTO getUserById(
            @RequestParam(name = "id") Long id,
            @RequestParam(name = "cat") Integer category
    ) {
        log.info(category);
        return new UserDTO("user_" + id, "1231232132");
    }

    @GetMapping(
//            value = "/get-user/{id}/{cat}",
            value = "/get-user/level.{level}/cat.{cat}/id.{id}",
//            value = "/get-user/id={id}/cat={cat}",
            produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public UserDTO getUserByIdMissingPathVar(
            @PathVariable(name = "id") Long id,
            @PathVariable(name = "cat") Integer category,
            @PathVariable(name = "level") Integer level
    ) {
        log.info(category);
        return new UserDTO("user_" + id, "1231232132");
    }

    @GetMapping(value = "/dep1/get-user/{id}", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ResponseBody
    public UserDTO getUserByIdTryCustomException(@PathVariable Long id) {
        throw new ThereIsNoSuchUserException(id);
    }

    @GetMapping(value = "/get-user-by-name")
    @ResponseBody
    public UserDTO getUserByName(@Valid @RequestParam("name") String name) {
        return new UserDTO(name, "1231232132");
    }

    @PostMapping(
            value = "/set-user",
            produces = {
                    MediaType.APPLICATION_JSON_VALUE,
                    MediaType.APPLICATION_XML_VALUE,//406
                    MediaType.APPLICATION_PDF_VALUE//406
            },
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    public ResponseEntity<UserDTO> setUser(@Valid @RequestBody UserDTO user) {
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }

    @GetMapping(
            value = "/products/{code}",
            produces = {
//                    MediaType.APPLICATION_JSON_VALUE,
//                    MediaType.APPLICATION_XML_VALUE,//406
//                    MediaType.APPLICATION_PDF_VALUE//406
            }
    )
    @ResponseBody
    public UserDTO getProductByCode(
            @PathVariable final String code,
            @RequestParam(required = false, defaultValue = "BASIC") final String options) {
        return new UserDTO("user_" + code, options);
    }

    @Autowired
    ConversionService conversionService;

    ///string-to-user?user=1234,Peter
    @GetMapping("/string-to-user")
    public ResponseEntity<Object> getStringToEmployee(
            @RequestParam("user") UserDTO user) {
        conversionService.convert("25, , ", UserDTO.class);
        return ResponseEntity.ok(user);
    }

    ///////
    @PostMapping(value = "/dep3/set-user")
    public ResponseEntity<UserDTO> setUserDep3(@RequestBody UserDTO user) {
//        try {
//            marshal(user);
//        } catch (JAXBException e) {
//            e.printStackTrace();
//        }
        return new ResponseEntity<>(HttpStatus.ACCEPTED);
    }
    ///////

//    private void marshal(UserDTO user) throws JAXBException {
//        JAXBContext context = JAXBContext.newInstance(Book.class);
//        Marshaller mar= context.createMarshaller();
//        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
//        mar.marshal(user, new File("./book.xml"));
//    }
}
