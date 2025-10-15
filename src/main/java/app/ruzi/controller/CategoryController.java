package app.ruzi.controller;

import app.ruzi.configuration.annotation.auth.MethodInfo;
import app.ruzi.configuration.messaging.HandlerService;
import app.ruzi.configuration.messaging.MessageResponse;
import app.ruzi.entity.app.Category;
import app.ruzi.service.app.category.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/route-category")
@RequiredArgsConstructor
public class CategoryController {

    private final HandlerService handlerService;
    private final CategoryService categoryService;

    @PostMapping("/data-table-main")
    //@CustomAuthRole(roles = {"ROLE_CATEGORY_READ"})
    @MethodInfo(methodName = "read-category-table")
    public ResponseEntity<Object> getCategoryTable(@RequestBody @Valid DataTablesInput dataTablesInput) {
        DataTablesOutput<Category> privilegeDataTablesOutput = categoryService.getCategories(dataTablesInput);
        return new ResponseEntity<>(privilegeDataTablesOutput, HttpStatus.OK);
    }

    @GetMapping("/data-list-main")
    //@CustomAuthRole(roles = {"ROLE_CATEGORY_READ"})
    @MethodInfo(methodName = "read-category-list")
    public ResponseEntity<Object> getCategoryList(
            @RequestHeader(value = "Accept-Language", required = false) String langType) {
        MessageResponse messageResponse = handlerService.handleRequest(
                categoryService::getCategoryList,
                langType
        );
        return ResponseEntity.status(messageResponse.getStatus()).body(messageResponse);
    }
}