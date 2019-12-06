import java.util.ArrayList;

import DB.DormParser;
import DB.StudentParser;
import logic.ScheduleCheck;
import protocol.ProtocolField;
import shared.classes.Dormitory;
import shared.enums.Gender;

//디버깅용 클래스
//대충 클라이언트에서 어떤 요청이 왔을때 그에 대한 반응(로직)을 모아둠.
//주석으로 로직을 설명하고, 실제 동작가능한 코드도 넣어라.
//실제 네트워킹이 정상적으로 작동하면, 이 클래스를 삭제하고 코드를 적절한 위치에 옮기도록.
//로직을 변경하게 될 경우 맨 아래 주석에 로그를 남겨주세요!!!

//학기코드
//201901 : 1학기
//201902 : 여름계절
//201903 : 여름계절이후
//201904 : 2학기
//201905: 겨울계절
//201906 : 겨울계절이후

public class Responser
{
	//아래 규칙을 지켜주세요.
	//(1) 메소드 위에 붙는 모든 주석은 클라이언트에서의 동작이다.
	//(2) 메소드 안에 있는 주석 및 코드는 서버에서의 동작이다.
	//(3) 괄호가 쳐진 인덱스와 내용은 클라이언트에서의 동작이다.
	//(4) 주석 아래에 그 동작에 해당하는 메소드를 적는다.
	//(5) 전체적인 흐름을 한눈에 파악할 수 있도록, 코드가 길어지면 다른클래스로 분할한다.
	//(6) IF문과 같은 간단한 제어문은 있어도 상관없다.
	
//	[예시]
//	학생 - 생활관 입사 신청 - 들어왔을 때
//	public void student_submitApplicationPage_onEnter()
//	{
//		//1. 스케쥴을 확인하고 입사 신청 가능한 날짜인지 조회 -> TRUE이면 다음으로, FALSE이면 못들어가게 막음
//		boolean isPassed = DifferentClass.checkSchedule();
//		
//		//2. 받은 요청의 헤더에서 학번을 알아낸다.
//		String id = DifferentClass2.checkId();
//		
//		//3. 학생테이블에서 학번으로 조회하여 성별을 알아낸다.
//		Gender g = DifferentClass2.getGender();
//		
//		//4. 생활관 테이블에서 이번 학기에 해당하고, 성별에 해당하는 기숙사 정보 목록을 가져온다.
//		ArrayList<Dormitory> dormList = DifferentClass3.getDorms();
//		
//		//5. 가져와야할 정보는 생활관 테이블의 생활관명, 기간구분(없으면말고), 식사구분, 5일식 식비, 7일식 식비, 관리비,
//		//	 스케쥴 테이블에서 비고(안내사항)를 가져온다.
//		ArrayList<DataModel> rs = DBManager.getDatas();
//		
//		//6. 해당 정보를 객체화, 배열로 만들어 클라이언트에게 전송한다.
//		Networking(rs);
//	}
	
	//학생 - 생활관 입사 신청 - 들어왔을 때
	public void student_submitApplicationPage_onEnter() throws Exception
	{
		//1. 스케쥴을 확인하고 입사 신청 가능한 날짜인지 조회 -> TRUE이면 다음으로, FALSE이면 못들어가게 막음
		//boolean isPassed = ScheduleCheck.check(ProtocolField.Code1.Page.입사신청);
		
		//2. 받은 요청의 헤더에서 학번을 알아낸다.
		String id = "20160469"; //수정 필요
		
		//3. 학생테이블에서 학번으로 조회하여 성별을 알아낸다.
		Gender g = StudentParser.getGender(id);
		
		//4. 생활관 테이블에서 이번 학기에 해당하고, 성별에 해당하는 기숙사 정보 목록을 가져온다.
		ArrayList<Dormitory> dList = new ArrayList<Dormitory>();
		dList = DormParser.getDormList(g);
		
		//5. 가져와야할 정보는 생활관 테이블의 생활관명, 기간구분(없으면말고), 식사구분, 5일식 식비, 7일식 식비, 관리비,
		//	 스케쥴 테이블에서 비고(안내사항)를 가져온다.
		
		
		//6. 해당 정보를 객체화, 배열로 만들어 클라이언트에게 전송한다.
	}
	
	//학생 - 생활관 입사 신청 - 등록 버튼 클릭 시
	public void student_submitApplicationPage_onSubmit()
	{
		//1. 받은 요청의 헤더에서 학번을 알아낸다. 
		//2. 신청 테이블에서 해당 학번이 이번 학기에 신청한 내역이 있는지 조회 -> TRUE 이면 내역 취소하고 하라고 클라이언트에게 알려줌. FALSE이면 다음으로
		//3. 받은 데이터를 역직렬화한다. ([생활관구분, 기간구분, 식사구분] x4 와 휴대전화번호, 코골이여부가 나옴)
		//4. 해당 배열을 신청 데이트에 INSERT한다.
		//5. 클라이언트에게 성공 여부를 알려준다.(성공/DB연결 오류로 인한 실패/DB사망/알수없는오류 등등...)
	}
	
	//학생 - 생활관 입사 신청 - 취소 버튼 클릭 시
	public void student_submitApplicationPage_onCancel()
	{
		//1. 받은 요청의 헤더에서 학번을 알아낸다. 
		//2. 신청 테이블에서 해당 학번이 이번 학기에 신청한 내역이 있는지 조회 -> TRUE 이면 다음으로, FALSE이면 클라이언트에게 내역 없다고 알려줌.
		//3. DB에 삭제 요청을 한다.
		//4. 클라이언트에게 삭제 성공 여부를 알려준다.
	}
	
	//------------------------------------------------------------------------
	
	//학생 - 생활관 신청 조회 - 들어왔을 때
	public void student_CheckApplicationPage_onEnter()
	{
		//1. 스케쥴을 확인하고 신청내역 조회 가능한 날짜인지 조회 -> TRUE이면 다음으로, FALSE이면 못들어가게 막음
		//2. 스케쥴 테이블에서 비고(안내사항)를 가져온다.
		//3. 스케쥴 객체를 클라이언트에게 전송한다.
		//(4. 클라이언트에서는 받은 비고(안내사항)을 표시한다)
	}
	
	//학생 - 생활관 신청 조회 - 조회 버튼 클릭 시
	public void student_CheckApplicationPage_onCheck()
	{
		//1. 받은 요청의 헤더에서 학번을 알아낸다. 
		//2. 신청 테이블에서 해당 학번이 이번 학기에 신청한 내역 중 지망, 생활관명, 식사구분을 조회. (클라이언트의 '생활관 입사지원 내역' 테이블뷰에 표시할 것임)
		//3. 신청 테이블에서 해당 학번이 이번 학기에 신청한 내역 중 합격여부가 T인 내역의 지망, 생활관명, 식사구분, 합격여부, 납부여부를 조회.
		//	 (클라이언트의 '생활관 선발 결과' 테이블뷰에 표시할 것임)
		//4. 조회된 내역을 객체화, 배열에 담아 클라이언트에게 반환한다.
	}
	
	//------------------------------------------------------------------------
	
	//학생 - 생활관 고지서 조회 - 들어왔을 때
	public void student_CheckBillPage_onEnter()
	{
		//1. 스케쥴을 확인하고 고지서 조회 가능한 날짜인지 조회 -> TRUE이면 괜찮다고 클라이언트에게 전송, FALSE이면 못들어가게 막음
	}
	
	//학생 - 생활관 고지서 조회 - 조회 버튼 클릭 시
	public void student_CheckBillPage_onCheck()
	{
		//1. 받은 요청의 헤더에서 학번을 알아낸다. 
		//2. 신청 테이블에서 해당 학번이 이번 학기에 신청한 내역 중 합격여부가 T인 내역 조회 -> 내역 있으면 다음으로, 없으면 없다고 클라이언트에게 알려줌
		//3. 신청 테이블에서 해당 학번이 이번 학기에 신청한 내역 중 합격여부가 T인 내역의 식사구분, 생활관구분을 알아낸다.
		//4. 해당 생활관, 해당 식비로 총 금액을 알아낸다.
		//5. 랜덤 생성한 계좌번호와, 은행 명, 계산한 식비를 객체화해서 클라이언트에게 전송한다.
		//(6. 클라이언트는 이걸 받아서 대충 메모장으로 띄워준다.)
	}
	
	//------------------------------------------------------------------------
	
	//학생 - 생활관 호실 조회 - 들어왔을 때
	public void student_checkRoomPage_onEnter()
	{
		//1. 스케쥴을 확인하고 호실 조회 가능한 날짜인지 조회 -> TRUE이면 다음으로, FALSE이면 못들어가게 막음
		//2. 스케쥴 테이블에서 비고(안내사항)를 가져온다.
		//3. 스케쥴 객체를 클라이언트에게 전송한다.
		//(4. 클라이언트에서는 받은 비고(안내사항)을 표시한다)
	}
	
	//학생 - 생활관 호실 조회 - 조회 버튼 클릭 시
	public void student_checkRoomPage_onCheck()
	{
		//1. 받은 요청의 헤더에서 학번을 알아낸다. 
		//2. 신청 테이블에서 해당 학번이 이번 학기에 신청한 내역 중 최종합격여부가 T인 내역 조회 
		//3-1. 내역이 없는 경우 불합격이라고 클라이언트에게 알려준다.(객체 만들던지, String 보내던 알아서 해야될듯. 전용 객체가 있는게 바람직하겠다.)
		//3-2. 내역이 있는 경우 신청 테이블에서 최종합격여부, 납부여부, 식비구분, 생활관, 호실유형(이건 일반실 고정)을 조회한다.
		//4. 배정내역 테이블에서 해당 학번이 배정되있는 호실과 침대번호를 가져온다.
		//5. 3-2와 4를 합쳐 객체화한다. 그리고 이것을 클라이언트에게 전송한다.
		//(6. 클라이언트는 받은 객체를 역직렬화, UI에 표기한다)
	}
	
	//------------------------------------------------------------------------
	
	//학생 - 서류 제출 - 들어왔을 때
	public void student_submitDocumentPage_onEnter()
	{
		//실제 원스톱을 기반으로, 학생이 서류 제출하는건 아무때나 할 수 있다고 하였다.
		//1. 서류 유형을 객체화 배열화하여 클라이언트로 전송한다.
		//(2. 클라이언트는 받은 배열을 역직렬화하여 서류유형 combobox에 표시한다)
	}
	
	//학생 - 서류 제출 - 제출 버튼 클릭 시(파일 업로드)
	public void student_submitDocumentPage_onCheck()
	{
		//1. 서버 컴퓨터 내 저장할 공간에 빈공간이 10MB보다 큰지 확인한다. -> 빈공간이 10MB보다 크면 진행, 작으면 클라이언트에게 안된다고 알려줌.
		//2. 헤더에서 파일 유형이 결핵진단서인지, 서약서인지 확인한다.
		//3. 현재 날짜로부터 학기를 특정한다. (학기는 201901 ~ 201906)
		//4. 어느폴더\학번\학기\학번+결핵진단서or서약서.jpg 와 같은 형식으로 저장된다.
		//	 (학기가 겹치면 덮어씌워진다. 즉, 한학기에 한 파일만 유효함)
		//5. 파일 저장 성공/실패 여부를 클라이언트에게 알려준다.
	}
	
	//------------------------------------------------------------------------
	
	//학생 - 서류 조회 - 들어왔을 때
	public void student_checkDocumentPage_onEnter()
	{
		//1. 서류 유형을 객체화 배열화하여 클라이언트로 전송한다.
		//(2. 클라이언트는 받은 배열을 역직렬화하여 서류유형 combobox에 표시한다)
	}
	
	//학생 - 서류 제출 - 조회 버튼 클릭 시
	public void student_checkDocumentPage_onCheck()
	{
		//1. 받은 요청의 헤더에서 학번, 서류유형을 알아낸다. 
		//2. 서류 테이블에서 해당 학번이 이번 학기에 제출한 내역 중 서류유형이 일치하는 것을 찾는다. -> 있으면 진행, 없으면 없다고 알려줌
		//3. 서류 테이블에서 서류유형, 제출일시, 진단일시, 파일경로를 알아내어 객체화한다.
		//4. 클라이언트에게 전송한다.
	}
	
	//학생 - 서류 제출 - 다운로드 버튼 클릭 시(파일 다운로드)
	public void student_checkDocumentPage_onDownlaod()
	{
		//(1. 클라이언트는 자기 남은 용량이 10MB 이상인지 체크한다)
		//(2. 클라이언트는 해당 파일 경로를 바디에 담아 서버에게 보낸다)
		//3. 서버는 해당 파일 경로로 파일을 찾는다. -> 파일이 있으면 진행, 없으면 없다고 알려줌(이땐 버그라고 봐야할듯)
		//4. 파일을 찾았으면 클라이언트에 업로드한다.
	}
}

//변경 로그
//2019-12-06 v1.00
//	Responser.java 생성하였음 -명근

//2019-12-07 v1.01
//주석 추가 및 오타 수정 -명근

