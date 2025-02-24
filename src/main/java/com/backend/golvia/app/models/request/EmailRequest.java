package com.backend.golvia.app.models.request;

import java.util.List;

public class EmailRequest {

    private EmailAddress from;
    private List<EmailAddress> to;
    private String subject;
    private String text;
    private String html;

    // Getters and Setters

    public EmailAddress getFrom() {
		return from;
	}

	public EmailRequest() {
		super();
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toString() {
		return "EmailRequest [from=" + from + ", to=" + to + ", subject=" + subject + ", text=" + text + ", html="
				+ html + "]";
	}

	public EmailRequest(EmailAddress from, List<EmailAddress> to, String subject, String text, String html) {
		super();
		this.from = from;
		this.to = to;
		this.subject = subject;
		this.text = text;
		this.html = html;
	}

	public void setFrom(EmailAddress from) {
		this.from = from;
	}

	public List<EmailAddress> getTo() {
		return to;
	}

	public void setTo(List<EmailAddress> to) {
		this.to = to;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getHtml() {
		return html;
	}

	public void setHtml(String html) {
		this.html = html;
	}

	public static class EmailAddress {
        private String email;

        // Constructor, Getters, and Setters
        public EmailAddress(String email) {
            this.email = email;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }
    }
}

