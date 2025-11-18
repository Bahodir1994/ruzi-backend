package app.ruzi.service.enums;

import lombok.Getter;

@Getter
public enum ErrorCode {

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
