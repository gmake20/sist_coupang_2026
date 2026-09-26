package org.doit.goodpang.util;

public final class PaymentMethodValidator {

    private PaymentMethodValidator() {
    }

    public static String validatePaymentType(String paymentType) {

        if (paymentType == null
                || paymentType.isBlank()) {

            return "결제수단 종류를 선택해주세요.";
        }

        if (!"BANK".equals(paymentType)
                && !"CARD".equals(paymentType)) {

            return "올바르지 않은 결제수단입니다.";
        }

        return null;
    }

    public static String validateBank(
            String bankCode,
            String accountNumber,
            String accountHolder) {

        if (bankCode == null
                || bankCode.isBlank()) {

            return "은행을 선택해주세요.";
        }


        if (accountNumber == null
                || accountNumber.isBlank()) {

            return "계좌번호를 입력해주세요.";
        }


        String cleanedAccountNumber =
                accountNumber.replaceAll(
                        "[^0-9]",
                        ""
                );

        if (cleanedAccountNumber.length() < 8
                || cleanedAccountNumber.length() > 20) {

            return "올바른 계좌번호를 입력해주세요.";
        }


        if (accountHolder == null
                || accountHolder.isBlank()) {

            return "예금주명을 입력해주세요.";
        }


        if (accountHolder.trim().length() < 2) {

            return "예금주명을 올바르게 입력해주세요.";
        }


        return null;
    }

    public static String validateCard(
            String cardCompany,
            String cardNumber) {

        if (cardCompany == null
                || cardCompany.isBlank()) {

            return "카드사를 선택해주세요.";
        }


        if (cardNumber == null
                || cardNumber.isBlank()) {

            return "카드번호를 입력해주세요.";
        }


        String cleanedCardNumber =
                cardNumber.replaceAll(
                        "[^0-9]",
                        ""
                );


        if (cleanedCardNumber.length() != 16) {

            return "카드번호 16자리를 입력해주세요.";
        }
        return null;
    }
}