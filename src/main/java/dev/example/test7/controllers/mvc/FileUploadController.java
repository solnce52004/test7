package dev.example.test7.controllers.mvc;

import dev.example.test7.constants.Route;
import dev.example.test7.constants.View;
import dev.example.test7.services.UploadService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import java.util.ArrayList;

@Controller
@RequestMapping("/")
@Log4j2
public class FileUploadController {

    private final UploadService uploadService;

    @Autowired
    public FileUploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }


    @GetMapping(Route.ROUTE_UPLOAD_INDEX)
    public String uploadIndex(Model model) {

        if (!model.containsAttribute("file")) {
            model.addAttribute(
                    "file",
                    new Object()
            );
        }

        if (!model.containsAttribute("uploadedFiles")) {
            model.addAttribute(
                    "uploadedFiles",
                    new ArrayList<>()
            );
        }

        if (!model.containsAttribute("errors")) {
            model.addAttribute(
                    "errors",
                    new ArrayList<>()
            );
        }

        return View.VIEW_UPLOAD_INDEX;
    }

    @PostMapping(
            value = Route.ROUTE_UPLOAD_FILE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ModelAndView uploadFile(
            @RequestParam("file") MultipartFile file,
            ModelAndView model,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes
    ) {
        String filename = uploadService.store(file);
        log.info(filename);

        final ArrayList<Object> uploadedFiles = model.getModelMap().containsAttribute("uploadedFiles")
                ? (ArrayList<Object>) model.getModelMap().getAttribute("uploadedFiles")
                : new ArrayList<>();

        uploadedFiles.add(filename);

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.file", bindingResult);
        }
        redirectAttributes.addFlashAttribute("file", file);
        redirectAttributes.addFlashAttribute("uploadedFiles", uploadedFiles);
        redirectAttributes.addFlashAttribute("errors", new ArrayList<>());

        final RedirectView redirectView = new RedirectView(View.VIEW_UPLOAD_INDEX);
        redirectView.setStatusCode(HttpStatus.MOVED_PERMANENTLY);

        final ModelAndView mav = new ModelAndView();
        mav.setView(redirectView);

        return mav;
    }
}
