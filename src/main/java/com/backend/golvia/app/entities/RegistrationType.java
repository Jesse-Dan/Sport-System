package com.backend.golvia.app.entities;

public class RegistrationType {

	private Long id;


	public RegistrationType() {
	}

	public RegistrationType(String name, String iconurl) {
		super();
		this.name = name;
		this.iconurl = iconurl;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getIconurl() {
		return iconurl;
	}

	public void setIconurl(String iconurl) {
		this.iconurl = iconurl;
	}

	private String name;

	private String iconurl;

}
