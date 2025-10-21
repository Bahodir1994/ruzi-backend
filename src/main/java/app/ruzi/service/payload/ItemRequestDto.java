package app.ruzi.service.payload;

import jakarta.validation.constraints.*;
import lombok.Builder;

/**
 * Item yaratish yoki yangilash uchun API request DTO.
 * Barcha maydonlar String sifatida olinadi va validatsiya qilinadi.
 */
@Builder
public record ItemRequestDto(

        /** Tovar kodi — majburiy, kategoriya ichida unikal bo‘lishi kerak */
        @NotBlank(message = "Mahsulot kodi (code) bo‘sh bo‘lmasligi kerak")
        @Size(max = 100, message = "Mahsulot kodi 100 belgidan oshmasligi kerak")
        String code,

        /** Mahsulot nomi — majburiy */
        @NotBlank(message = "Mahsulot nomi (name) bo‘sh bo‘lmasligi kerak")
        @Size(max = 600, message = "Mahsulot nomi 600 belgidan oshmasligi kerak")
        String name,

        /** Narx — ixtiyoriy, lekin agar kiritilsa, musbat bo‘lishi shart */
        @Pattern(regexp = "^[0-9]+(\\.[0-9]{1,2})?$", message = "Narx faqat raqam bo‘lishi kerak (masalan: 12000 yoki 12000.50)")
        String price,

        /** Kategoriya ID — majburiy (UUID formatida bo‘lishi kerak) */
        @NotBlank(message = "Kategoriya ID bo‘sh bo‘lmasligi kerak")
        @Pattern(regexp = "^[0-9a-fA-F-]{36}$", message = "Kategoriya ID noto‘g‘ri formatda (UUID kutilmoqda)")
        String categoryId,

        /** Aktivlik flagi — true yoki false */
        @Pattern(regexp = "^(true|false)$", message = "isActive faqat true yoki false bo‘lishi kerak")
        String isActive,

        /** Asosiy rasm URL manzili */
        @Size(max = 300, message = "Rasm URL uzunligi 300 belgidan oshmasligi kerak")
        @Pattern(
                regexp = "^(https?://)?.*\\.(jpg|jpeg|png|webp|gif)?$",
                message = "Rasm URL noto‘g‘ri formatda"
        )
        String primaryImageUrl,

        /** SKU kodi — unikal, majburiy */
        @NotBlank(message = "SKU kodi bo‘sh bo‘lmasligi kerak")
        @Size(max = 50, message = "SKU kodi 50 belgidan oshmasligi kerak")
        String skuCode,

        /** Shtrix-kod — ixtiyoriy, lekin unikal */
        @Pattern(regexp = "^[0-9A-Za-z\\-]*$", message = "Barcode faqat raqam va harflardan iborat bo‘lishi kerak")
        @Size(max = 100, message = "Barcode uzunligi 100 belgidan oshmasligi kerak")
        String barcode,

        /** Brend nomi */
        @Size(max = 200, message = "Brend nomi 200 belgidan oshmasligi kerak")
        String brand,

        /** Birlik kodi (masalan: 'dona', 'kg', 'l') */
        @Size(max = 3, message = "Birlik (unit) 3 belgidan oshmasligi kerak")
        String unit,

        /** Tavsif — ixtiyoriy */
        @Size(max = 600, message = "Tavsif uzunligi 600 belgidan oshmasligi kerak")
        String description
) {}
