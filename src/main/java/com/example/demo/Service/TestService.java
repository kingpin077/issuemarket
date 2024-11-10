
package com.example.demo.Service;

import com.example.demo.DTO.TestDTO;
import com.example.demo.Entity.TestEntity;
import com.example.demo.Repository.TestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service
public class TestService {
    private final TestRepository testRepository;
    @Autowired
    public TestService(TestRepository testRepository) {
        this.testRepository = testRepository;
    }

    public List<TestDTO> findAllByPcOrderByDesc() {
        List<TestEntity> entities = testRepository.findTop10ByOrderByPcDesc();

        return entities.stream().map(entity -> {
            TestDTO dto = new TestDTO();
            dto.setKeyword(entity.getKeyword());
            dto.setPc(entity.getPc());
            return dto;
        }).collect(Collectors.toList());
    }
    // 새로운 페이징 메서드
    public Page<TestDTO> findAllByPcOrderByDescPaged(int page) {
        PageRequest pageRequest = PageRequest.of(page, 5); // 5개씩 페이징
        Page<TestEntity> entities = testRepository.findAllByOrderByPcDesc(pageRequest);

        return entities.map(entity -> {
            TestDTO dto = new TestDTO();
            dto.setKeyword(entity.getKeyword());
            dto.setPc(entity.getPc());
            return dto;
        });
    }

    public Page<TestDTO> findAllByTotalOrderByDescPaged(int page) {
        PageRequest pageRequest = PageRequest.of(page, 5); // 5개씩 페이징
        Page<TestEntity> entities = testRepository.findAllByOrderByTotalDesc(pageRequest);

        return entities.map(entity -> {
            TestDTO dto = new TestDTO();
            dto.setKeyword(entity.getKeyword());
            dto.setTotal(entity.getTotal());
            return dto;
        });
    }


    public List<TestDTO> findAllByTotalOrderByDesc() {
        List<TestEntity> entities = testRepository.findTop10ByOrderByTotalDesc();

        return entities.stream().map(entity -> {
            TestDTO dto = new TestDTO();
            dto.setKeyword(entity.getKeyword());
            dto.setTotal(entity.getTotal());
            return dto;
        }).collect(Collectors.toList());
    }


    public List<TestDTO> findAllByTagOrderByDesc(String tag) {
        List<TestEntity> entities = testRepository.findTop10ByTagOrderByTotalDesc(tag);

        return entities.stream().map(entity -> {
            TestDTO dto = new TestDTO();
            dto.setKeyword(entity.getKeyword());
            dto.setTotal(entity.getTotal());
            return dto;
        }).collect(Collectors.toList());
    }


    // 총 검색량 상위 30개의 데이터를 가져오는 메서드
    public List<TestDTO> findTop50ByTotalOrderByDesc() {
        List<TestEntity> entities = testRepository.findTop50ByOrderByTotalDesc();

        return entities.stream().map(entity -> {
            TestDTO dto = new TestDTO();
            dto.setKeyword(entity.getKeyword());
            dto.setTotal(entity.getTotal());
            return dto;
        }).collect(Collectors.toList());
    }


    // 워드 클라우드를 위한 리스트 생성 메서드
    public List<Map<String, Object>> getWordCloudList() {
        List<Object[]> data = testRepository.findKeywordsAndTotalByOrderByTotalDesc();

        // 각 키워드와 total 값을 Map으로 변환하고 리스트로 반환
        return data.stream()
                .map(row -> Map.of(
                        "keyword", row[0],  // keyword
                        "total", row[1]     // total
                ))
                .collect(Collectors.toList());
    }

    // 태그별 페이징 조회 메서드 (수정)
    public Page<TestDTO> findAllByTagOrderByDescPaged(String tag, int page) {
        PageRequest pageRequest = PageRequest.of(page, 10); // 10개씩 페이징
        Page<TestEntity> entities = testRepository.findAllByTagOrderByTotalDesc(tag, pageRequest);
        return entities.map(entity -> {
            TestDTO dto = new TestDTO();
            dto.setKeyword(entity.getKeyword());
            dto.setTotal(entity.getTotal());
            return dto;
        });
    }

    /** 엑셀 다운로드 로직 **/
    private final ObjectMapper objectMapper = new ObjectMapper();

    public  byte[] generateExcelFile(Map<String, Object> data) throws IOException {
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Issue Market");

            int rowNum = 0;

            // 퍼센트 형식의 CellStyle 설정
            CellStyle percentStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            percentStyle.setDataFormat(format.getFormat("0%"));  // 숫자를 백분율 형식으로 설정

            // 1. naverData 작성
            String naverDataJson = (String) data.get("naverData");
            if (naverDataJson != null && !naverDataJson.isEmpty()) {
                Map<String, Object> naverData = objectMapper.readValue(naverDataJson, Map.class);

                // naverData 섹션 제목
                Row naverTitleRow = sheet.createRow(rowNum++);
                naverTitleRow.createCell(0).setCellValue("Naver Data");

                // naverData 값 작성
                for (Map.Entry<String, Object> entry : naverData.entrySet()) {
                    Row row = sheet.createRow(rowNum++);
                    String label;

                    // Switch 문을 사용하여 각 키에 대한 레이블 지정
                    switch (entry.getKey()) {
                        case "startDate":
                            label = "시작 날짜";
                            break;
                        case "endDate":
                            label = "종료 날짜";
                            break;
                        case "timeUnit":
                            label = "시간 단위";
                            break;
                        case "results": //results 항목 건너뛰기
                            continue;
                        default:
                            label = entry.getKey(); // 기본적으로 키를 레이블로 사용
                            break;
                    }

                    row.createCell(0).setCellValue(label); // 레이블 설정
                    row.createCell(1).setCellValue(entry.getValue() != null ? entry.getValue().toString() : ""); // 값 설정
                }
                sheet.createRow(rowNum++); // 빈 행 추가
            }

            // 2. kakaoData 작성
            Map<String, Object> kakaoData = (Map<String, Object>) data.get("kakaoData");
            if (kakaoData != null) {

                // Gender 데이터 작성
                Row genderTitleRow = sheet.createRow(rowNum++);
                genderTitleRow.createCell(0).setCellValue("성별 통계");
                Map<String, Integer> genderData = (Map<String, Integer>) kakaoData.get("gender");
                Row genderRow = sheet.createRow(rowNum++);
                genderRow.createCell(0).setCellValue("남성");
                Cell maleCell = genderRow.createCell(1);
                maleCell.setCellValue(genderData.get("male") / 100.0);  // 백분율로 계산
                maleCell.setCellStyle(percentStyle);
                genderRow.createCell(2).setCellValue("여성");
                Cell femaleCell = genderRow.createCell(3);
                femaleCell.setCellValue(genderData.get("female") / 100.0);  // 백분율로 계산
                femaleCell.setCellStyle(percentStyle);

                sheet.createRow(rowNum++); // 빈 행 추가

                // Age 데이터 작성
                Row ageTitleRow = sheet.createRow(rowNum++);
                ageTitleRow.createCell(0).setCellValue("연령별 통계");

                Map<String, Integer> ageData = (Map<String, Integer>) kakaoData.get("age");
                for (Map.Entry<String, Integer> entry : ageData.entrySet()) {
                    Row ageRow = sheet.createRow(rowNum++);
                    ageRow.createCell(0).setCellValue(entry.getKey() + "대");

                    Cell ageCell = ageRow.createCell(1);
                    ageCell.setCellValue(entry.getValue() / 100.0);  // 백분율로 계산
                    ageCell.setCellStyle(percentStyle);
                }
                rowNum++; // 빈 행 추가
            }

            // 3. monthlyPcQcCnt 및 monthlyMobileQcCnt 추가
            Integer monthlyPcQcCnt = (Integer) data.get("monthlyPcQcCnt");
            Integer monthlyMobileQcCnt = (Integer) data.get("monthlyMobileQcCnt");
            if (monthlyPcQcCnt != null || monthlyMobileQcCnt != null) {
                Row monthlyTitleRow = sheet.createRow(rowNum++);

                if (monthlyPcQcCnt != null) {
                    Row pcRow = sheet.createRow(rowNum++);
                    pcRow.createCell(0).setCellValue("PC 검색량");
                    pcRow.createCell(1).setCellValue(monthlyPcQcCnt);
                }

                if (monthlyMobileQcCnt != null) {
                    Row mobileRow = sheet.createRow(rowNum++);
                    mobileRow.createCell(0).setCellValue("모바일 검색량");
                    mobileRow.createCell(1).setCellValue(monthlyMobileQcCnt);
                }

                sheet.createRow(rowNum++); // 빈 행 추가
            }

            // 4. ratioResults 작성
            List<Map<String, Object>> ratioResults = (List<Map<String, Object>>) data.get("ratioResults");
            if (ratioResults != null && !ratioResults.isEmpty()) {
                Row ratioTitleRow = sheet.createRow(rowNum++);
                ratioTitleRow.createCell(0).setCellValue("기간별 검색량");

                Row ratioHeaderRow = sheet.createRow(rowNum++);
                ratioHeaderRow.createCell(0).setCellValue("날짜");
                ratioHeaderRow.createCell(1).setCellValue("값");

                for (Map<String, Object> ratio : ratioResults) {
                    Row ratioRow = sheet.createRow(rowNum++);

                    // 날짜 설정
                    Object periodObj = ratio.get("period");
                    ratioRow.createCell(0).setCellValue(periodObj != null ? periodObj.toString() : "");

                    // 값이 존재하고, 숫자 타입인지 확인 후 정수로 변환하여 설정
                    Object estimatedValueObj = ratio.get("estimatedValue");
                    if (estimatedValueObj instanceof Number) {
                        int estimatedValue = ((Number) estimatedValueObj).intValue();
                        ratioRow.createCell(1).setCellValue(estimatedValue);
                    } else {
                        ratioRow.createCell(1).setCellValue(0); // 값이 없으면 기본값 0으로 설정
                    }
                }

                sheet.createRow(rowNum++); // 빈 행 추가
            }

            // 각 열의 너비를 자동으로 조정
            sheet.autoSizeColumn(0); // 첫 번째 열 너비 자동 조정
            sheet.autoSizeColumn(1); // 두 번째 열 너비 자동 조정

            // 4. 엑셀 파일로 출력
            workbook.write(out);
            return out.toByteArray();
        }
    }

}