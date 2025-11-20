package app.ruzi.service.app.purchase;

import app.ruzi.repository.app.PurchaseOrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class OrderNumberGeneratorService {

    private final PurchaseOrderRepository purchaseOrderRepository;

    @Transactional
    public String generateOrderNumber(String clientId) {

        LocalDate today = LocalDate.now();

        String yy = String.format("%02d", today.getYear() % 100);
        String mm = String.format("%02d", today.getMonthValue());

        String prefix = "PO-" + yy + mm + "-";

        String lastOrder = purchaseOrderRepository
                .findLastByClientAndPrefix(clientId, prefix);

        int nextSeq = 1;

        if (lastOrder != null) {
            // oxirgi 3 xonani ajratamiz
            String lastSeq = lastOrder.substring(lastOrder.lastIndexOf("-") + 1);
            nextSeq = Integer.parseInt(lastSeq) + 1;
        }

        if (nextSeq > 999) {
            throw new RuntimeException("Bu oy uchun 999 ta limitdan oshib ketdi!");
        }

        return prefix + String.format("%03d", nextSeq);
    }

}

