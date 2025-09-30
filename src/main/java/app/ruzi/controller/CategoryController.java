package app.ruzi.controller;

import app.ruzi.configuration.annotation.auth.MethodInfo;
import app.ruzi.configuration.messaging.HandlerService;
import app.ruzi.configuration.messaging.MessageResponse;
import app.ruzi.service.app.category.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryController {

    private final HandlerService handlerService;
    private final CategoryService categoryService;

    @PostMapping("/self")
    //@CustomAuthRole(roles = {"ROLE_CATEGORY_READ"})
    @MethodInfo(methodName = "bnt-app-cmdt-desc-read")
    public ResponseEntity<Object> getCategory(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @RequestBody @Valid DataTablesInput dataTablesInput
    ) {

        MessageResponse messageResponse = handlerService.handleRequest(
                () -> categoryService.getCategories(dataTablesInput),
                langType
        );

        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }


    @GetMapping("/width-item")
    //@CustomAuthRole(roles = {"ROLE_CATEGORY_READ"})
    @MethodInfo(methodName = "bnt-app-cmdt-desc-read")
    public ResponseEntity<Object> getCategoryWithItem(@RequestHeader(value = "Accept-Language", required = false) String langType) {

        MessageResponse messageResponse = handlerService.handleRequest(
                categoryService::getCategoriesWithItems,
                langType
        );

        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @GetMapping("/tree")
    //@CustomAuthRole(roles = {"ROLE_CATEGORY_READ"})
    @MethodInfo(methodName = "bnt-app-cmdt-desc-read")
    public ResponseEntity<Object> getCategoryTree(@RequestHeader(value = "Accept-Language", required = false) String langType) {

        MessageResponse messageResponse = handlerService.handleRequest(
                categoryService::getCategoryTree,
                langType
        );

        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

    @GetMapping("/{id}/translations")
    //@CustomAuthRole(roles = {"ROLE_CATEGORY_READ"})
    @MethodInfo(methodName = "bnt-app-cmdt-desc-read")
    public ResponseEntity<Object> getCategoryTranslations(
            @RequestHeader(value = "Accept-Language", required = false) String langType,
            @PathVariable String id
    ) {

        MessageResponse messageResponse = handlerService.handleRequest(
                () -> categoryService.getCategoryTranslation(id),
                langType
        );

        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }

}
