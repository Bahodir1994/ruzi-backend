package app.ruzi.controller;

import app.ruzi.configuration.annotation.auth.MethodInfo;
import app.ruzi.entity.app.Stock;
import app.ruzi.service.app.stock.StockService;
import app.ruzi.service.payload.app.StockViewDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.datatables.mapping.DataTablesInput;
import org.springframework.data.jpa.datatables.mapping.DataTablesOutput;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/route-stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @PostMapping("/data-table-main")
    //@CustomAuthRole(roles = {"ROLE_CATEGORY_READ"})
    @MethodInfo(methodName = "read-stock-table")
    public ResponseEntity<Object> read_table_data(@RequestBody @Valid DataTablesInput dataTablesInput) {
        DataTablesOutput<StockViewDto> privilegeDataTablesOutput = stockService.getStock(dataTablesInput);
        return new ResponseEntity<>(privilegeDataTablesOutput, HttpStatus.OK);
    }
}

