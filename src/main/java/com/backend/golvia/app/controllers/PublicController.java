package com.backend.golvia.app.controllers;

import com.backend.golvia.app.dtos.CustomEmailRequest;
import com.backend.golvia.app.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/public")
public class PublicController {
    @Autowired
    private EmailService emailService;

    @GetMapping("/email-form")
    public String getEmailForm() {
        return "<html>" +
                "<head>" +
                "<title>Golvia Email Form</title>" +
                "<style>" +
                "body { font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif; background-color: #f0f4f8; padding: 20px; margin: 0; }" +
                "h2 { color: #333; text-align: center; }" +
                ".container { display: flex; justify-content: space-between; align-items: stretch; }" +
                "form { background-color: #ffffff; padding: 30px; border-radius: 10px; box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1); max-width: 600px; margin-right: 20px; flex-grow: 1; }" +
                "label { font-weight: bold; margin-top: 15px; display: inline-block; }" +
                "input[type='text'], input[type='email'], textarea { width: calc(100% - 22px); padding: 12px; margin-top: 5px; border: 1px solid #ccc; border-radius: 5px; transition: border-color 0.3s;" +
                "box-shadow: inset 0 1px 3px rgba(0,0,0,.1); }" +
                "input[type='text']:focus, input[type='email']:focus, textarea:focus { border-color: #28a745; outline: none; }" +
                "input[type='submit'] { background-color: #28a745; color: white; border: none; padding: 12px 20px; cursor: pointer; border-radius: 5px; font-size: 16px; width: 100%; margin-top: 20px;" +
                "transition: background-color 0.3s; }" +
                "input[type='submit']:hover { background-color: #218838; }" +
                ".preview { margin-top: 20px; padding: 15px; border-radius: 5px; background-color: #e9ecef; max-height: auto;" +
                "border-left: 5px solid #28a745;}" +
                ".preview h3 { margin-top: 0; color:#333;}" +
                ".preview p { margin-bottom: 10px; }" +
                ".view-toggle { display:flex; justify-content:center;margin-bottom:15px;}" +
                ".view-toggle label { margin-right:.5rem;}" +
                ".toggle-group { display: flex; justify-content: center; }" +
                ".toggle-group label { cursor: pointer; padding: 10px 20px; border: 1px solid #ccc; border-radius: 5px; margin: 0 5px; background-color: #f0f4f8; transition: background-color 0.3s; }" +
                ".toggle-group input { display: none; }" +
                ".toggle-group input:checked + label { background-color: #28a745; color: white; border-color: #28a745; }" +
                "@media (max-width:768px) {" +
                ".container { flex-direction: column-reverse; }" +
                "form, .preview { max-width:none;width:auto;}" +
                ".preview{margin-top:.5rem;}" +
                "}" +
                ".loading-overlay {" +
                "  display: none; position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); justify-content: center; align-items: center; z-index: 1000;" +
                "}" +
                ".loading-overlay.visible { display: flex; }" +
                ".loading-spinner {" +
                "  border: 5px solid #f3f3f3; border-top: 5px solid #28a745; border-radius: 50%; width: 50px; height: 50px; animation: spin 1s linear infinite;" +
                "}" +
                "@keyframes spin { 0% { transform: rotate(0deg); } 100% { transform: rotate(360deg); }}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<h2>Send Custom Email</h2>" +
                "<div class='view-toggle'>" +
                "<div class='toggle-group'>" +
                "<input type='radio' id='webView' name='viewToggle' checked onchange='toggleView()'>" +
                "<label for='webView'>Web View</label>" +
                "<input type='radio' id='mobileView' name='viewToggle' onchange='toggleView()'>" +
                "<label for='mobileView'>Mobile View</label>" +
                "</div>" +
                "</div>" +
                "<div class='container web-view' id='emailContainer'>" +
                "<form id='emailForm' action='/public/send-custom-mail' method='post' onsubmit='return sendEmail(event)'>" +
                "<label><input type='radio' name='recipientType' value='singleUser' checked onchange='toggleFields()'> Send to Single User</label><br>" +
                "<label><input type='radio' name='recipientType' value='allUsers' onchange='toggleFields()'> Send to All Users</label><br>" +
                "<label><input type='radio' name='recipientType' value='specifiedUsers' onchange='toggleFields()'> Send to Specified Users</label><br>" +
                "<div id='singleUserSection'>" +
                "<label>To (required):</label><input type='email' name='to' required><br>" +
                "</div>" +
                "<div id='allUsersSection' style='display:none;'>" +
                "</div>" +
                "<div id='specifiedUsersSection' style='display:none;'>" +
                "<label>List of Emails (comma-separated):</label><input type='text' name='emails' placeholder='example1@example.com, example2@example.com'><br>" +
                "</div>" +
                "<label>Subject (required):</label><input type='text' name='subject' required><br>" +
                "<label>HTML Content (required):</label><textarea name='htmlContent' rows='10' required oninput='updatePreview()'></textarea><br>" +
                "<input type='submit' value='Send Email'>" +
                "</form>" +
                "<div class='preview' id='emailPreview'>" +
                "<h3>Email Preview:</h3>" +
                "<p>No content yet.</p>" +
                "</div>" +
                "</div>" +
                "<div class='loading-overlay' id='loadingOverlay'>" +
                "<div class='loading-spinner'></div>" +
                "</div>" +
                "<script>" +
                "function toggleView() {" +
                "   const container = document.getElementById('emailContainer');" +
                "   const isMobile = document.getElementById('mobileView').checked;" +
                "   if (isMobile) {" +
                "       container.classList.remove('web-view');" +
                "       container.classList.add('mobile-view');" +
                "   } else {" +
                "       container.classList.remove('mobile-view');" +
                "       container.classList.add('web-view');" +
                "   }" +
                "}" +
                "function updatePreview() {" +
                "   var htmlContent = document.querySelector('textarea[name=\"htmlContent\"]').value;" +
                "   document.getElementById('emailPreview').innerHTML = htmlContent;" +
                "}" +
                "function toggleFields() {" +
                "   var recipientType = document.querySelector('input[name=\"recipientType\"]:checked').value;" +
                "   document.getElementById('singleUserSection').style.display = recipientType === 'singleUser' ? 'block' : 'none';" +
                "   document.getElementById('allUsersSection').style.display = recipientType === 'allUsers' ? 'block' : 'none';" +
                "   document.getElementById('specifiedUsersSection').style.display = recipientType === 'specifiedUsers' ? 'block' : 'none';" +
                "}" +
                "async function sendEmail(event) {" +
                "   event.preventDefault();" +
                "   document.getElementById('loadingOverlay').classList.add('visible');" +
                "   const formData = new FormData(document.getElementById('emailForm'));" +
                "   const recipientType = document.querySelector('input[name=\"recipientType\"]:checked').value;" +
                "   const emailRequest = {" +
                "       to: recipientType === 'singleUser' ? formData.get('to') : null," +
                "       subject: formData.get('subject')," +
                "       htmlContent: formData.get('htmlContent')," +
                "       allUsers: recipientType === 'allUsers'," +
                "       specifiedUsers: recipientType === 'specifiedUsers'," +
                "       emails: recipientType === 'specifiedUsers' ? formData.get('emails').split(',').map(email => email.trim()) : []" +
                "   };" +
                "   try {" +
                "       const response = await fetch('/public/send-custom-mail', {" +
                "           method: 'POST'," +
                "           body: JSON.stringify(emailRequest)," +
                "           headers: { 'Content-Type': 'application/json' }" +
                "       });" +
                "       const result = await response.text();" +
                "       alert(result);" +
                "   } catch (error) {" +
                "       alert('Failed to send email: ' + error.message);" +
                "   } finally {" +
                "       document.getElementById('loadingOverlay').classList.remove('visible');" +
                "   }" +
                "}" +
                "</script>" +
                "</body>" +
                "</html>";
    }

    @PostMapping("/send-custom-mail")
    public ResponseEntity<String> sendCustomMail(@RequestBody CustomEmailRequest emailRequest) {
        try {
            emailService.sendCustomMail(emailRequest);
            return ResponseEntity.ok(emailRequest.getSubject().toUpperCase() + " Mail Sent!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Failed to send " + emailRequest.getSubject().toUpperCase() + " emails: " + e.getMessage());
        }
    }
}
