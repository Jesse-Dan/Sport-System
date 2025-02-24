package com.backend.golvia.app.html.auth;

import com.backend.golvia.app.html.GolviaEmailBuilder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor
@Data
public class TestUsage {

    private String firstName;

    public String build() {
        GolviaEmailBuilder html = new GolviaEmailBuilder();

        html.setTitle("Hello " + (firstName != null ? firstName.toUpperCase() : "User"));
        html.setSubtitle("Account Creation Email");
        html.setBodyContent("Welcome to Golvia! We're excited to have you on board.");
        html.getButtons().add(new GolviaEmailBuilder.Button(
                "Go to Dashboard",
                "https://html.com"
        ));

        GolviaEmailBuilder.Table table = new GolviaEmailBuilder.Table();
        List<String> headers = new ArrayList<>();
        List<List<String>> rows = new ArrayList<>();

        headers.add("Name");
        headers.add("Class");
        headers.add("Age");

        List<String> row1 = new ArrayList<>();
        row1.add("John Doe");
        row1.add("Ss3");
        row1.add("32");

        List<String> row2 = new ArrayList<>();
        row2.add("Jane Smith");
        row2.add("Jss2");
        row2.add("54");

        rows.add(row1);
        rows.add(row2);

        table.setHeaders(headers);
        table.setRows(rows);
        html.setTable(table);

        System.out.println("Headers: " + headers);
        System.out.println("Rows: " + rows);

        return html.build();
    }
}
