package timeme.bean;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class BriefBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String name;

	private long ellapsedTime;
	private long id;

	public BriefBean(String name, long id) {
		this.name = name;
		this.setId(id);
	}

	public String getName() {
		return name;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEllapsedTime() {
		return String.format(
				"%02d:%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(ellapsedTime),
				TimeUnit.MILLISECONDS.toMinutes(ellapsedTime)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
								.toHours(ellapsedTime)),
				TimeUnit.MILLISECONDS.toSeconds(ellapsedTime)
						- TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
								.toMinutes(ellapsedTime)));
	}

	public String getEllapsedTimeForTxtFile() {
		return String.format(
				"%02d:%02d",
				TimeUnit.MILLISECONDS.toHours(ellapsedTime),
				TimeUnit.MILLISECONDS.toMinutes(ellapsedTime)
						- TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
								.toHours(ellapsedTime)));
	}

	public void setEllapsedTime(long ellapsedTime) {
		this.ellapsedTime = this.ellapsedTime + ellapsedTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (int) (id ^ (id >>> 32));
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BriefBean other = (BriefBean) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Brief Name: " + name;
	}

	public String toFileString() {
		// TODO Auto-generated method stub
		return "\nBrief Name: " + name + "\tTime: " + getEllapsedTimeForTxtFile();
	}

}
