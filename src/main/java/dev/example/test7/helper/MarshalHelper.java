package dev.example.test7.helper;

import dev.example.test7.constant.LocalConstant;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Log4j2
@NoArgsConstructor(access = AccessLevel.PRIVATE)

public final class MarshalHelper {

    public static <T> void marshalToFile(T obj) throws JAXBException {
        final Class<?> objClass = obj.getClass();
        JAXBContext context = JAXBContext.newInstance(objClass);
        Marshaller marshaller = context.createMarshaller();

        //для структурного отображения
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

        marshaller.marshal(obj, new File(LocalConstant.STORAGE + objClass + ".xml"));
        marshaller.marshal(obj, System.out);
    }

    //ответ в виде xml
    public static <T> String marshalOutputStream(T obj) throws JAXBException {
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            JAXBContext context = JAXBContext.newInstance(obj.getClass());
            Marshaller marshaller = context.createMarshaller();

            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            marshaller.marshal(obj, outputStream);
            marshaller.marshal(obj, System.out);

            return outputStream.toString(StandardCharsets.UTF_8);

        } catch (JAXBException | IOException ex) {
            log.error(ex.getMessage(), ex);
            throw new JAXBException("marshal2");
        }
    }
}
