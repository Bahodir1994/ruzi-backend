//package app.ruzi.service.payload;
//
//import app.ruzi.entity.PurchaseOrder;
//import lombok.Value;
//
//import java.io.Serializable;
//import java.math.BigDecimal;
//import java.time.LocalDate;
//
///**
// * DTO for {@link app.ruzi.entity.PurchaseOrder}
// */
//@Value
//public class PurchaseOrderDto implements Serializable {
//    Long id;
//    String orderNumber;
//    LocalDate createdAt;
//    LocalDate approvedAt;
//    String currency;
//    LocalDate dueDate;
//    PurchaseOrder.Status status;
//    PurchaseOrder.PaymentStatus paymentStatus;
//    BigDecimal totalAmount;
//    BigDecimal paidAmount;
//    BigDecimal debtAmount;
//    String comment;
//}