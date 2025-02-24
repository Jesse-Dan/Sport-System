////package com.backend.golvia.app.services;
////
////import org.springframework.stereotype.Service;
////import org.springframework.web.client.RestTemplate;
////
////import com.backend.golvia.app.models.request.EmailRequest;
////
////import org.springframework.beans.factory.annotation.Value;
////import org.springframework.http.HttpEntity;
////import org.springframework.http.HttpHeaders;
////import org.springframework.http.HttpMethod;
////import org.springframework.http.HttpStatus;
////import org.springframework.http.ResponseEntity;
////
////@Service
////public class ApiService {
////
////    private final RestTemplate restTemplate;
////
////    @Value("${spring.sendgrid.api-key}")
////    private String SENDER_TOKEN;
////
////
////    @Value("${sendgrid.url}")
////    private String SENDGRID_URL;
////    //
////
////    public ApiService(RestTemplate restTemplate) {
////        this.restTemplate = restTemplate;
////    }
////
////    public Object sendPostRequest(EmailRequest req) {
////
////
////    	System.out.println("**************** inside  sendPostRequest function *****************");
////
////        // Set the headers
////        HttpHeaders headers = new HttpHeaders();
////
////        headers.set("Content-Type", "application/json");
////        headers.set("X-Requested-With", "XMLHttpRequest");
////        headers.set("Authorization","Bearer " + SENDER_TOKEN);
////
////
////        // Create the request body
////        HttpEntity<EmailRequest> requestEntity = new HttpEntity<>(req, headers);
////
////        System.out.println("**************** printing mailersend requestEntity *****************");
////        System.out.println(req);
////        System.out.println(requestEntity);
////
////        ResponseEntity<String> response = null;
////
////        try {
////            response = restTemplate.exchange(SENDGRID_URL, HttpMethod.POST, requestEntity, String.class);
////
////            System.out.println("**************** printing mailersend response after trial *****************!");
////            System.out.println(response);
////
////            // Check response status
////            if (response.getStatusCode() == HttpStatus.ACCEPTED) {
////                System.out.println("Email sent successfully!");
////            } else {
////                System.out.println("Failed to send email. Status code: " + response.getStatusCode());
////            }
////        } catch (Exception e) {
////
////            System.out.println(e);
////        }
////
////        System.out.println("**************** printing sendgrid body *****************");
////        System.out.println(response);
////
////        // Return the response body
////        return response;
////    }
////}
////
//package com.backend.golvia.app.services;
//
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.HttpEntity;
//import org.springframework.http.HttpHeaders;
//import org.springframework.http.HttpMethod;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.stereotype.Service;
//import org.springframework.web.client.RestTemplate;
//
//import com.backend.golvia.app.models.request.EmailRequest;
//
//@Service
//public class ApiService {
//
//    private final RestTemplate restTemplate;
//
//    @Value("${spring.sendgrid.api-key}")
//    private String senderToken;
//
//    @Value("${sendgrid.url}")
//    private String sendGridUrl;
//
//    public ApiService(RestTemplate restTemplate) {
//        this.restTemplate = restTemplate;
//    }
//
//    public ResponseEntity<String> sendPostRequest(EmailRequest req) {
//        System.out.println("**************** Inside sendPostRequest function *****************");
//
//        // Set the headers
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Content-Type", "application/json");
//        headers.set("X-Requested-With", "XMLHttpRequest");
//        headers.set("Authorization", "Bearer " + senderToken);
//
//        // Create the request body
//        HttpEntity<EmailRequest> requestEntity = new HttpEntity<>(req, headers);
//
//        System.out.println("**************** Printing MailerSend requestEntity *****************");
//        System.out.println(requestEntity);
//
//        ResponseEntity<String> response;
//
//        try {
//            response = restTemplate.exchange(sendGridUrl, HttpMethod.POST, requestEntity, String.class);
//            System.out.println("**************** Printing MailerSend response after trial *****************!");
//            System.out.println(response);
//
//            // Check response status
//            if (response.getStatusCode() == HttpStatus.ACCEPTED) {
//                System.out.println("Email sent successfully!");
//            } else {
//                System.out.println("Failed to send email. Status code: " + response.getStatusCode());
//            }
//            return response;
//        } catch (Exception e) {
//            System.err.println("Error occurred while sending email: " + e.getMessage());
//            e.printStackTrace(); // Print stack trace for debugging
//        }
//
//        System.out.println("**************** Printing SendGrid response body *****************");
//        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to send email");
//    }
//}
