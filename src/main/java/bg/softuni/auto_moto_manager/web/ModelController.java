package bg.softuni.auto_moto_manager.web;

import bg.softuni.auto_moto_manager.model.dto.binding.CreateModelDTO;
import bg.softuni.auto_moto_manager.model.enums.VehicleTypeEnum;
import bg.softuni.auto_moto_manager.service.ModelService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static bg.softuni.auto_moto_manager.util.Constants.BINDING_RESULT_PACKAGE;

@Controller
@RequestMapping("/models")
public class ModelController {
    private final ModelService modelService;

    public ModelController(ModelService modelService) {
        this.modelService = modelService;
    }

    @ModelAttribute
    public CreateModelDTO initCreateModelDTO() {
        return new CreateModelDTO();
    }

    @GetMapping("/add")
    public String createModel(Model model) {
        model.addAttribute("vehicleTypes", VehicleTypeEnum.values());

        List<String> makesNames = modelService.getAllMakersNamesOrdered();
        model.addAttribute("makesNames", makesNames);

        return "add-model";
    }

    @PostMapping("/add")
    public String createModel(@Valid CreateModelDTO createModelDTO,
                              BindingResult bindingResult,
                              RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("createModelDTO", createModelDTO);
            redirectAttributes.addFlashAttribute(
                    BINDING_RESULT_PACKAGE + "createModelDTO",
                    bindingResult);

            return "redirect:/models/add";
        }

        modelService.createModel(createModelDTO);

        return "redirect:/";
    }

}
