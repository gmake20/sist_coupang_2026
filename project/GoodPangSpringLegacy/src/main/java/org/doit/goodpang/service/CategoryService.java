package org.doit.goodpang.service;


import org.springframework.stereotype.Service;

/**
 * 카테고리 목록페이지의 업무 로직 담당 (Service 계층).
 *
 * 원래 CategoryServlet.doGet() 안에 있던 DB 조회 + 계산 + 화면용 정적 목록을 여기로 옮김. Handler 는
 * 파라미터만 읽어서 이 클래스를 부르고 request 에 담기만 함.
 *
 * ※ CategoryServlet 은 아직 원본 그대로 살아있음. 이 클래스는 그 로직을 옮겨 담은 것.
 */
@Service
public class CategoryService {
	
}
