package app.ruzi.service.app.item;

import app.ruzi.service.payload.app.ItemXlsxRequestDto;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.Getter;

import java.util.*;
import java.util.function.Consumer;

public class ItemListener extends AnalysisEventListener<ItemXlsxRequestDto> {
    private static final Map<String, String> FIELD_TO_EXCEL_HEADER = Map.ofEntries(
            Map.entry("code", "Mahsulot kodi"),
            Map.entry("name", "Mahsulot nomi"),
            Map.entry("price", "Narxi"),
            Map.entry("categoryId", "Kategoriya ID"),
            Map.entry("isActive", "Holati (true/false)"),
            Map.entry("primaryImageUrl", "Rasm URL"),
            Map.entry("skuCode", "SKU kodi"),
            Map.entry("barcode", "Shtrix-kod"),
            Map.entry("brand", "Brend"),
            Map.entry("unit", "O‘lchov birligi"),
            Map.entry("description", "Tavsif")
    );

    private static final int BATCH_SIZE = 100;
    private final List<ItemXlsxRequestDto> batchList = new ArrayList<>();

    @Getter
    private final List<Map<String, Object>> validationErrors = new ArrayList<>();

    private final Consumer<List<ItemXlsxRequestDto>> batchSaver;
    private final Validator validator;

    public ItemListener(Consumer<List<ItemXlsxRequestDto>> batchSaver, Validator validator) {
        this.batchSaver = batchSaver;
        this.validator = validator;
    }

    @Override
    public void invoke(ItemXlsxRequestDto dto, AnalysisContext context) {
        Set<ConstraintViolation<ItemXlsxRequestDto>> violations = validator.validate(dto);
        if (!violations.isEmpty()) {
            Map<String, List<Map<String, String>>> fieldErrorsMap = new HashMap<>();

            Map<String, Object> errorObject = new HashMap<>();
            errorObject.put("lineNumber", context.readRowHolder().getRowIndex() + 1);

            List<Map<String, String>> lineColumns = new ArrayList<>();

            for (ConstraintViolation<?> v : violations) {
                String field = v.getPropertyPath().toString();
                String header = FIELD_TO_EXCEL_HEADER.getOrDefault(field, field);

                Map<String, String> columnError = new HashMap<>();
                columnError.put("field", header);
                columnError.put("message", v.getMessage());
                lineColumns.add(columnError);
            }

            errorObject.put("lineColumns", lineColumns);
            validationErrors.add(errorObject);
        }


        batchList.add(dto);

        if (batchList.size() >= BATCH_SIZE) {
            if (validationErrors.isEmpty()) {
                batchSaver.accept(new ArrayList<>(batchList));
            }
            batchList.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!batchList.isEmpty() && validationErrors.isEmpty()) {
            batchSaver.accept(new ArrayList<>(batchList));
        }

//        if (!validationErrors.isEmpty()) {
//            throw new CustomValidationException(
//                    validationErrors,
//                    ERROR_TYPE1.getCode()
//            );
//        }
    }
}
