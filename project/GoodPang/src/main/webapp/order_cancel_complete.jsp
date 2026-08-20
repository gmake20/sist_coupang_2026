<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>주문 취소 완료</title>

<style>
* {
    box-sizing: border-box;
}

body {
    margin: 0;
    background-color: #f5f5f5;
    font-family: Arial, "Malgun Gothic", sans-serif;
    color: #222;
}

.complete-wrap {
    width: 700px;
    min-height: 100vh;
    margin: 0 auto;
    background-color: #fff;

    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    text-align: center;
    padding: 40px;
}

.complete-icon {
    width: 70px;
    height: 70px;

    display: flex;
    align-items: center;
    justify-content: center;

    margin-bottom: 25px;

    border-radius: 50%;
    background-color: #e51b23;
    color: #fff;

    font-size: 40px;
    font-weight: bold;
}

.complete-wrap h1 {
    margin: 0 0 15px;
    font-size: 26px;
}

.complete-wrap p {
    margin: 0 0 35px;
    color: #666;
    font-size: 15px;
}

.detail-btn {
    width: 300px;
    height: 52px;

    border: none;
    border-radius: 5px;

    background-color: #e51b23;
    color: #fff;

    font-size: 16px;
    font-weight: bold;

    cursor: pointer;
}

.detail-btn:hover {
    background-color: #c9151c;
}
</style>

</head>

<body>

<div class="complete-wrap">

    <div class="complete-icon">
        ✓
    </div>

    <h1>주문 취소 완료</h1>

    <p>
        주문이 정상적으로 취소되었습니다.
    </p>

    <button type="button"
            class="detail-btn"
            onclick="location.href='${pageContext.request.contextPath}/order_detail.jsp'">
        주문 상세 보기
    </button>

</div>

</body>
</html>