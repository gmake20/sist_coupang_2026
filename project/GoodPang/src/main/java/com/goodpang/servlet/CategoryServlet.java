package com.goodpang.servlet;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.goodpang.dao.CategoryProductDAO;
import com.goodpang.dao.CategoryProductDAO.Sort;
import com.goodpang.dto.CategoryDTO;
import com.goodpang.dto.CategoryProductDTO;

@WebServlet("/category")
public class CategoryServlet extends HttpServlet {

    private static final int PAGE_SIZE = 60;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int categoryNo = parseIntOrDefault(request.getParameter("categoryNo"), 10301);
        Sort sort = parseSortOrDefault(request.getParameter("sort"));
        int page = Math.max(1, parseIntOrDefault(request.getParameter("page"), 1));

        // 가격대 필터 — 안 고르면 전체(0 ~ 최대) 그대로
        int minPrice = Math.max(0, parseIntOrDefault(request.getParameter("minPrice"), 0));
        int maxPrice = parseIntOrDefault(request.getParameter("maxPrice"), Integer.MAX_VALUE);
        if (maxPrice < minPrice) maxPrice = Integer.MAX_VALUE;

        // 평점 필터 — 원본처럼 "N점 이상"(0 = 전체)
        int minRating = Math.max(0, Math.min(5, parseIntOrDefault(request.getParameter("rating"), 0)));

        // 색상 필터 — 하나만 선택(원본도 실측상 클릭 시 전체 페이지 재요청, 다중선택 UI 아님). 안 고르면 null
        String color = request.getParameter("color");
        if (color != null && color.isBlank()) color = null;

        CategoryProductDAO dao = new CategoryProductDAO();

        List<CategoryProductDTO> products =
                dao.findByCategory(categoryNo, sort, minPrice, maxPrice, minRating, color, page, PAGE_SIZE);
        int totalCount = dao.countByCategory(categoryNo, minPrice, maxPrice, minRating, color);
        int totalPages = (int) Math.ceil(totalCount / (double) PAGE_SIZE);

        CategoryDTO[] breadcrumb = dao.findBreadcrumb(categoryNo);
        List<CategoryDTO> siblingCategories = dao.findSiblingCategories(categoryNo);
        List<String> colorOptions = dao.findColorOptionsByCategory(categoryNo);

        request.setAttribute("products", products);
        request.setAttribute("totalCount", totalCount);
        request.setAttribute("totalPages", totalPages);
        request.setAttribute("page", page);
        request.setAttribute("sort", sort.name());
        request.setAttribute("minPrice", minPrice);
        request.setAttribute("maxPrice", maxPrice == Integer.MAX_VALUE ? "" : maxPrice);
        request.setAttribute("rating", minRating);
        request.setAttribute("selectedColor", color);

        request.setAttribute("categoryNo", categoryNo);
        request.setAttribute("breadcrumb", breadcrumb);          // [대분류, 중분류, 소분류]
        request.setAttribute("siblingCategories", siblingCategories);
        request.setAttribute("colorOptions", colorOptions);

        /* 실제 DB에 속성 컬럼 자체가 없는 필터들 — 화면엔 보여주되(원본과 동일 구성) 동작은 안 함(2026-08-30 확정).
         * 2026-08-31: 원본 실측(Playwright browser_evaluate, ref/category/STRUCTURE.md)한 순서 그대로 맞추려고
         * "색상 앞"/"색상 뒤" 두 그룹으로 나눔 — 원본은 카테고리→브랜드→상품상태→색상→핏→...→별점→가격 순서라
         * 색상(실제 동작)이 inert 그룹들 사이에 끼어있음. 별점/가격도 원본은 맨 끝이라 JSP에서 순서 맞춰 넣음. */
        request.setAttribute("beforeColorGroups", buildBeforeColorGroups());
        request.setAttribute("afterColorGroups", buildAfterColorGroups());
        // "필터" 제목 바로 아래, 소제목(h3) 없이 나오는 체크박스 줄 — 2026-08-31 실측(1440px 스크린샷)한
        // 실제 라벨 그대로("로켓럭셔리만 보기" 등은 처음에 잘못 짐작한 것, 이걸로 교체). 로켓 배지 이미지는 없어서 글자만
        request.setAttribute("topFilterItems", new String[] { "로켓", "R.LUX만 보기", "로켓와우만 보기", "로켓직구만 보기", "C.에비뉴", "무료배송" });

        request.getRequestDispatcher("/WEB-INF/views/category_list.jsp").forward(request, response);
    }

    private int parseIntOrDefault(String value, int defaultValue) {
        if (value == null || value.isBlank()) return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    private Sort parseSortOrDefault(String value) {
        if (value == null) return Sort.LATEST;
        try {
            return Sort.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            return Sort.LATEST;
        }
    }

    /*
     * JSP EL 은 리스트 리터럴(${['a','b']}) 을 못 써서, 화면에만 쓰이는 정적 필터 목록은
     * 서블릿에서 Map 으로 만들어 넘기고 JSP 는 <c:forEach> 로 그냥 돌기만 함.
     * (2026-08-31 STRUCTURE.md 3장 표를 그대로 옮김 — 카테고리/평점/가격/색상은 이미 위에서 따로 처리하니 제외)
     */
    // 원본 실측 순서: 카테고리 → 브랜드 → 상품상태 → (색상, JSP에서 별도 처리) → 핏 → ...
    private Map<String, String[]> buildBeforeColorGroups() {
        Map<String, String[]> groups = new LinkedHashMap<>();
        groups.put("브랜드", new String[] { "나이키", "노스페이스", "뉴발란스", "지프" });
        groups.put("상품 상태", new String[] { "새 상품", "박스 훼손", "반품" });
        return groups;
    }

    // 원본 실측 순서: (색상) → 핏 → 사용대상 → 소재 → 네크라인 → 사용계절 → 소매길이 → 길이 →
    // 패턴/프린트 → 출시년도 → 제조년도 → 출시 계절 → 세탁방법 → 상하의세트 여부 → 스타일 → (별점/가격은 JSP에서 별도)
    private Map<String, String[]> buildAfterColorGroups() {
        Map<String, String[]> groups = new LinkedHashMap<>();
        groups.put("핏", new String[] { "슬림", "일반", "오버사이즈" });
        groups.put("사용대상", new String[] { "남성용", "여성용", "남녀공용", "아동·유아용" });
        groups.put("소재", new String[] { "면 100%", "니트", "면혼방", "린넨", "레이온", "폴리에스터/나일론" });
        groups.put("네크라인", new String[] { "라운드넥", "브이넥", "헨리넥", "터틀넥/폴라" });
        groups.put("사용계절", new String[] { "사계절용", "봄가을용", "여름용", "겨울용" });
        groups.put("소매길이", new String[] { "민소매", "반소매", "7부소매", "긴소매" });
        groups.put("길이", new String[] { "숏/크롭", "기본", "롱" });
        groups.put("패턴/프린트", new String[] { "단색", "스트라이프", "도트", "플라워" });
        groups.put("출시년도", new String[] { "2023", "2022", "2021", "2020" });
        groups.put("제조년도", new String[] { "2023", "2022", "2021", "2020" });
        groups.put("출시 계절", new String[] { "봄", "여름", "가을", "겨울" });
        groups.put("세탁방법", new String[] { "손세탁권장", "세탁기사용가능", "드라이클리닝", "세탁불가" });
        groups.put("상하의세트 여부", new String[] { "상의", "하의", "상하의세트" });
        groups.put("스타일", new String[] { "캐주얼", "홈웨어", "오피스", "스포티" });
        return groups;
    }
}
