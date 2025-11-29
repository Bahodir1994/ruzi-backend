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

    ERROR1001(
            "ERROR1001",
            "Cart session not found",
            "Savat topilmadi",
            "Чек-сессия не найдена"
    ),

    ERROR1002(
            "ERROR1002",
            "Only checked-out carts can be returned",
            "Faqat yakunlangan savatdan qaytarish mumkin",
            "Возврат возможен только для закрытых чеков"
    ),

    ERROR1003(
            "ERROR1003",
            "Cart item not found",
            "Savatdagi mahsulot topilmadi",
            "Товар в чеке не найден"
    ),

    ERROR1004(
            "ERROR1004",
            "Invalid return quantity",
            "Qaytarilayotgan miqdor noto‘g‘ri",
            "Неверное количество для возврата"
    ),

    ERROR1005(
            "ERROR1005",
            "Stock record not found",
            "Zaxira topilmadi",
            "Запись склада не найдена"
    ),
    FORBID0001(
            "FORBID0001",
            "Only purchase orders in DRAFT status can be deleted",
            "Faqat QORALAMA holatidagi buyurtmalarni o‘chirsa bo‘ladi",
            "Удалять можно только заказы в статусе ЧЕРНОВИК"
    ),

    FORBID0002(
            "FORBID0002",
            "Items cannot be deleted from a confirmed order",
            "Tasdiqlangan kirim partiyasidan tovar o‘chirib bo‘lmaydi",
            "Товары не могут быть удалены из подтвержденного заказа"
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
