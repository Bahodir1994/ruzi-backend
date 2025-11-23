package app.ruzi.service.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {
    WARN0001(
            "WARN0001",
            "Record was changed by another user",
            "Yozuv boshqa foydalanuvchi tomonidan o‘zgartirilgan",
            "Запись была изменена другим пользователем"
    ),

    ERROR0002(
            "ERROR0002",
            "File is empty",
            "Excel xujjat kelmadi",
            "Данные не найдено"
    ),

    ERROR0003(
            "ERROR0003",
            "An error occurred while processing the data",
            "Ma`lumotlarni qayta ishlashda xatolik yuz berdi",
            "Произошла ошибка при обработке данных"
    ),

    ERROR0004(
            "ERROR0004",
            "There is an error in the uploaded document",
            "Yuklangan xujjatda xatolik mavjud",
            "В загруженном документе обнаружена ошибка"
    ),

    FORBID0001(
            "FORBID0001",
                    "Only purchase orders in DRAFT status can be deleted",
                    "Faqat QORALAMA holatidagi buyurtmalarni o‘chirsa bo‘ladi",
                    "Удалять можно только заказы в статусе ЧЕРНОВИК"
    );

    private final String code;
    private final String en;
    private final String uz;
    private final String ru;

    ErrorCode(String code, String en, String uz, String ru) {
        this.code = code;
        this.en = en;
        this.uz = uz;
        this.ru = ru;
    }
}
