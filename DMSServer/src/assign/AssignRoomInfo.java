package assign;

import java.sql.Date;

import shared.classes.RoomInfo;

public class AssignRoomInfo extends RoomInfo  {
	private String seat;
	private boolean isEmpty;
	private String studentId;
	private Date checkout;
	
		
	public String getSeat()
	{
		return seat;
	}
	public void setSeat(String seat)
	{
		this.seat = seat;
	}
	public boolean getIsEmpty()
	{
		return isEmpty;
	}
	public void setIsEmpty(boolean isEmpty)
	{
		this.isEmpty = isEmpty;
	}
	public String getStudentId()
	{
		return studentId;
	}
	public void setStudentId(String studentId)
	{
		this.studentId = studentId;
	}
	public Date getCheckOut()
	{
		return checkout;
	}
	public void setCheckout(Date checkout)
	{
		this.checkout = checkout;
	}
}
