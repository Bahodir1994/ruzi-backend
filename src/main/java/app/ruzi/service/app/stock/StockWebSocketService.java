package app.ruzi.service.app.stock;

import app.ruzi.service.payload.app.StockViewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class StockWebSocketService {
    private final SimpMessagingTemplate template;

    public void broadcastStockUpdate(StockViewDto dto) {
        template.convertAndSend("/topic/stock-updates", dto);
    }

    public void broadcastCartListUpdate() {
        template.convertAndSend("/topic/carts", "UPDATED");
    }
}

