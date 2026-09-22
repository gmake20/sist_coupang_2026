package org.doit.ik.controller;

import org.doit.ik.service.OrderService;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import lombok.RequiredArgsConstructor;
@Component
@RequiredArgsConstructor
public class Test {
	
  private final OrderService orderService;
	@GetMapping("/test_order")
	@ResponseBody
	public String testOrder() {
	    int count = 0;
		try {
			count = orderService.getOrderCount(1, "recent");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    System.out.println("주문 건수 테스트 결과 : " + count);
	    return "Test Success: " + count;
	}
}
