package org.sp.mvc.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.sp.mvc.domain.Board;
import org.sp.mvc.model.BoardDAO;

//글쓰기 요청을 처리하는 하위 컨트롤러 
public class RegistController implements Controller{
	BoardDAO boardDAO=new BoardDAO();
	
	//3단계: 일 시킨다 
	public void execute(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String title=request.getParameter("title");
		String writer=request.getParameter("writer");
		String content=request.getParameter("content");
		
		//dto에 담기
		Board board = new Board();
		board.setTitle(title);
		board.setWriter(writer);
		board.setContent(content);
		
		//4단계: 그 결과 담기는, jsp로 가져갈 데이터가 있을때만 수행한다..
		//DML의 경우에는 가져갈 것이 없으므로 생략...
		boardDAO.insert(board);	
	}

	@Override
	public String getViewKey() {
		return "board/registView";
	}
	
}
