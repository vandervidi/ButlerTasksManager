package com.vandervidi.butler.butlertaskmanager;

import java.io.Serializable;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Task implements Serializable{
    private static final long serialVersionUID = 1L;
    private static final String LOG_TAG = "Task Class";

	//private static final long serialVersionUID = 8513818035567616532L;
	private int id;
	private String title;
	private String description;
	private Calendar taskDate;
    private double lat;
    private double lng;

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Task(int id, String title, String description, double lat, double lng ) {
		super();
		this.id = id;
		this.title = title;
		this.description = description;
		taskDate = null;
        this.lat = lat;
        this.lng = lng;
	}

	public void setDate(String date, String time){

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
