package app.ruzi.service.app.cart;

import org.springframework.stereotype.Service;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

@Service
public class PrinterService {

    public void printSimple(String printerIp, int port, String text) throws Exception {
        try (Socket socket = new Socket(printerIp, port)) {
            OutputStream out = socket.getOutputStream();

            // Reset
            out.write(new byte[]{0x1B, '@'});

            // Text
            out.write(text.getBytes(StandardCharsets.UTF_8));

            // Feed
            out.write("\n\n\n".getBytes());

            // Cut
            out.write(new byte[]{0x1D, 'V', 1});

            out.flush();
        }
    }
}
