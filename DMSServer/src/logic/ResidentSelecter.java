package logic;

import DB.ApplicationParser;
import java.util.Iterator;
import java.util.TreeSet;
import models.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ResidentSelecter 
{
	static final String DRIVER_NAME = "mysql";
	static final String HOSTNAME = "wehatejava.czztgstzacsv.us-east-1.rds.amazonaws.com";
	static final String PORT = "3306";
	static final String DB_NAME = "Prototype";													//DB이름
	static final String USER_NAME = "admin"; 													//DB에 접속할 사용자 이름을 상수로 정의
	static final String PASSWORD = "En2i3oHKLGh9UlnbYFP1"; 									//사용자의 비밀번호를 상수로 정의
	static final String DB_URL = 
					"jdbc:" + 
					DRIVER_NAME + "://" + 
					HOSTNAME + ":" + 
					PORT + "/" + 
					DB_NAME + "?user=" + 
					USER_NAME + "&password=" + 
					PASSWORD; 
	
//	탑층 알고리즘 설계중
//	1. 탑층 capacity보다 신청이 많은지 확인
//	2. 일반 capacity보다 신청이 많은지 확인
//	
//	둘 다 true일 경우 -> 그대로 진행
//	1은 true, 2는 false일 경우 -> ???
//	1은 false, 2는 true일 경우 -> 1의 남은 capacity를 2에 합쳐서 선발
//	둘 다 false일 경우 -> 그대로 진행
		
	public static void selectionByChoice() throws ClassNotFoundException, SQLException
	{
		passerSelection("푸름1", 0);
		passerSelection("푸름1_탑층", 0);
		passerSelection("푸름2", 0);
		passerSelection("푸름2_탑층", 0);
		passerSelection("푸름3", 0);
		for(int choice = 1; choice < 4; choice++)
		{
			passerSelection("푸름1", choice);
			passerSelection("푸름1_탑층", choice);
			passerSelection("푸름2", choice);
			passerSelection("푸름2_탑층", choice);
			passerSelection("푸름3", choice);
			passerSelection("푸름4", choice);
			passerSelection("오름1", choice);
			passerSelection("오름2", choice);
			passerSelection("오름3", choice);
			passerSelection("신평_남", choice);
			passerSelection("신평_여", choice);
		}
	}
	
	public static void passerSelection(String dormName, int choice) throws SQLException, ClassNotFoundException
	{
		int semester = ApplicationParser.getSemester();
		
		int leftCapacity = getNumOfLeftSeat(dormName, semester);
		TreeSet<Application> apps = ApplicationParser.getUnsortedApps(dormName, choice, semester);
		
		Iterator<Application> iterator = apps.iterator();
		
		for(int i = 0; i < leftCapacity; i++)
		{
			Application temp = iterator.next();
			ApplicationParser.updatePasser(temp);
		}
	}
	
	public static int getNumOfLeftSeat(String dormName, int semester) throws ClassNotFoundException, SQLException	//생활관 이름과 학기를 넣으면 남은 자리의 수를 리턴하는 함수
	{
		Connection conn = null;
		Statement state = null;
		
		Class.forName("com.mysql.cj.jdbc.Driver");
		conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);		
		state = conn.createStatement();
		
		String pureSemester = String.valueOf(semester).substring(4);
		String getNumOfPassedAppsQuery;
		String getCapacityQuery;
		ResultSet passed;
		ResultSet capacity;
		if(pureSemester.equals("01") || pureSemester.equals("04") || pureSemester.equals("02") || pureSemester.equals("05"))
		{
			getNumOfPassedAppsQuery = "SELECT COUNT(*) FROM (SELECT * FROM 배정내역 WHERE 생활관명=" + dormName + " AND 학기=" + semester + ")";			
			getCapacityQuery = "SELECT 수용인원 FROM 생활관정보 WHERE 생활관명=" + dormName + "AND 학기=" + semester;
		}
		else
		{
			getNumOfPassedAppsQuery = "SELECT COUNT(*) FROM (SELECT * FROM 배정내역 WHERE 생활관명=" + dormName + " AND 학기=" + (semester - 1) + ")";
			getCapacityQuery = "SELECT 수용인원 FROM 생활관정보 WHERE 생활관명=" + dormName + "AND 학기=" + (semester - 1);
		}
		passed = state.executeQuery(getNumOfPassedAppsQuery);
		Statement state2 = conn.createStatement();
		capacity = state2.executeQuery(getCapacityQuery);
		
		capacity.next();
		passed.next();
		
		int leftCapacity = capacity.getInt("수용인원") - passed.getInt("COUNT(*)");
		
		return leftCapacity;
	}
	
	public static double getFinalScore(String studentId, int semester) throws ClassNotFoundException, SQLException
	{
		Connection conn = null;
		Statement state = null;
		
		Class.forName("com.mysql.cj.jdbc.Driver");
		conn = DriverManager.getConnection(DB_URL, USER_NAME, PASSWORD);		
		state = conn.createStatement();
		
		String getScoresQuery = "SELECT 학점,등급 FROM 점수 WHERE 학번=" + studentId + "AND 학기 BETWEEN '" + pastTwo(semester) + "' AND '" + pastOne(semester) + "'";	//직전 2학기 점수 테이블 가져오는 쿼리
		ResultSet scores = state.executeQuery(getScoresQuery);
		
		double sumOfTakenGrade = 0;
		double sumOfTakenCredit = 0;
		
		while(scores.next())
		{
			sumOfTakenCredit += scores.getInt("학점");
			switch(scores.getString("성적등급")) 
			{
			case "A+":
				sumOfTakenGrade += 4.5 * scores.getInt("학점");
				break;
			case "A":
				sumOfTakenGrade += 4 * scores.getInt("학점");
				break;
			case "B+":
				sumOfTakenGrade += 3.5 * scores.getInt("학점");
				break;
			case "B":
				sumOfTakenGrade += 3 * scores.getInt("학점");
				break;
			case "C+":
				sumOfTakenGrade += 2.5 * scores.getInt("학점");
				break;
			case "C":
				sumOfTakenGrade += 2 * scores.getInt("학점");
				break;
			case "D+":
				sumOfTakenGrade += 1.5 * scores.getInt("학점");
				break;
			case "D":
				sumOfTakenGrade += 1 * scores.getInt("학점");
				break;
			case "F":
				break;
			}
		}
		
		Statement state2 = conn.createStatement();
		String zipCodeQuery = "SELECT 보호자우편번호 FROM 학생 WHERE 학번=" + studentId;
		ResultSet zipCode = state2.executeQuery(zipCodeQuery);
		zipCode.next();
		
		return (sumOfTakenGrade/sumOfTakenCredit + getDistanceScore(zipCode.getString("보호자우편번호")));
	}
	
	public static double getDistanceScore(String s)
	{
		int a = Integer.parseInt(s);		
		if(a/100 == 402) return 0.4;	//울릉도
		
		a = a/1000;
		if(a==63) return 0.4;	//제주도
		else if(35 <a && a<44) return 0.1;	//경북, 대구
		else if(43 <a && a<54) return 0.2;	//울산, 부산, 경남
		else if(33 <a && a<36) return 0.2;	//대전
		else
			return 0.3;
	}

	
	public static int pastOne(int semester)
	{
		String pureSemester = String.valueOf(semester).substring(4);	//학기부분만 잘라냄 ex)201901에서 01
		
		switch(pureSemester)
		{
		case "01":				//1학기
			semester -= 97;
			break;
		case "02":				//여름 계절
			semester -= 98;
			break;
		case "03":				//여름 계절 이후
			semester -= 99;
			break;
		case "04":				//2학기
			semester -= 3;
			break;
		case "05":				//겨울 계절
			semester -= 4;
			break;
		case "06":				//겨울 계절 이후
			semester -= 5;
			break;
		}
		
		return semester;
	}
	
	public static int pastTwo(int semester)
	{
		String pureSemester = String.valueOf(semester).substring(4);	//학기부분만 잘라냄 ex)201901에서 01
		
		switch(pureSemester)
		{
		case "01":
		case "04":
			semester -= 100;
			break;
		case "02":
		case "05":
			semester -= 101;
			break;
		case "03":
		case "06":
			semester -= 102;
			break;
		}
		
		return semester;
	}
	
}