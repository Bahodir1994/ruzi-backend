package app.ruzi.service.payload.app;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderUpdateDto {

    /**
     * Agar mavjud orderni qayta yaratish bo‘lsa yoki tashqi tizimdan ID kelayotgan bo‘lsa.
     * Bo‘lmasa null bo‘lib ketadi va JPA UUID avtomatik generatsiya qiladi.
     */
    private String id;

    private String supplierId;
    private String warehouseId;
    private String currency;
    private String dueDate;
    private String comment;
    private String status;
    private String paidAmount;



}

