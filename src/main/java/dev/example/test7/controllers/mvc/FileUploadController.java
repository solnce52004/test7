package dev.example.test7.controllers.mvc;

import dev.example.test7.constants.Constant;
import dev.example.test7.constants.Route;
import dev.example.test7.constants.View;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Controller
@RequestMapping("/")
@Log4j2
public class FileUploadController {

    @GetMapping(Route.ROUTE_UPLOAD)
    public String uploadMethod() {
        return View.VIEW_UPLOAD;
    }

    @PostMapping(
            value = Route.ROUTE_UPLOAD_FILE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public String uploadFile(
            @RequestParam("file") MultipartFile file,
            ModelMap modelMap
    ) {
        try {
            final byte[] fileBytes = file.getBytes();
            String filename = file.getOriginalFilename();
            log.info(filename);

            final File dir = new File(Constant.LOCAL_FILE_STORAGE);
//            final File dir = new File("tmp");

            final File uploadedFile = new File(
                    dir.getAbsolutePath() + File.separator + filename
            );

//            final Path path = Paths.get("tmp" + File.separator + filename);
//            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
//
            try (BufferedOutputStream stream = new BufferedOutputStream(
                    new FileOutputStream(uploadedFile))
            ) {
                stream.write(fileBytes);
                stream.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
            log.error("IOException on getBytes()");
            return "redirect:" + Route.ROUTE_UPLOAD;
        }

        modelMap.addAttribute("file", file);
        return "redirect:" + Route.ROUTE_UPLOAD;
    }
}
