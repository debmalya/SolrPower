package org.deb.bbclive.model;

import java.io.Serializable;

public class DisplayableItem implements Displayable,Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3042258935295062882L;
	private String text;
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	private String link;
	
	
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public DisplayableItem(String text, String link) {
		super();
		this.text = text;
		this.link = link;
	}
	
	
}
