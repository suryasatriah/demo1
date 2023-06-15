package com.suryahidayat.demo;

import java.io.ByteArrayOutputStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

import lombok.Data;
import lombok.NoArgsConstructor;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Data
    @NoArgsConstructor
    public static class User {
        private String login;
        private String html_url;
    }

    @Bean
    public WebClient webClient() {
        return WebClient.create();
    }

    @RestController
    @RequestMapping("/users")
    public class UserController {

        @Autowired
        WebClient webClient;

        @GetMapping(produces = MediaType.APPLICATION_PDF_VALUE)
        public byte[] getUsersPdf() {
            User[] users = webClient.get()
                    .uri("https://api.github.com/users?per_page=50")
                    .retrieve()
                    .bodyToMono(User[].class)
                    .block();

            Document document = new Document(PageSize.A4);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter.getInstance(document, outputStream);

            document.open();

            document.add(new Paragraph("DATA HASIL QUERRY"));
            for (User user : users) {
                document.add(new Paragraph("Login: " + user.getLogin()));
                document.add(new Paragraph("URL: " + user.getHtml_url()));
                document.add(new Paragraph("\n"));
            }

            document.close();

            return outputStream.toByteArray();
        }

        @GetMapping("/getTest")
        public @ResponseBody String getText() {
            return "Test";
        }

    }

}
