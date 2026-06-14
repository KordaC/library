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
        String statusClass = "ACTIVE".equals(view.status()) ? "status-active" : "status-blocked";
        String cardNumber = formatCardNumber(view.cardNumber());
        return """
                <div class="plastic-card">
                  <div class="chip"></div>
                  <div class="logo" aria-hidden="true">📚</div>
                  <p class="library">Городская библиотека</p>
                  <h1>Читательский билет</h1>
                  <p class="number">%s</p>
                  <p class="holder">%s</p>
                  <span class="status %s">%s</span>
                  <p class="valid">QR до %s</p>
                </div>
                <p class="hint">Данные получены по подписанному QR-коду библиотеки.</p>
                """.formatted(
                esc(cardNumber),
                esc(view.fullName()),
                statusClass,
                esc(statusLabel),
                esc(view.validUntil())
        );
    }

    private static String formatCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.isBlank()) {
            return "—";
        }
        String digits = cardNumber.replaceAll("\\s+", "");
        if (digits.length() == 5) {
            return digits.substring(0, 2) + " " + digits.substring(2);
        }
        return digits;
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
                      margin: 0; min-height: 100vh; display: flex; flex-direction: column;
                      align-items: center; justify-content: center; gap: 16px; padding: 24px;
                      background: radial-gradient(circle at top, #2D5A87 0%%, #0F2840 55%%, #1C1B1A 100%%);
                    }
                    .wrap { width: min(100%%, 420px); }
                    .plastic-card {
                      position: relative; aspect-ratio: 1.586 / 1; width: 100%%;
                      border-radius: 16px; padding: 20px;
                      background: linear-gradient(135deg, #1B3A5C 0%%, #2D5A87 50%%, #0F2840 100%%);
                      box-shadow: 0 16px 40px rgba(0,0,0,.45), inset 0 1px 0 rgba(255,255,255,.15);
                      color: #fff; overflow: hidden;
                    }
                    .plastic-card::before {
                      content: ""; position: absolute; top: 0; left: 0; right: 0; height: 5px;
                      background: linear-gradient(90deg, #9A7B2F, #F5E6C8, #9A7B2F);
                    }
                    .plastic-card::after {
                      content: ""; position: absolute; inset: 0;
                      background: linear-gradient(315deg, rgba(255,255,255,.14), transparent 45%%);
                      pointer-events: none;
                    }
                    .chip {
                      width: 40px; height: 30px; border-radius: 4px;
                      background: linear-gradient(135deg, #D4AF37, #9A7B2F);
                      border: 1px solid rgba(255,255,255,.25);
                    }
                    .logo {
                      position: absolute; top: 20px; right: 20px; font-size: 24px;
                    }
                    .library {
                      margin: 14px 0 0; font-size: 11px; letter-spacing: .12em;
                      text-transform: uppercase; color: rgba(255,255,255,.7);
                    }
                    .plastic-card h1 {
                      margin: 4px 0 0; font-size: 18px; font-weight: 700; color: #fff;
                    }
                    .number {
                      position: absolute; left: 20px; right: 20px; bottom: 78px;
                      margin: 0; font: 700 28px/1.1 ui-monospace, SFMono-Regular, Menlo, monospace;
                      letter-spacing: .08em; color: #F5E6C8;
                    }
                    .holder {
                      position: absolute; left: 20px; right: 20px; bottom: 52px;
                      margin: 0; font-size: 16px; color: #fff;
                    }
                    .status {
                      position: absolute; left: 20px; bottom: 24px;
                      display: inline-block; padding: 4px 10px; border-radius: 12px;
                      font-size: 13px; font-weight: 600;
                    }
                    .status-active { background: #B7E4C7; color: #0D3321; }
                    .status-blocked { background: #E8E4DD; color: #4A4640; }
                    .valid {
                      position: absolute; right: 20px; bottom: 28px; margin: 0;
                      font-size: 12px; color: rgba(255,255,255,.72); text-align: right;
                    }
                    .hint {
                      margin: 0; font-size: 13px; color: rgba(255,255,255,.72);
                      line-height: 1.4; text-align: center;
                    }
                    .error {
                      background: #FFFCF8; border-radius: 16px; padding: 24px;
                      color: #BA1A1A; font-size: 16px; margin: 0;
                    }
                  </style>
                </head>
                <body>
                  <div class="wrap">%s</div>
                </body>
                </html>
                """.formatted(esc(title), body);
    }
}
