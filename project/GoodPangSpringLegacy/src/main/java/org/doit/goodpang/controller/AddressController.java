package org.doit.goodpang.controller;

import java.util.List;

import org.doit.goodpang.domain.AddressDTO;
import org.doit.goodpang.domain.security.CustomUser;
import org.doit.goodpang.service.AddressService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j;

@Controller
@Log4j
@RequiredArgsConstructor
@RequestMapping("/address")
public class AddressController {

    private final AddressService addressService;


    // 배송지 추가 화면
    @GetMapping("/add")
    public String addForm(
            @RequestParam(
                    value = "checkoutNo",
                    required = false
            ) Integer checkoutNo,

            @RequestParam(
                    value = "from",
                    required = false
            ) String from,

            Model model) {

        log.info("AddressAddController.addForm()");

        model.addAttribute("checkoutNo", checkoutNo);
        model.addAttribute("from", from);

        return "address_add";
    }


    // 배송지 등록
    @PostMapping("/add")
    public String add(
            Authentication authentication,

            @RequestParam("receiverName")
            String receiverName,

            @RequestParam("tel")
            String tel,

            @RequestParam("zipcode")
            String zipcode,

            @RequestParam("address")
            String address,

            @RequestParam("detailAddress")
            String detailAddress,

            @RequestParam(
                    value = "requestMsg",
                    required = false
            )
            String requestMsg,

            @RequestParam(
                    value = "addressDefault",
                    required = false,
                    defaultValue = "N"
            )
            String addressDefault,

            @RequestParam(
                    value = "checkoutNo",
                    required = false
            )
            Integer checkoutNo,

            @RequestParam(
                    value = "from",
                    required = false
            )
            String from,

            Model model) {

        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember()
                          .getMemberNo();

        AddressDTO dto = new AddressDTO();

        dto.setMemberNo(memberNo.intValue());
        dto.setReceiverName(receiverName);
        dto.setTel(tel);
        dto.setZipcode(zipcode);
        dto.setAddress(address);
        dto.setDetailAddress(detailAddress);
        dto.setRequestMsg(requestMsg);
        dto.setAddressDefault(addressDefault);

        int result =
                addressService.insertAddress(dto);

        if (result <= 0) {

            model.addAttribute(
                    "error",
                    "배송지 등록에 실패했습니다."
            );

            model.addAttribute(
                    "checkoutNo",
                    checkoutNo
            );

            model.addAttribute(
                    "from",
                    from
            );

            return "address_add";
        }


        // 결제 화면에서 배송지 추가로 들어온 경우
        if ("payment".equals(from)
                && checkoutNo != null) {

            return "redirect:/order/payment?checkoutNo="
                    + checkoutNo;
        }

        // 일반 배송지 관리에서 들어온 경우
        return "redirect:/address/list";
    }
    
    
    @GetMapping("/list")
    public String addressList(
            Authentication authentication,
            Model model) {

        // 현재 로그인한 사용자
        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember()
                          .getMemberNo();


        List<AddressDTO> addressList =
                addressService.getAddressList(memberNo);

        model.addAttribute(
                "addressList",
                addressList
        );

        return "address/addressbook";
    }
    
    @GetMapping("/edit")
    public String edit(
            Authentication authentication,

            @RequestParam(
                    value = "addressNo",
                    required = false
            )
            Integer addressNo,

            @RequestParam(
                    value = "checkoutNo",
                    required = false
            )
            Integer checkoutNo,

            @RequestParam(
                    value = "from",
                    required = false
            )
            String from,

            Model model) {

        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember()
                          .getMemberNo();

        if (addressNo == null) {

            return redirectBack(
                    from,
                    checkoutNo,
                    null
            );
        }

        AddressDTO address =
                addressService.getAddressForEdit(
                        addressNo,
                        memberNo
                );

        if (address == null) {

            return redirectBack(
                    from,
                    checkoutNo,
                    null
            );
        }


        model.addAttribute(
                "address",
                address
        );

        model.addAttribute(
                "checkoutNo",
                checkoutNo
        );

        model.addAttribute(
                "from",
                from
        );


        return "address/address_edit";
    }

    @PostMapping("/edit")
    public String edit(
            Authentication authentication,

            @RequestParam("addressNo")
            Integer addressNo,

            @RequestParam("receiverName")
            String receiverName,

            @RequestParam("tel")
            String tel,

            @RequestParam("zipcode")
            String zipcode,

            @RequestParam("address")
            String address,

            @RequestParam(
                    value = "detailAddress",
                    required = false
            )
            String detailAddress,

            @RequestParam(
                    value = "requestMsg",
                    required = false
            )
            String requestMsg,

            @RequestParam(
                    value = "addressDefault",
                    required = false,
                    defaultValue = "N"
            )
            String addressDefault,

            @RequestParam(
                    value = "checkoutNo",
                    required = false
            )
            Integer checkoutNo,

            @RequestParam(
                    value = "from",
                    required = false
            )
            String from,

            Model model,
            RedirectAttributes rttr) {


        CustomUser customUser =
                (CustomUser) authentication.getPrincipal();

        Long memberNo =
                customUser.getMember()
                          .getMemberNo();

        AddressDTO dto =
                new AddressDTO();

        dto.setAddressNo(addressNo);
        dto.setMemberNo(memberNo);

        dto.setReceiverName(receiverName);
        dto.setTel(tel);
        dto.setZipcode(zipcode);

        dto.setAddress(address);
        dto.setDetailAddress(detailAddress);

        dto.setRequestMsg(requestMsg);
        dto.setAddressDefault(addressDefault);


        int result =
                addressService.updateAddress(dto);

        if (result > 0) {

            return redirectBack(
                    from,
                    checkoutNo,
                    addressNo
            );
        }

        if ("payment".equals(from)
                && checkoutNo != null) {

            rttr.addFlashAttribute(
                    "addressEditError",
                    "배송지 수정에 실패했습니다."
            );

            rttr.addAttribute(
                    "checkoutNo",
                    checkoutNo
            );

            rttr.addAttribute(
                    "addressNo",
                    addressNo
            );


            return "redirect:/order/payment";
        }

        model.addAttribute(
                "error",
                "배송지 수정에 실패했습니다."
        );

        model.addAttribute(
                "address",
                dto
        );

        model.addAttribute(
                "checkoutNo",
                checkoutNo
        );

        model.addAttribute(
                "from",
                from
        );


        return "address/address_edit";
    }

    private String redirectBack(
            String from,
            Integer checkoutNo,
            Integer addressNo) {


        // 결제 페이지에서 들어온 경우
        if ("payment".equals(from)
                && checkoutNo != null) {

            StringBuilder url =
                    new StringBuilder();

            url.append(
                    "redirect:/order/payment"
            );

            url.append(
                    "?checkoutNo="
            );

            url.append(
                    checkoutNo
            );


            if (addressNo != null) {

                url.append(
                        "&addressNo="
                );

                url.append(
                        addressNo
                );
            }


            return url.toString();
        }


        // 배송지 관리 페이지
        return "redirect:/address/list";
    }
}