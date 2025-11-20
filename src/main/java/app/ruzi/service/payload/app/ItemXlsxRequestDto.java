package app.ruzi.service.payload.app;

import app.ruzi.service.mappers.CellToStringConverter;
import com.alibaba.excel.annotation.ExcelProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ItemXlsxRequestDto {

    @ExcelProperty(value = "Mahsulot kodi", converter = CellToStringConverter.class)
    @NotBlank(message = "Mahsulot kodi bo‘sh bo‘lmasligi kerak")
    @Size(max = 100, message = "Mahsulot kodi 100 belgidan oshmasligi kerak")
    private String code;

    @ExcelProperty(value = "Mahsulot nomi", converter = CellToStringConverter.class)
    @NotBlank(message = "Mahsulot nomi bo‘sh bo‘lmasligi kerak")
    @Size(max = 600, message = "Mahsulot nomi 600 belgidan oshmasligi kerak")
    private String name;

    @ExcelProperty(value = "Narxi", converter = CellToStringConverter.class)
    @Pattern(
            regexp = "^[0-9]+(\\.[0-9]{1,2})?$",
            message = "Narx faqat musbat raqam (12 yoki 12.50) formatida bo‘lishi kerak"
    )
    private String price;

    @ExcelProperty(value = "Kategoriya ID", converter = CellToStringConverter.class)
    @Pattern(
            regexp = "^[0-9a-fA-F-]{36}$",
            message = "Kategoriya ID noto‘g‘ri (UUID formatida bo‘lishi kerak)"
    )
    private String categoryId;

    @ExcelProperty(value = "Holati (true/false)", converter = CellToStringConverter.class)
    @Pattern(regexp = "^(true|false)$", message = "Holat faqat true yoki false bo‘lishi kerak")
    private String isActive;

    @ExcelProperty(value = "Rasm URL", converter = CellToStringConverter.class)
    @Size(max = 300, message = "Rasm URL 300 belgidan oshmasligi kerak")
    private String primaryImageUrl;

    @ExcelProperty(value = "SKU kodi", converter = CellToStringConverter.class)
    @NotBlank(message = "SKU kodi bo‘sh bo‘lmasligi kerak")
    @Size(max = 50, message = "SKU kodi 50 belgidan oshmasligi kerak")
    private String skuCode;

    @ExcelProperty(value = "Shtrix-kod", converter = CellToStringConverter.class)
    @Pattern(regexp = "^[0-9A-Za-z\\-]*$", message = "Shtrix-kod faqat harf va raqamlardan iborat bo‘lishi kerak")
    @Size(max = 100, message = "Shtrix-kod uzunligi 100 belgidan oshmasligi kerak")
    private String barcode;

    @ExcelProperty(value = "Brend", converter = CellToStringConverter.class)
    @Size(max = 200, message = "Brend nomi 200 belgidan oshmasligi kerak")
    private String brand;

    @ExcelProperty(value = "O‘lchov birligi", converter = CellToStringConverter.class)
    @NotBlank(message = "O‘lchov birligi bo‘sh bo‘lmasligi kerak")
    @Size(max = 3, message = "O‘lchov birligi 3 belgidan oshmasligi kerak")
    private String unit;

    @ExcelProperty(value = "Tavsif", converter = CellToStringConverter.class)
    @Size(max = 600, message = "Tavsif uzunligi 600 belgidan oshmasligi kerak")
    private String description;
}
