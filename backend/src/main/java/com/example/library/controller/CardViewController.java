package com.example.library.controller;

import com.example.library.dto.CardDtos;
import com.example.library.service.CardService;
import com.example.library.web.CardWebPaths;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@Controller
public class CardViewController {

    private final CardService cardService;

    public CardViewController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping(CardWebPaths.TICKET_PAGE)
    public ResponseEntity<String> ticket(@RequestParam(required = false) String token) {
        String html;
        if (token == null || token.isBlank()) {
            html = htmlPage("QR-код", errorBody("Не указан код билета."));
        } else {
            try {
                CardDtos.QrCardView view = cardService.resolveQrToken(token);
                html = htmlPage("Читательский билет", cardBody(view));
            } catch (IllegalArgumentException e) {
                html = htmlPage("QR-код", errorBody(e.getMessage()));
            }
        }
        return htmlResponse(html);
    }

    /** Старые QR вели на /card/view — iOS сохранял их как файл «view». */
    @GetMapping("/card/view")
    public ResponseEntity<Void> legacyView(@RequestParam(required = false) String token) {
        String base = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        String location = base + CardWebPaths.TICKET_PAGE;
        if (token != null && !token.isBlank()) {
            location = location + "?token=" + token;
        }
        return ResponseEntity.status(HttpStatus.FOUND).location(URI.create(location)).build();
    }

    private static ResponseEntity<String> htmlResponse(String html) {
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("text/html;charset=UTF-8"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"ticket.html\"")
                .body(html);
    }

    private static String cardBody(CardDtos.QrCardView view) {
        String statusLabel = statusLabel(view.status());
        return """
                <p class="badge">Проверка пройдена</p>
                <h1>%s</h1>
                <dl>
                  <dt>Номер билета</dt><dd>%s</dd>
                  <dt>Статус</dt><dd><span class="status">%s</span></dd>
                  <dt>Код действует до</dt><dd>%s</dd>
                </dl>
                <p class="hint">Данные получены по подписанному QR-коду библиотеки.</p>
                """.formatted(
                esc(view.fullName()),
                esc(view.cardNumber()),
                esc(statusLabel),
                esc(view.validUntil())
        );
    }

    private static String errorBody(String message) {
        return "<p class=\"error\">" + esc(message) + "</p>";
    }

    private static String statusLabel(String status) {
        if ("ACTIVE".equals(status)) {
            return "Активен";
        }
        if ("BLOCKED".equals(status)) {
            return "Заблокирован";
        }
        return status != null ? status : "—";
    }

    private static String esc(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }

    private static String htmlPage(String title, String body) {
        return """
                <!DOCTYPE html>
                <html lang="ru">
                <head>
                  <meta charset="UTF-8"/>
                  <meta name="viewport" content="width=device-width, initial-scale=1"/>
                  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
                  <title>%s</title>
                  <style>
                    * { box-sizing: border-box; }
                    body {
                      font-family: system-ui, -apple-system, Segoe UI, Roboto, sans-serif;
                      margin: 0; min-height: 100vh; display: flex; align-items: center;
                      justify-content: center; padding: 24px;
                      background: linear-gradient(160deg, #1B3A5C 0%%, #2D5A87 45%%, #F7F4EF 45%%);
                    }
                    .card {
                      background: #FFFCF8; border-radius: 20px; padding: 28px 24px;
                      max-width: 400px; width: 100%%; box-shadow: 0 12px 40px rgba(0,0,0,.18);
                    }
                    .badge {
                      display: inline-block; margin: 0 0 12px; padding: 6px 12px;
                      border-radius: 20px; background: #B7E4C7; color: #0D3321;
                      font-size: 13px; font-weight: 600;
                    }
                    h1 { margin: 0 0 20px; font-size: 22px; color: #1B3A5C; line-height: 1.3; }
                    dl { margin: 0; }
                    dt { font-size: 12px; text-transform: uppercase; letter-spacing: .04em;
                         color: #7A756C; margin-top: 14px; }
                    dd { margin: 4px 0 0; font-size: 17px; color: #1C1B1A; }
                    .status { color: #2D6A4F; font-weight: 600; }
                    .hint { margin: 24px 0 0; font-size: 13px; color: #7A756C; line-height: 1.4; }
                    .error { color: #BA1A1A; font-size: 16px; margin: 0; }
                  </style>
                </head>
                <body>
                  <div class="card">%s</div>
                </body>
                </html>
                """.formatted(esc(title), body);
    }
}
