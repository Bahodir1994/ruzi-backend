package app.ruzi.service.app.item;

import app.ruzi.configuration.jwt.JwtUtils;
import app.ruzi.configuration.jwt.UserJwt;
import app.ruzi.repository.app.ItemRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ItemCodeGeneratorService {

    private final JwtUtils jwtUtils;
    private final ItemRepository itemRepository;

    /**
     * PLU code generatsiyasi (client bo‘yicha incremental)
     */
    @Transactional
    public String generatePluCode() {

        UserJwt finalUserJwt = jwtUtils.extractUserFromToken();

        Integer maxCode = itemRepository.findMaxCodeByClient(finalUserJwt.getClientId());
        int next = (maxCode == null ? 1000 : maxCode + 1);
        return String.valueOf(next);
    }

    /**
     * SKU generatsiya — alfanumerik
     * SKU format: SKU-{clientId}-{runningNumber}
     */
    @Transactional
    public String generateSkuCode() {
        UserJwt finalUserJwt = jwtUtils.extractUserFromToken();

        Integer maxSku = itemRepository.findMaxSkuByClient(finalUserJwt.getClientId());
        int next = (maxSku == null ? 1 : maxSku + 1);

        return "SKU-" + finalUserJwt.getClientId().substring(0, 4).toUpperCase()
                + "-" + String.format("%05d", next);
    }

    /**
     * Barcode optional, agar siz avtomatik EAN-13 generatsiya qilmoqchi bo‘lsangiz
     */
    public String generateBarcode() {
        return null; // hozircha optional, keyin EAN-13 generator qo‘shib beraman
    }
}

