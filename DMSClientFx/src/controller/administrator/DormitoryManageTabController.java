package controller.administrator;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import application.IOHandler;
import application.Responser;
import controller.InnerPageController;
import enums.Bool;
import enums.Gender;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import models.Dormitory;
import models.Tuple;
import tableViewModel.DormitoryViewModel;

//생활관 조회 및 관리
public class DormitoryManageTabController extends InnerPageController 
{
	@FXML
    private Button check_button;

    @FXML
    private TableView<DormitoryViewModel> check_dormitory_tableview;
    
    @FXML
    private TableColumn<DormitoryViewModel, String> check_dormitory_column_dormName;
    
    @FXML
    private TableColumn<DormitoryViewModel, String> check_dormitory_column_gender;

    @FXML
    private TableColumn<DormitoryViewModel, String> check_dormitory_column_semester;

    @FXML
    private TableColumn<DormitoryViewModel, String> check_dormitory_column_capacity;

    @FXML
    private TableColumn<DormitoryViewModel, String> check_dormitory_column_mealDuty;

    @FXML
    private TableColumn<DormitoryViewModel, String> check_dormitory_column_meal5;

    @FXML
    private TableColumn<DormitoryViewModel, String> check_dormitory_column_meal7;

    @FXML
    private TableColumn<DormitoryViewModel, String> check_dormitory_column_boardingFee;

    @FXML
    private Button delete_button;

    @FXML
    private TextField delete_dormName_textfield;

    @FXML
    private TextField delete_semester_textfield;

    @FXML
    private Button insert_button;

    @FXML
    private TextField insert_dormName_textfield;

    @FXML
    private TextField insert_semester_textfield;

    @FXML
    private TextField insert_capacity_textfield;

    @FXML
    private TextField insert_mealCost5_textfield;

    @FXML
    private TextField insert_mealCost7_textfield;

    @FXML
    private TextField insert_boradingFees_textfield;

    @FXML
    private TextField insert_gender_textfield;

    @FXML
    private ComboBox<String> insert_mealDuty_combobox;
    
	@Override
	public void initialize(URL location, ResourceBundle resources)
	{
		System.out.println("생활관 조회 및 관리 새로고침됨");
		
		//네트워크 통신 후 식사의무칸 가져와야하나? 어짜피 모든 기숙사엔 5일식 7일식 식사안함밖에없으니까.
		insert_mealDuty_combobox.getItems().addAll(comboboxItem_boolean);
	}
	
	//---------------------이벤트---------------------
	
	@FXML
    void on_check_button_actioned(ActionEvent event) 
    {
		System.out.println("생활관 조회 및 관리 : 조회 클릭됨");
		checkDormitories();
    }

    @FXML
    void on_delete_button_actioned(ActionEvent event) 
    {
    	System.out.println("생활관 조회 및 관리 : 삭제 클릭됨");
    	deleteDormitory();
    }

    @FXML
    void on_insert_button_actioned(ActionEvent event) 
    {
    	System.out.println("생활관 조회 및 관리 : 등록 클릭됨");
    	insertDormitory();
    }
	
	//---------------------로직---------------------

    private void checkDormitories()
    {
    	//서버에서 생활관 목록 쫙 조회함.
    	ArrayList<Dormitory> resultList = Responser.admin_dormitoryManagePage_onCheck();
    	
    	if(resultList == null)
    	{
    		IOHandler.getInstance().showAlert("서버에 연결할 수 없습니다.");
        	return;
    	}
    	
    	if(resultList != null)
    	{
    		ObservableList<DormitoryViewModel> dormitoryViewModels = FXCollections.observableArrayList();
    		
    		for(Dormitory dorm : resultList)
        	{
    			dormitoryViewModels.add(dormitoryToViewModel(dorm));
        	}
    		
    		setTableView(dormitoryViewModels);
    	}
    	
    }
    
    //스케쥴 객체를 뷰모델로 바꾸는 메소드
    private DormitoryViewModel dormitoryToViewModel(Dormitory dormitory)
    {
    	return new DormitoryViewModel(dormitory.dormitoryName, dormitory.gender, dormitory.semesterCode, dormitory.capacity, 
    			dormitory.isMealDuty, dormitory.mealCost5, dormitory.mealCost7, dormitory.boardingFees);
    			
    }
    
    private void setTableView(ObservableList<DormitoryViewModel> dormitoryList)
    {
    	check_dormitory_column_dormName.setCellValueFactory(cellData -> cellData.getValue().dormNameProperty());
    	
    	check_dormitory_column_semester.setCellValueFactory(cellData -> cellData.getValue().semesterProperty());
    	check_dormitory_column_gender.setCellValueFactory(cellData -> cellData.getValue().genderProperty());
    	check_dormitory_column_capacity.setCellValueFactory(cellData -> cellData.getValue().capacityProperty());
    	check_dormitory_column_mealDuty.setCellValueFactory(cellData -> cellData.getValue().mealDutyProperty());
    	check_dormitory_column_meal5.setCellValueFactory(cellData -> cellData.getValue().meal5Property());
    	check_dormitory_column_meal7.setCellValueFactory(cellData -> cellData.getValue().meal7Property());
    	check_dormitory_column_boardingFee.setCellValueFactory(cellData -> cellData.getValue().boardingProperty());
    	check_dormitory_tableview.setItems(dormitoryList);
    }
    
    //-----------------------------------------------------------------
    
    private void deleteDormitory()
    {
    	String dormName = delete_dormName_textfield.getText();
    	String semester = delete_semester_textfield.getText();
    	
    	if(dormName == null || dormName.isEmpty())
    	{
    		//생활관명 비어있음
    		IOHandler.getInstance().showAlert("생활관명이 비어있습니다.");
    		return;
    	}
    	else if(semester == null || semester.isEmpty())
    	{
    		//학기가 없음
    		IOHandler.getInstance().showAlert("학기가 비어있습니다.");
    		return;
    	}
    	
    	//서버에 삭제 쿼리 요청 후 성공/실패여부 메시지로 알려주자.
    	Tuple<Bool, String> resultTuple = Responser.admin_dormitoryManagePage_onDelete(dormName, semester);
    	
    	if(resultTuple == null)
    	{
    		IOHandler.getInstance().showAlert("서버에 연결할 수 없습니다.");
        	return;
    	}
    	
    	if(resultTuple != null)
    	{
    		if(resultTuple.obj1 == Bool.TRUE)
    		{
    			clearDeleteInfo();
    			checkDormitories();
    		}
    		IOHandler.getInstance().showAlert(resultTuple.obj2);
    	}
    	
    }
    
    private void clearDeleteInfo()
    {
		//선택한 항목들 클리어
		delete_dormName_textfield.setText(null);
		delete_semester_textfield.setText(null);
    }
    
    //-----------------------------------------------------------------
    
    private void insertDormitory()
    {
    	String dormNameStr = insert_dormName_textfield.getText();
    	String genderStr = insert_gender_textfield.getText();
    	String semesterStr = insert_semester_textfield.getText();
    	String capacityStr = insert_capacity_textfield.getText();
    	String mealDutyStr = insert_mealDuty_combobox.getSelectionModel().getSelectedItem();
    	String mealCost5Str = insert_mealCost5_textfield.getText();
    	String mealCost7Str = insert_mealCost7_textfield.getText();
    	String boardingFeesStr = insert_boradingFees_textfield.getText();
    	
    	if(dormNameStr == null || dormNameStr.isEmpty())
    	{
    		//생활관명 비어있음
    		IOHandler.getInstance().showAlert("생활관명이 비어있습니다.");
    		return;
    	}
    	else if(genderStr == null || genderStr.isEmpty())
    	{
    		//성별이 없음
    		IOHandler.getInstance().showAlert("성별이 비어있습니다.");
    		return;
    	}
    	else if(semesterStr == null || semesterStr.isEmpty())
    	{
    		//학기가 없음
    		IOHandler.getInstance().showAlert("학기가 비어있습니다.");
    		return;
    	}
    	else if(capacityStr == null || capacityStr.isEmpty())
    	{
    		//수용인원이 없음
    		IOHandler.getInstance().showAlert("수용인원이 비어있습니다.");
    		return;
    	}
    	else if(mealDutyStr == null || mealDutyStr.isEmpty())
    	{
    		//식사의무가 비어있음
    		IOHandler.getInstance().showAlert("식사의무가 비어있습니다.");
    		return;
    	}
    	else if(mealCost5Str == null || mealCost5Str.isEmpty())
    	{
    		//5일식 식비가 없음
    		IOHandler.getInstance().showAlert("5일식 식비가 비어있습니다.");
    		return;
    	}
    	else if(mealCost7Str == null || mealCost7Str.isEmpty())
    	{
    		//7일식 식비가 없음
    		IOHandler.getInstance().showAlert("7일식 식비가 비어있습니다.");
    		return;
    	}
    	else if(boardingFeesStr == null || boardingFeesStr.isEmpty())
    	{
    		//기숙사비가 없음
    		IOHandler.getInstance().showAlert("기숙사비가 비어있습니다.");
    		return;
    	}
    	
    	//서버에 삭제 쿼리 요청 후 성공/실패여부 메시지로 알려주자.
    	Gender gender = genderStr.equals("M") ? Gender.Male : Gender.Female;
    	int semester = Integer.parseInt(semesterStr);
    	int capacity = Integer.parseInt(capacityStr);
    	Bool isMealDuty = mealDutyStr.equals("T") ? Bool.TRUE : Bool.FALSE;
    	int mealCost5 = Integer.parseInt(mealCost5Str);
    	int mealCost7 = Integer.parseInt(mealCost7Str);
    	int boardingFees = Integer.parseInt(boardingFeesStr);
    	
    	Dormitory data = new Dormitory(dormNameStr, gender, semester, capacity, isMealDuty, mealCost5, mealCost7, boardingFees);
    	Tuple<Bool, String> resultTuple = Responser.admin_dormitoryManagePage_onInsert(data);
    	
    	if(resultTuple == null)
    	{
    		IOHandler.getInstance().showAlert("서버에 연결할 수 없습니다.");
        	return;
    	}
    	
    	if(resultTuple != null)
    	{
    		if(resultTuple.obj1 == Bool.TRUE)
    		{
    			clearInsertInfo();
    			checkDormitories();
    		}
    		IOHandler.getInstance().showAlert(resultTuple.obj2);
    	}
    	
    }
    
    private void clearInsertInfo()
    {
    	//선택한 항목들 클리어
		insert_dormName_textfield.setText(null);
		insert_gender_textfield.setText(null);
		insert_semester_textfield.setText(null);
		insert_capacity_textfield.setText(null);
		insert_mealDuty_combobox.getSelectionModel().select(-1);
		insert_mealCost5_textfield.setText(null);
		insert_mealCost7_textfield.setText(null);
		insert_boradingFees_textfield.setText(null);
    }
}
