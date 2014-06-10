package org.eviline.webapp.dbo;

import java.util.Date;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.annotate.JsonSerialize.Inclusion;

@XmlRootElement(name="score")
@JsonSerialize(include=Inclusion.NON_NULL)
public class Score {
	private long lines;
	private long score;
	private String name;
	private Date date;
	
	public long getLines() {
		return lines;
	}
	public void setLines(long lines) {
		this.lines = lines;
	}
	public long getScore() {
		return score;
	}
	public void setScore(long score) {
		this.score = score;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
}
