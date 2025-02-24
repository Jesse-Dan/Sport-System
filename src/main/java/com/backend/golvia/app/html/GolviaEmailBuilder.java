package com.backend.golvia.app.html;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class GolviaEmailBuilder {

    private String title;
    private String subtitle;
    private String bodyContent;
    private List<Button> buttons = new ArrayList<>();
    private Table table;
    private String footerText;

    public String build() {
        StringBuilder buttonsHtml = new StringBuilder();
        for (Button button : buttons) {
            String url = button.getUrl() != null ? button.getUrl().replace("%", "%%") : "#";
            String text = button.getText() != null ? button.getText().replace("%", "%%") : "";
            buttonsHtml.append(
                    "<a href=" + url + " class=cta-button>" + text + "</a>"
            );
        }

        String tableHtml = (table != null) ? buildTableHtml(table) : "";

        return "<!DOCTYPE html>" +
                "<html lang=en>" +
                "<head>" +
                "    <meta charset=UTF-8>" +
                "    <meta name=viewport content=width=device-width, initial-scale=1.0>" +
                "    <title>Golvia</title>" +
                "    <style>" +
                "        /* General Styles */" +
                "        body {" +
                "            font-family: 'Helvetica Neue', Arial, sans-serif;" +
                "            background-color: #f9f9f9;" +
                "            margin: 0;" +
                "            padding: 0;" +
                "            color: #444444;" +
                "            text-align: center;" +
                "        }" +
                "        /* Email Container */" +
                "        .email-container {" +
                "            max-width: 650px;" +
                "            margin: 30px auto;" +
                "            background: #ffffff;" +
                "            border-radius: 12px;" +
                "            overflow: hidden;" +
                "            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);" +
                "            border: 1px solid #e0e0e0;" +
                "        }" +
                "        /* Header */" +
                "        .email-header {" +
                "            background-color: #4a90e2;" +
                "            padding: 25px;" +
                "            text-align: center;" +
                "            color: white;" +
                "        }" +
                "        .email-header img {" +
                "            max-width: 120px;" +
                "            margin-bottom: 10px;" +
                "        }" +
                "        .email-header h1 {" +
                "            font-size: 24px;" +
                "            margin: 0;" +
                "        }" +
                "        /* Content */" +
                "        .email-body {" +
                "            padding: 25px;" +
                "            font-size: 16px;" +
                "            line-height: 1.8;" +
                "            text-align: left;" +
                "            background: #fefefe;" +
                "        }" +
                "        .email-title {" +
                "            font-size: 26px;" +
                "            font-weight: bold;" +
                "            margin-bottom: 15px;" +
                "            color: #333333;" +
                "        }" +
                "        .email-subtitle {" +
                "            font-size: 20px;" +
                "            color: #666666;" +
                "            margin-bottom: 20px;" +
                "        }" +
                "        /* Buttons */" +
                "        .cta-button {" +
                "            display: inline-block;" +
                "            margin: 15px 10px;" +
                "            padding: 12px 30px;" +
                "            text-decoration: none;" +
                "            color: white;" +
                "            background-color: #4a90e2;" +
                "            border-radius: 6px;" +
                "            font-weight: bold;" +
                "            font-size: 16px;" +
                "            transition: background-color 0.3s ease, box-shadow 0.3s ease;" +
                "            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);" +
                "        }" +
                "        .cta-button:hover {" +
                "            background-color: #357ABD;" +
                "            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.2);" +
                "        }" +
                "        /* Table Styles */" +
                "        .email-table {" +
                "            width: 100%;" +
                "            border-collapse: collapse;" +
                "            margin-top: 25px;" +
                "            background-color: #fafafa;" +
                "            border-radius: 6px;" +
                "            overflow: hidden;" +
                "        }" +
                "        .email-table th, .email-table td {" +
                "            border: 1px solid #ddd;" +
                "            padding: 12px;" +
                "            text-align: left;" +
                "            font-size: 14px;" +
                "        }" +
                "        .email-table th {" +
                "            background-color: #f0f0f0;" +
                "            font-weight: bold;" +
                "            text-transform: uppercase;" +
                "            color: #555555;" +
                "        }" +
                "        .email-table tr:nth-child(even) {" +
                "            background-color: #f9f9f9;" +
                "        }" +
                "        .email-table tr:hover {" +
                "            background-color: #f1f1f1;" +
                "        }" +
                "        /* Footer */" +
                "        .email-footer {" +
                "            padding: 15px;" +
                "            font-size: 14px;" +
                "            color: #888888;" +
                "            background-color: #f4f4f4;" +
                "            text-align: center;" +
                "            border-top: 1px solid #e0e0e0;" +
                "        }" +
                "        .email-footer p {" +
                "            margin: 0;" +
                "        }" +
                "    </style>" +
                "</head>" +
                "<body>" +
                "    <div class=email-container>" +
                "        <div class=email-header>" +
                "            <img src=https://golviasports.com/static/media/logo-white.c8694e98a6d8b5da08ff031058e90a57.svg alt=Golvia Logo>" +
                "            <h1>Golvia</h1>" +
                "        </div>" +
                "        <div class=email-body>" +
                "            <div class=email-title>" + (title != null ? title.replace("%", "%%") : "") + "</div>" +
                "            <div class=email-subtitle>" + (subtitle != null ? subtitle.replace("%", "%%") : "") + "</div>" +
                "            <div>" + (bodyContent != null ? bodyContent.replace("%", "%%") : "") + "</div>" +
                "            " + tableHtml +
                "            " + buttonsHtml +
                "        </div>" +
                "        <div class=email-footer>" +
                "            <p>" + (footerText != null ? footerText : "© 2024 Golvia LTD. All Rights Reserved.") + "</p>" +
                "        </div>" +
                "    </div>" +
                "</body>" +
                "</html>";
    }

    private String buildTableHtml(Table table) {
        StringBuilder tableHtml = new StringBuilder();
        tableHtml.append("<table class=email-table>");

        tableHtml.append("<tr>");
        for (String header : table.getHeaders()) {
            tableHtml.append("<th>").append(header).append("</th>");
        }
        tableHtml.append("</tr>");

        for (List<String> row : table.getRows()) {
            tableHtml.append("<tr>");
            for (String cell : row) {
                tableHtml.append("<td>").append(cell).append("</td>");
            }
            tableHtml.append("</tr>");
        }

        tableHtml.append("</table>");
        return tableHtml.toString();
    }

    @Data
    @NoArgsConstructor
    public static class Button {
        private String text;
        private String url;

        public Button(String text, String url) {
            this.text = text;
            this.url = url;
        }
    }

    @Data
    @NoArgsConstructor
    public static class Table {
        private List<String> headers = new ArrayList<>();
        private List<List<String>> rows = new ArrayList<>();
    }
}
