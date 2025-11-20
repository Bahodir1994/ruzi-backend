package app.ruzi.service.payload.app;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderCreatReadDto {

    /**
     * Agar mavjud orderni qayta yaratish bo‘lsa yoki tashqi tizimdan ID kelayotgan bo‘lsa.
     * Bo‘lmasa null bo‘lib ketadi va JPA UUID avtomatik generatsiya qiladi.
     */
    private String id;

}

