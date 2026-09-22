package org.doit.goodpang;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import lombok.extern.log4j.Log4j;

@Controller
@Log4j
public class HomeController {
	/*
	 * @Autowired private JdbcTemplate jdbcTemplate;
	 */
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public String home(Locale locale, Model model) {
		System.out.println("🤣 HomeController.home()....");
		
		/*
		 * Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM PRODUCT",
		 * Integer.class); System.out.println("🔥 PRODUCT count = " + count);
		 */
		
		/*
		 * List<String> names = jdbcTemplate.queryForList(
		 * "SELECT PRODUCT_NAME FROM PRODUCT WHERE ROWNUM <= 5", String.class);
		 * System.out.println("🔥 상품 5개 = " + names);
		 */
		
		return "home.index";
	}
	
}
