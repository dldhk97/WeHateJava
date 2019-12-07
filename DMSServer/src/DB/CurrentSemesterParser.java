package DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CurrentSemesterParser 
{
	public static int getCurrentSemester() throws ClassNotFoundException, SQLException
	{
		Date time = new Date();
		SimpleDateFormat format1 = new SimpleDateFormat ("yyyy-MM-dd");					
		String time1 = format1.format(time);
		int result;
		
		String sql1 = "SELECT `스케쥴 할일 코드`, 시작일, 종료일  비고 FROM " + DB.DBHandler.DB_NAME + ".스케쥴 WHERE `스케쥴 할일 코드` = 1학기"; //신청테이블에서 하나만 가져와서 그 학기를 봄
		String sql2 = "SELECT `스케쥴 할일 코드`, 시작일, 종료일  비고 FROM " + DB.DBHandler.DB_NAME + ".스케쥴 WHERE `스케쥴 할일 코드` = 여름계절"; //신청테이블에서 하나만 가져와서 그 학기를 봄
		String sql3 = "SELECT `스케쥴 할일 코드`, 시작일, 종료일  비고 FROM " + DB.DBHandler.DB_NAME + ".스케쥴 WHERE `스케쥴 할일 코드` = 여름계절이후"; //신청테이블에서 하나만 가져와서 그 학기를 봄
		String sql4 = "SELECT `스케쥴 할일 코드`, 시작일, 종료일  비고 FROM " + DB.DBHandler.DB_NAME + ".스케쥴 WHERE `스케쥴 할일 코드` = 2학기"; //신청테이블에서 하나만 가져와서 그 학기를 봄
		String sql5 = "SELECT `스케쥴 할일 코드`, 시작일, 종료일  비고 FROM " + DB.DBHandler.DB_NAME + ".스케쥴 WHERE `스케쥴 할일 코드` = 겨울계절"; //신청테이블에서 하나만 가져와서 그 학기를 봄
		String sql6 = "SELECT `스케쥴 할일 코드`, 시작일, 종료일  비고 FROM " + DB.DBHandler.DB_NAME + ".스케쥴 WHERE `스케쥴 할일 코드` = 겨울계절이후"; //신청테이블에서 하나만 가져와서 그 학기를 봄
		
//		String sql = "SELECT `스케쥴 할일 코드`, 비고 FROM " + DBinfo.DB_NAME + ".스케쥴 <![CDATA[ WHERE 시작일 <="+time1+" and "+time1 + "<= 종료일   ]]>"; //만약 위 방법이 안되면 사용할것		 

		Connection connection = DBHandler.INSTANCE.getConnetion();
		PreparedStatement state1 = connection.prepareStatement(sql1);
		ResultSet rs1 = state1.executeQuery();
		rs1.next();		
		PreparedStatement state2 = connection.prepareStatement(sql2);
		ResultSet rs2 = state2.executeQuery();
		rs2.next();		
		PreparedStatement state3 = connection.prepareStatement(sql3);
		ResultSet rs3 = state3.executeQuery();
		rs3.next();		
		PreparedStatement state4 = connection.prepareStatement(sql4);
		ResultSet rs4 = state4.executeQuery();
		rs4.next();		
		PreparedStatement state5 = connection.prepareStatement(sql5);
		ResultSet rs5 = state5.executeQuery();
		rs5.next();		
		PreparedStatement state6 = connection.prepareStatement(sql6);
		ResultSet rs6 = state6.executeQuery();
		rs6.next();		
		
		if(rs1.getDate("시작일").before(time)&& time.before(rs1.getDate("종료일")))
		{
			result = rs1.getInt("비고"); //1학기
			rs1.close();
			DBHandler.INSTANCE.returnConnection(connection);
		}
		else if(rs2.getDate("시작일").before(time)&& time.before(rs2.getDate("종료일"))) 
		{
			if(rs3.getDate("시작일").before(time)&& time.before(rs3.getDate("종료일"))) 
			{
				result = rs3.getInt("비고");//여름계절이후
				rs3.close();
				DBHandler.INSTANCE.returnConnection(connection);
			}
			else
			{
				result = rs2.getInt("비고"); //여름계절
				rs2.close();
				DBHandler.INSTANCE.returnConnection(connection);
			}
		}
		else if(rs4.getDate("시작일").before(time)&& time.before(rs4.getDate("종료일"))) 		
		{
			rs4.close();
			DBHandler.INSTANCE.returnConnection(connection);
			result = rs4.getInt("비고"); //2학기
		}
		else if(rs5.getDate("시작일").before(time)&& time.before(rs5.getDate("종료일"))) 
		{
			if(rs6.getDate("시작일").before(time)&& time.before(rs6.getDate("종료일"))) 
			{
				rs6.close();
				DBHandler.INSTANCE.returnConnection(connection);
				result = rs6.getInt("비고"); //겨울계절이후
			}
			else
			{
				rs5.close();
				DBHandler.INSTANCE.returnConnection(connection);
				result = rs5.getInt("비고"); //겨울계절
			}
		}
		
			System.out.println("CurrentSemesterParser 에러");
			return 0;
		
	}
}