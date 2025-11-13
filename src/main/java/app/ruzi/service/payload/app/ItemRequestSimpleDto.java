package app.ruzi.service.payload.app;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

/**
 * Item yaratish yoki yangilash uchun API request DTO.
 * Barcha maydonlar String sifatida olinadi va validatsiya qilinadi.
 */
@Builder
public record ItemRequestSimpleDto(

        /** Mahsulot nomi — majburiy */
        @NotBlank(message = "Mahsulot nomi (name) bo‘sh bo‘lmasligi kerak")
        @Size(max = 600, message = "Mahsulot nomi 600 belgidan oshmasligi kerak")
        String name,

        /** Narx — ixtiyoriy, lekin agar kiritilsa, musbat bo‘lishi shart */
        @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$", message = "Narx faqat raqam bo‘lishi kerak (masalan: 12000 yoki 12000.50)")
        String price,

        /** Aktivlik flagi — true yoki false */
        @Pattern(regexp = "^(true|false)$", message = "isActive faqat true yoki false bo‘lishi kerak")
        String isActive
) {
}
