package com.goodpang.servlet;

import java.io.IOException;

import com.goodpang.dao.AddressDAO;
import com.goodpang.dto.AddressDTO;
import com.goodpang.dto.MemberDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/address/edit")
public class AddressEditServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    // 수정 페이지
    @Override
    protected void doGet(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("loginMember") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        String addressNoParam =
                request.getParameter("addressNo");

        if (addressNoParam == null
                || addressNoParam.isBlank()) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/address/list"
            );
            return;
        }

        try {

            int addressNo =
                    Integer.parseInt(addressNoParam);

            MemberDTO loginMember =
                    (MemberDTO) session
                            .getAttribute("loginMember");

            int memberNo =
                    loginMember.getMemberNo();

            AddressDAO dao = new AddressDAO();

            AddressDTO address =
                    dao.editGetAddress(
                            addressNo,
                            memberNo
                    );

            if (address == null) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/address/list"
                );
                return;
            }

            request.setAttribute(
                    "address",
                    address
            );

            request.getRequestDispatcher(
                    "/address_edit.jsp"
            ).forward(request, response);

        } catch (NumberFormatException e) {

            response.sendRedirect(
                    request.getContextPath()
                            + "/address/list"
            );

        } catch (Exception e) {

            throw new ServletException(
                    "배송지 조회 중 오류가 발생했습니다.",
                    e
            );
        }
    }


    // 실제 수정 처리
    @Override
    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        HttpSession session = request.getSession(false);

        if (session == null
                || session.getAttribute("loginMember") == null) {

            response.sendRedirect(
                    request.getContextPath() + "/login"
            );
            return;
        }

        try {

            MemberDTO loginMember =
                    (MemberDTO) session
                            .getAttribute("loginMember");

            int memberNo =
                    loginMember.getMemberNo();

            int addressNo =
                    Integer.parseInt(
                            request.getParameter("addressNo")
                    );

            String addressDefault =
                    request.getParameter("addressDefault");

            if (addressDefault == null) {
                addressDefault = "N";
            }

            AddressDTO dto = new AddressDTO();

            dto.setAddressNo(addressNo);
            dto.setMemberNo(memberNo);

            dto.setReceiverName(
                    request.getParameter("receiverName")
            );

            dto.setTel(
                    request.getParameter("tel")
            );

            dto.setZipcode(
                    request.getParameter("zipcode")
            );

            dto.setAddress(
                    request.getParameter("address")
            );

            dto.setDetailAddress(
                    request.getParameter("detailAddress")
            );

            dto.setRequestMsg(
                    request.getParameter("requestMsg")
            );

            dto.setAddressDefault(
                    addressDefault
            );

            AddressDAO dao = new AddressDAO();

            int result =
                    dao.updateAddress(dto);

            if (result > 0) {

                response.sendRedirect(
                        request.getContextPath()
                                + "/address/list"
                );

            } else {

                request.setAttribute(
                        "error",
                        "배송지 수정에 실패했습니다."
                );

                request.setAttribute(
                        "address",
                        dto
                );

                request.getRequestDispatcher(
                        "/address_edit.jsp"
                ).forward(request, response);
            }

        } catch (Exception e) {

            e.printStackTrace();

            throw new ServletException(
                    "배송지 수정 중 오류가 발생했습니다.",
                    e
            );
        }
    }
}