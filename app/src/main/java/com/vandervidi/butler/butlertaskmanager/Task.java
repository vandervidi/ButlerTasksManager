package com.vandervidi.butler.butlertaskmanager;

import android.util.Log;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Task implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8513818035567616532L;
	private int id;
	private String title;
	private String description;
	private Calendar taskDate;

	

	public Task(int id, String title, String description ) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		taskDate = null;
	}

	public void setDate(String date, String time){
		Log.i("TIME TEST", time);
		String tmpDate[], tmpTime[];
		tmpDate = date.split("/");
		tmpTime = time.split(":");
		taskDate = new GregorianCalendar(Integer.parseInt(tmpDate[2]), Integer.parseInt(tmpDate[1]) - 1 , Integer.parseInt(tmpDate[0]), Integer.parseInt(tmpTime[0]), Integer.parseInt(tmpTime[1]));
	}
	
	public Calendar getTaskDate() {
		return taskDate;
	}

	public void setTaskDate(Calendar taskDate) {
		this.taskDate = taskDate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	



	

	
}
