package com.goodpang.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.goodpang.dto.CategoryDTO;
import com.goodpang.util.ConnectionProvider;

@WebServlet("/category/getinfo")
public class CategoryInfo extends HttpServlet {
	private static final long serialVersionUID = 1L;

    public CategoryInfo() {
        super();

    }

	private static final Gson gson = new Gson();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		String sql = """
				SELECT category_no,
				       category_name,
				       parent_category_no,
				       category_level
				FROM CATEGORY
				START WITH parent_category_no IS NULL
				CONNECT BY PRIOR category_no = parent_category_no
				       AND LEVEL <= 3
				ORDER SIBLINGS BY category_no
				""";

		List<CategoryDTO> categoryList = new ArrayList<>();
		boolean hasError = false;

		try (
			Connection conn = ConnectionProvider.getConnection();
			PreparedStatement pstmt = conn.prepareStatement(sql);
			ResultSet rs = pstmt.executeQuery()
		) {
			while (rs.next()) {
				long categoryNo = rs.getLong("category_no");
				String categoryName = rs.getString("category_name");
				long parentCategoryNo = rs.getLong("parent_category_no");
				boolean parentIsNull = rs.wasNull();
				int categoryLevel = rs.getInt("category_level");

				categoryList.add(new CategoryDTO(
					categoryNo,
					categoryName,
					parentIsNull ? null : parentCategoryNo,
					categoryLevel
				));
			}
		} catch (Exception e) {
			e.printStackTrace();
			hasError = true;
		}

		String ctype = request.getParameter("ctype");

		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		if (hasError) {
			response.setStatus(500);
		}

		try (PrintWriter out = response.getWriter()) {
			if (hasError) {
				out.print(gson.toJson(new ErrorResponse("카테고리 조회 중 오류가 발생했습니다.")));
			} else if ("main".equals(ctype)) {
				gson.toJson(groupByLevel(categoryList), out);
			} else {
				gson.toJson(categoryList, out);
			}
		}
	}

	/** categoryLevel(1=대분류, 2=중분류, 3=소분류)별로 묶어서 "1", "2", "3" 키를 가진 맵으로 정리 */
	private Map<Integer, List<CategoryDTO>> groupByLevel(List<CategoryDTO> categoryList) {
		Map<Integer, List<CategoryDTO>> groupedByLevel = new LinkedHashMap<>();

		for (CategoryDTO category : categoryList) {
			groupedByLevel
				.computeIfAbsent(category.getCategoryLevel(), level -> new ArrayList<>())
				.add(category);
		}

		return groupedByLevel;
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private static class ErrorResponse {
		private final String error;

		private ErrorResponse(String error) {
			this.error = error;
		}
	}

}
