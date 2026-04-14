# 월간 작업 리포트 필터링 및 타임존 수정 진행 현황

## 1. 개요
'월간 작업 다운로드' 엑셀 추출 시 발생하는 날짜 필터링 오류와 타임존 일자 오차(1일 차이) 문제를 해결하고, 프로젝트의 실제 구동 구조를 문서화함.

## 2. 프로젝트 기술 지도 (Architectural Map) 🗺️

이 프로젝트는 **전통적인 Spring Boot(JSP)와 현대적인 Vue.js가 결합된 하이브리드 구조**입니다.

### 🔄 데이터 흐름 및 구동 방식
1.  **접속**: 브라우저에서 `/work/list.do` 호출.
2.  **컨트롤러**: `WorkController.java`가 요청을 받아 `WEB-INF/views/work/work0100.jsp` 반환.
3.  **컨테이너(JSP)**: JSP 파일은 실질적인 로직이 거의 없으며, Vue 앱을 실행하기 위한 빈 그릇(`<div id="v-app">`) 역할만 수행.
4.  **라우팅**: `static/js/src/router.js`가 URL 경로를 해석하여 필요한 Vue 컴포넌트 로드.
5.  **실무 로직 (중요)**: `static/js/vue/WorkComponent.vue` 등 **Vue 소스 파일**에서 모든 UI 렌더링, 이벤트 처리(엑셀 추출 등), API 호출 로직이 수행됨.

### 📂 주요 디렉토리 가이드
-   **Back-end (Java)**
    -   `src/main/java/com/uixs/controller`: API 및 페이지 라우팅 엔드포인트.
    -   `src/main/java/com/uixs/model/work/dao`: MyBatis/SQLite DB 접근 로직.
    -   `src/main/java/com/uixs/model/work/dto`: 데이터 객체 및 JSON 변환 정의.
    -   `src/main/java/com/uixs/util`: 타임존 보정 등 유틸리티 로직.
-   **Front-end (Vue.js & Static)**
    -   `src/main/resources/static/js/vue`: **[수정 1순위]** 실제 화면과 로직이 담긴 Vue 컴포넌트들.
    -   `src/main/resources/static/js/src/router.js`: Vue 라우터 설정.
-   **Legacy/Shell (JSP)**
    -   `src/main/webapp/WEB-INF/views`: Vue를 띄우기 위한 껍데기 파일들. **(가급적 수정을 피할 것)**

## 3. 핵심 수정 사항 (2026-04-10)

### ✅ [타임존 보정] 날짜 1일 오차 해결
-   **파일명**: `FlexibleLocalDateTimeDeserializer.java`
-   **내용**: MongoDB 이전 데이터(UTC기준)를 Java에서 읽을 때, 오프셋을 무시하던 로직을 수정하여 `Asia/Seoul` (UTC+9)로 정확히 변환하도록 개선.
-   **결과**: 운영 서버와 동일하게 날짜가 정상 표시됨 (예: 16일 -> 17일 정정).

### ✅ [엑셀 필터링] 타 월 데이터 배제
-   **파일명**: `WorkComponent.vue`
-   **내용**: `excelDownload` 메서드 내 루프에서 '착수일' 또는 '실제 종료일'이 선택한 월에 해당하는 행만 포함하도록 정규식 기반 필터링 적용.
-   **결과**: 4월 리포트에서 3월 작업 내역이 완전히 배제됨.

### ✅ [기타] 코드 정리
-   `work0100.jsp`에 임시로 추가되었던 테스트 스크립트 전량 제거 및 원복.
-   `WorkController.java`의 디버깅 콘솔 로그 제거.

---
**주의사항**: 향후 UI나 엑셀 관련 수정 요청 시 반드시 `src/main/resources/static/js/vue` 폴더 내의 컴포넌트 파일을 수정해야 합니다.
