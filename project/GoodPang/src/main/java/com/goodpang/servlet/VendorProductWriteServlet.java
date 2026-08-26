package com.goodpang.servlet;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.util.UUID;

import com.goodpang.dao.ProductWriteDAO;
import com.goodpang.dto.ProductOptionWriteDTO;
import com.goodpang.dto.ProductWriteDTO;
import com.goodpang.dto.SellerDTO;
import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

/**
 * 판매자 상품 등록 화면(vendor-product-write.jsp) GET 진입 + POST 등록 처리.
 */
@WebServlet("/vendor/product/write")
@MultipartConfig(maxFileSize = 10 * 1024 * 1024, maxRequestSize = 150 * 1024 * 1024)
public class VendorProductWriteServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	private static final Gson gson = new Gson();
	private final ProductWriteDAO productWriteDAO = new ProductWriteDAO();

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession(false);

		if (session == null || session.getAttribute("loginSeller") == null) {
			response.sendRedirect(request.getContextPath() + "/vendor/login");
			return;
		}

		request.getRequestDispatcher("/WEB-INF/views/vendor-product-write.jsp").forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		request.setCharacterEncoding("UTF-8");
		response.setContentType("application/json;charset=UTF-8");
		response.setCharacterEncoding("UTF-8");

		HttpSession session = request.getSession(false);
		SellerDTO loginSeller = (session != null) ? (SellerDTO) session.getAttribute("loginSeller") : null;

		if (loginSeller == null) {
			writeJson(response, 401, new Result(false, "로그인이 필요합니다.", 0));
			return;
		}

		try {
			ProductWriteDTO dto = buildProductWriteDTO(request, loginSeller);

			if (dto.getProductName() == null || dto.getProductName().isBlank()) {
				writeJson(response, 400, new Result(false, "노출상품명을 입력해주세요.", 0));
				return;
			}
			if (dto.getOptions().isEmpty()) {
				writeJson(response, 400, new Result(false, "옵션을 최소 1개 이상 추가해주세요.", 0));
				return;
			}

			int productNo = productWriteDAO.insertProduct(dto);
			writeJson(response, 200, new Result(true, null, productNo));

		} catch (NumberFormatException e) {
			e.printStackTrace();
			writeJson(response, 400, new Result(false, "입력값을 다시 확인해주세요.", 0));
		} catch (Exception e) {
			e.printStackTrace();
			writeJson(response, 500, new Result(false, "상품 등록 중 오류가 발생했습니다.", 0));
		}
	}

	private ProductWriteDTO buildProductWriteDTO(HttpServletRequest request, SellerDTO loginSeller) throws IOException, ServletException {

		ProductWriteDTO dto = new ProductWriteDTO();

		dto.setSellerNo(loginSeller.getSellerNo());
		dto.setSubCategoryNo(Integer.parseInt(request.getParameter("categoryNo")));

		dto.setSaleMethod("판매자배송");

		dto.setBrandName(blankToNull(request.getParameter("brandName")));
		dto.setNoBrandYn(request.getParameter("noBrandYn"));
		dto.setProductName(request.getParameter("displayName"));
		dto.setInternalName(blankToNull(request.getParameter("internalName")));

		dto.setManufacturer(blankToNull(request.getParameter("manufacturer")));
		dto.setCompositionType(request.getParameter("compositionType"));
		dto.setCertificationType(request.getParameter("certificationType"));
		dto.setParallelImportYn(request.getParameter("parallelImportYn"));
		dto.setMinorPurchaseYn(request.getParameter("minorPurchaseYn"));
		dto.setMaxPurchaseYn(request.getParameter("maxPurchaseYn"));
		dto.setSalePeriodYn(request.getParameter("salePeriodYn"));
		dto.setVatType(request.getParameter("vatType"));

		dto.setDetailType(request.getParameter("detailType"));

		// 출고지/반품지 주소록 기능이 아직 없어서, 판매자 입점 시 등록한 사업장 주소를 그대로 사용
		dto.setShippingZipcode(loginSeller.getZipcode());
		dto.setShippingAddress(loginSeller.getBusinessAddress());
		dto.setShippingDetailAddress(loginSeller.getBusinessDetailAddress());
		dto.setReturnZipcode(loginSeller.getZipcode());
		dto.setReturnAddress(loginSeller.getBusinessAddress());
		dto.setReturnDetailAddress(loginSeller.getBusinessDetailAddress());

		dto.setJejuShippingYn(request.getParameter("jejuShippingYn"));
		dto.setDeliveryServiceCode(request.getParameter("courier"));
		dto.setDeliveryMethod(request.getParameter("deliveryMethod"));
		dto.setBundleShippingYn(request.getParameter("bundleShippingYn"));
		dto.setShippingFeeType(request.getParameter("shippingFeeType"));

		dto.setLeadTimeInputType(request.getParameter("leadTimeInputType"));
		dto.setLeadTimeDays(parseIntOrNull(request.getParameter("leadTimeDays")));
		dto.setSameDayShipYn(request.getParameter("sameDayShipYn"));
		dto.setSameDayCutoffTime(blankToNull(request.getParameter("cutoffTime")));

		dto.setInitialShippingFee(parseIntOrZero(request.getParameter("initialShippingFee")));
		dto.setReturnShippingFee(parseIntOrZero(request.getParameter("returnShippingFee")));

		dto.setSaleStatus("승인 대기");

		int optionCount = parseIntOrZero(request.getParameter("optionCount"));
		for (int i = 0; i < optionCount; i++) {
			dto.getOptions().add(buildOptionDTO(request, i));
		}

		return dto;
	}

	private ProductOptionWriteDTO buildOptionDTO(HttpServletRequest request, int index) throws IOException, ServletException {

		ProductOptionWriteDTO option = new ProductOptionWriteDTO();

		option.setOption1Type(blankToNull(request.getParameter("option_" + index + "_option1Type")));
		option.setOption1Value(blankToNull(request.getParameter("option_" + index + "_option1Value")));
		option.setOption2Type(blankToNull(request.getParameter("option_" + index + "_option2Type")));
		option.setOption2Value(blankToNull(request.getParameter("option_" + index + "_option2Value")));
		option.setOption3Type(blankToNull(request.getParameter("option_" + index + "_option3Type")));
		option.setOption3Value(blankToNull(request.getParameter("option_" + index + "_option3Value")));
		option.setNormalPrice(parseIntOrNull(request.getParameter("option_" + index + "_normalPrice")));
		option.setSalePrice(parseIntOrZero(request.getParameter("option_" + index + "_salePrice")));
		option.setAutoPriceAdjustYn(request.getParameter("option_" + index + "_autoPriceAdjustYn"));
		option.setQuantity(parseIntOrZero(request.getParameter("option_" + index + "_quantity")));
		option.setSellerProductCode(blankToNull(request.getParameter("option_" + index + "_sellerProductCode")));
		option.setModelNo(blankToNull(request.getParameter("option_" + index + "_modelNo")));
		option.setBarcode(blankToNull(request.getParameter("option_" + index + "_barcode")));

		option.setMainImageUrl(saveUploadedImage(request, "option_" + index + "_mainImage"));

		int extraCount = parseIntOrZero(request.getParameter("option_" + index + "_extraImageCount"));
		for (int j = 0; j < extraCount; j++) {
			String extraUrl = saveUploadedImage(request, "option_" + index + "_extraImage_" + j);
			if (extraUrl != null) {
				option.getExtraImageUrls().add(extraUrl);
			}
		}

		return option;
	}

	private String saveUploadedImage(HttpServletRequest request, String partName) throws IOException, ServletException {

		Part part = request.getPart(partName);

		if (part == null || part.getSize() == 0) {
			return null;
		}

		String submittedFileName = part.getSubmittedFileName();
		String originalName = (submittedFileName != null) ? Path.of(submittedFileName).getFileName().toString() : "";

		String ext = "";
		int dotIndex = originalName.lastIndexOf('.');
		if (dotIndex >= 0) {
			ext = originalName.substring(dotIndex);
		}

		String savedName = UUID.randomUUID() + ext;

		String uploadDir = getServletContext().getRealPath("/upload");
		File uploadDirFile = new File(uploadDir);

		if (!uploadDirFile.exists()) {
			uploadDirFile.mkdirs();
		}

		part.write(uploadDir + File.separator + savedName);

		return "upload/" + savedName;
	}

	private void writeJson(HttpServletResponse response, int status, Result result) throws IOException {
		response.setStatus(status);
		try (PrintWriter out = response.getWriter()) {
			out.print(gson.toJson(result));
		}
	}

	private String blankToNull(String value) {
		return (value == null || value.isBlank()) ? null : value;
	}

	private int parseIntOrZero(String value) {
		if (value == null || value.isBlank()) return 0;
		return Integer.parseInt(value.trim());
	}

	private Integer parseIntOrNull(String value) {
		if (value == null || value.isBlank()) return null;
		return Integer.parseInt(value.trim());
	}

	private static class Result {
		private final boolean success;
		private final String message;
		private final int productNo;

		private Result(boolean success, String message, int productNo) {
			this.success = success;
			this.message = message;
			this.productNo = productNo;
		}
	}

}
