package com.goodpang.servlet;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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

    private static final int DEFAULT_PAGE_SIZE = 60;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        int categoryNo = parseIntOrDefault(request.getParameter("categoryNo"), 10301);
        Sort sort = parseSortOrDefault(request.getParameter("sort"));
        int page = Math.max(1, parseIntOrDefault(request.getParameter("page"), 1));

        // 보기 개수 60/120 — 2026-09-03 실동작으로 변경(예전엔 "60개 고정" 확정이었는데 사용자 요청으로 정정).
        // 원본처럼 화이트리스트 두 개만 허용 — 그 외 값(잘못된 파라미터 조작 등)은 기본값 60으로
        int listSize = parseIntOrDefault(request.getParameter("listSize"), DEFAULT_PAGE_SIZE);
        if (listSize != 60 && listSize != 120) listSize = DEFAULT_PAGE_SIZE;

        // 가격대 필터 — 안 고르면 전체(0 ~ 최대) 그대로
        int minPrice = Math.max(0, parseIntOrDefault(request.getParameter("minPrice"), 0));
        int maxPrice = parseIntOrDefault(request.getParameter("maxPrice"), Integer.MAX_VALUE);
        if (maxPrice < minPrice) maxPrice = Integer.MAX_VALUE;

        // 평점 필터 — 원본처럼 "N점 이상"(0 = 전체)
        int minRating = Math.max(0, Math.min(5, parseIntOrDefault(request.getParameter("rating"), 0)));

        // 색상 필터 — 실제 쿠팡 원본을 다시 확인해보니(2026-09-01 Playwright 재확인) 색상도 다중선택이고,
        // 선택한 값들은 OR("Black 또는 White"), 다른 필터 그룹과는 AND. 그래서 String 하나가 아니라 List 로 받음
        // (color=Black&color=White 형태).
        // ★ 2026-09-01: 예전엔 여기서 getBytes("ISO-8859-1")로 재해석하는 방어 코드가 있었는데, 실제
        // 다중선택을 배포해서 확인해보니 "선택한 필터"에 한글이 "???"로 깨지고 체크 표시도 안 붙는
        // 버그였음 — 원인은 이 프로젝트가 jakarta.servlet(Tomcat 10 계열)이라 URIEncoding 기본값이 이미
        // UTF-8이라 request.getParameter()가 애초에 정상 한글을 돌려주는데, 거기다 대고 "깨졌을 것"이라며
        // ISO-8859-1로 재해석하면 매핑 안 되는 한글이 전부 '?'로 대체돼버림(Java 인코더 기본 동작).
        // 그래서 재해석 없이 받은 값을 그대로 씀. (이전 세션 진단은 재배포 확인 전의 추측이었음 — 틀렸던 것으로 결론)
        String[] colorParams = request.getParameterValues("color");
        List<String> colors = new ArrayList<>();
        if (colorParams != null) {
            for (String c : colorParams) {
                if (c == null || c.isBlank()) continue;
                colors.add(c);
            }
        }
        // JSP 에서 "이 색상이 선택됐는지" 를 EL 로 바로 물어볼 수 있게 Map 으로도 같이 넘김
        // (JSTL EL 은 List.contains() 를 직접 못 부르고, fn:contains 는 문자열 부분일치라 여기엔 안 맞음)
        Map<String, Boolean> selectedColorMap = new LinkedHashMap<>();
        for (String c : colors) {
            selectedColorMap.put(c, Boolean.TRUE);
        }

        CategoryProductDAO dao = new CategoryProductDAO();

        List<CategoryProductDTO> products =
                dao.findByCategory(categoryNo, sort, minPrice, maxPrice, minRating, colors, page, listSize);
        int totalCount = dao.countByCategory(categoryNo, minPrice, maxPrice, minRating, colors);
        int totalPages = (int) Math.ceil(totalCount / (double) listSize);

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
        request.setAttribute("listSize", listSize);
        request.setAttribute("selectedColors", colors);
        request.setAttribute("selectedColorMap", selectedColorMap);

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
        // 2026-09-05: "필터" 제목 아래 배송 체크박스 줄(topFilterItems)은 여기서 안 내려줌 —
        // 원본을 다시 재보니 평평한 목록이 아니라 "로켓 전체" 밑에 3줄이 들어가는 2단 구조여서,
        // 문자열 배열로는 표현이 안 됨. DB 연동이 없는 장식용 줄이라 category_list.jsp 안에 직접 적어둠

        // 배송예정일 — ProductServlet(상세페이지)과 완전히 같은 계산식 재사용(2026-09-02 추가).
        // 실제 배송정보 컬럼(SHIPPING_FEE_TYPE/DELIVERY_METHOD/LEAD_TIME_DAYS)을 아직 어디서도 안 써서
        // (CLAUDE.md "지금 하는 일" 4번 미해결) 카드마다 다르게는 못 보여줌 — "내일(요일) M/d 도착 예정"을
        // 모든 카드에 똑같이 씀. 실제 컬럼 연동되면 그때 상품별로 갈라줄 것
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String dayOfWeek = tomorrow.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.KOREAN);
        // 2026-09-05 재실측: 원본은 로켓 상품일 때 날짜(M/d) 없이 "내일(목) 도착 보장" 이라고만 씀
        // (날짜가 붙는 건 "9/5(토) 도착 예정" 처럼 로켓이 아닌 상품 쪽). "도착 보장" 글자는 JSP 에 있음
        String deliveryDate = "내일(" + dayOfWeek + ")";
        request.setAttribute("deliveryDate", deliveryDate);

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
        // 2026-09-03: 아래 소재/네크라인/패턴·프린트/제조년도 4개는 원본이 "5개 초과 → +더보기" 로 접기 때문에
        // (coupang.com 실측, 5개까지만 보이고 6번째부터 접힘) 실제로 접히는 걸 눈으로 볼 수 있게
        // 항목 수를 실측값 그대로 채움. 나머지(핏/사용대상 등)는 원본도 5개 이하라 그대로 둠
        groups.put("소재", new String[] {
                "면 100%", "니트", "면혼방", "린넨", "레이온", "폴리에스터/나일론",
                "울/모직", "가죽", "인조가죽 (합성피혁)", "인조퍼", "기타 합성 섬유", "기모", "스판덱스", "아크릴", "캐시미어" });
        groups.put("네크라인", new String[] {
                "라운드넥", "브이넥", "헨리넥 (라운드넥+버튼)", "터틀넥/폴라", "일반 칼라", "버튼다운 칼라", "반집업 칼라" });
        groups.put("사용계절", new String[] { "사계절용", "봄가을용", "여름용", "겨울용" });
        groups.put("소매길이", new String[] { "민소매", "반소매", "7부소매", "긴소매" });
        groups.put("길이", new String[] { "숏/크롭", "기본", "롱" });
        groups.put("패턴/프린트", new String[] {
                "단색", "스트라이프", "도트", "체크/격자", "플라워",
                "밀리터리", "헤링본/기하학", "애니멀", "페이즐리/에스닉", "트로피칼/과일", "레터링" });
        groups.put("출시년도", new String[] { "2023", "2022", "2021", "2020" });
        groups.put("제조년도", new String[] { "2023", "2022", "2021", "2020", "2019", "2018", "2017 이전" });
        groups.put("출시 계절", new String[] { "봄", "여름", "가을", "겨울" });
        groups.put("세탁방법", new String[] { "손세탁권장", "세탁기사용가능", "드라이클리닝", "세탁불가" });
        groups.put("상하의세트 여부", new String[] { "상의", "하의", "상하의세트" });
        groups.put("스타일", new String[] { "캐주얼", "홈웨어", "오피스", "스포티" });
        return groups;
    }
}
