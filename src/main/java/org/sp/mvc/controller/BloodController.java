package org.sp.mvc.controller;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sp.mvc.model.blood.BloodManager;

//혈액형 요청을 전담하여 처리하는 하위 컨트롤러 
public class BloodController implements Controller{
	BloodManager manager;
	
	//일 시키기 (3단계)
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
		//파라미터 꺼내기 
		String blood=request.getParameter("blood");
		
		manager=new BloodManager();
		String msg=manager.getAdvice(blood);
		
		System.out.println("혈액형 판단 결과 "+msg);
		
		//4단계)  결과페이지로 가져갈 데이터 저장 
		request.setAttribute("msg", msg);
		
	}
	
	//DispatcherServlet 클래스가 어떤  key값으로 JSON 을 검색해야 하는지
	//반환해주는 메서드 
	@Override
	public String getViewKey() {
		return "bloodView";
	}
}







