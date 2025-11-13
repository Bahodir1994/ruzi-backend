package app.ruzi.service.app.item;

import app.ruzi.repository.app.ItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
@RequiredArgsConstructor
public class BarcodeGeneratorService {

    private final ItemRepository itemRepository;
    private final Random random = new Random();

    /**
     * EAN-13 Barcode generatsiyasi
     * Format:
     * 478 (Uzbekistan GS1 prefix) + 9 ta random digit + checksum
     */
    @Transactional
    public String generateBarcode() {

        String barcode;

        do {
            barcode = generateBaseBarcode();
        } while (itemRepository.existsByBarcode(barcode));

        return barcode;
    }

    private String generateBaseBarcode() {
        StringBuilder sb = new StringBuilder();

        // 478 - Uzbekistan GS1 prefix
        sb.append("478");

        // 9 ta random raqam (total 12 digit)
        for (int i = 0; i < 9; i++) {
            sb.append(random.nextInt(10));
        }

        // Checksum
        int checksum = calculateChecksum(sb.toString());
        sb.append(checksum);

        return sb.toString();
    }

    /**
     * EAN-13 checksum formula
     */
    private int calculateChecksum(String first12) {
        int sum = 0;

        for (int i = 0; i < first12.length(); i++) {
            int digit = first12.charAt(i) - '0';

            if ((i % 2) == 0) {        // Odd positions (0-based) → *1
                sum += digit;
            } else {                   // Even positions → *3
                sum += digit * 3;
            }
        }

        int mod = sum % 10;
        return (mod == 0) ? 0 : (10 - mod);
    }
}

