package org.sp.mvc.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//웹어플리케이션의 모든 요청을 이 클래스가 받을 예정 
//만일 모든 요청마다 1:1 대응하는 서블릿을 매핑하게 될 경우 
//요청의 수가 늘어남에 따라 유지보수성이 떨어진다...
public class DispatcherServlet extends HttpServlet{
	/*모든 컨트롤러의 5대 업무 처리 프로세스
	 * 1) 요청을 받는다 (형님)
	 * 2) 요청을 분석한다(모든 요청을 혼자 감당하고 있으므로) 형님
	 * 3) 알맞는 로직 객체에 일 시킨다(하위 컨트롤러가..)
	 * 4) View인 jsp로 가져갈 데이터가 있다면 결과저장(request에..)
	 *      포워딩이 요구됨... 
	 * 5) 결과 페이지로 전환, 보여주기
	 * */ 
	
	FileReader reader;
	JSONParser jsonParser;
	JSONObject obj;//파싱한 결과 객체
	
	//서블릿이 톰켓에 의해 인스턴스화 된 직후, 서블릿으로서 알아야 할 정보를 
	//init을 통해 전달받을 수 있다..(from tomcat...)
	public void init(ServletConfig config) throws ServletException {
		//스트림 생성  d:~~~ , pack
		URL url=this.getClass().getResource("/org/sp/mvc/controller/mapping.js");
		try {
			reader = new FileReader(new File(url.toURI()));
			
			
			jsonParser = new JSONParser();
			
			//파싱 후 객체를 반환받는다..따라서 문자열 이었던 json은 이 시점부터 
			//JSONObjec 객체가 된다..
			obj=(JSONObject)jsonParser.parse(reader);//해석(파싱)
			
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (URISyntaxException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
	}
	
	
	//post방식 요청만 처리..
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response);
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doRequest(request, response);
	}
	
	protected void doRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		//모든 하위 컨트롤러가 이 request 객체를 전달받기 때문에, 여기서 한번만
		//파라미터 처리를 해주면 효율적이다..
		request.setCharacterEncoding("utf-8");
		
		
		//2단계) 요청 분석하기 
		String uri=request.getRequestURI();
		out.print(uri);
		
		//uri 을 분석하여, 어떤 하위컨트롤러에게 요청을 전달할지를 결정
		//JSON 파싱한 정보를 이용하여 하위 컨트롤러 메모리에 올리고 동작시키기
		
		/*
		if(uri.equals("/blood.do")) { //혈액형을 원하면..
			BloodController controller = new BloodController();
			controller.work(request, response);//하위 컨트롤러 동작 시작
		}else if(uri.equals("/movie.do")) {
			MovieController controller = new MovieController();
			controller.work(request, response);//하위 컨트롤러 동작 시작
		}
		*/
		
		
		JSONObject json=(JSONObject)obj.get("controller");
		JSONObject viewJson=(JSONObject)obj.get("view");
		
		//하위 컨트롤러의 패키지+클래스 이름이 반환
		String subName=(String)json.get(uri);
		
		out.print(uri+"요청을 처리할 서브컨트롤러는 "+subName);
		
		//매개변수로 전달한 클래스명을 이용하여, static 영역으로 Load
		try {
			Class subController=Class.forName(subName); //static 동적로드
			Controller controller=(Controller)subController.getConstructor().newInstance();
			controller.execute(request, response);
			
			//위의 msg 는 여기서도  out.print() 할 수는 있지만, 하는 순간부터 
			//컨트롤러 + View 까지 담당하게 됨...
			//5단계) 결과 보여주기..
			String viewKey=controller.getViewKey();
			String viewPage = (String)viewJson.get(viewKey); 
					
			RequestDispatcher dis=request.getRequestDispatcher(viewPage);
			dis.forward(request, response);
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} 
		
	}
	
	//서블릿이 소멸될때,호출되는 생명주기 메서드.. 주로 닫을 자원이 있을때 유용
	public void destroy() {
		if(reader!=null) {
			try {
				reader.close();
			} catch (IOException e) {
 				e.printStackTrace();
			}
		}
	}
}













