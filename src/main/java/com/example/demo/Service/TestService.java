
package com.example.demo.Service;

import com.example.demo.DTO.TestDTO;
import com.example.demo.Entity.TestEntity;
import com.example.demo.Repository.TestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xddf.usermodel.chart.*;
import org.apache.poi.xssf.usermodel.*;
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
    public List<TestDTO> findTop100ByTotalOrderByDesc() {
        List<TestEntity> entities = testRepository.findTop100ByOrderByTotalDesc();

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

            // Font와 CellStyle 생성
            Font boldFont = workbook.createFont();
            boldFont.setBold(true); // Bold 설정
            boldFont.setFontName("Arial"); // 원하는 글꼴 이름 설정 (선택 사항)
            CellStyle boldCellStyle = workbook.createCellStyle();
            boldCellStyle.setFont(boldFont);

            // 퍼센트 형식의 CellStyle 설정
            CellStyle percentStyle = workbook.createCellStyle();
            DataFormat format = workbook.createDataFormat();
            percentStyle.setDataFormat(format.getFormat("0%"));  // 숫자를 백분율 형식으로 설정

            String keyword = (String) data.get("keyword_search");

            // 1. naverData 작성
            String naverDataJson = (String) data.get("naverData");
            if (naverDataJson != null && !naverDataJson.isEmpty()) {
                Map<String, Object> naverData = objectMapper.readValue(naverDataJson, Map.class);

                // naverData 섹션 제목
                Row naverTitleRow = sheet.createRow(rowNum++);
                Cell keywordCell = naverTitleRow.createCell(0);

                naverTitleRow.createCell(0).setCellValue("키워드");
                // Bold 스타일과 배경색 설정
                CellStyle boldAndColoredCellStyle = workbook.createCellStyle();
                // 기존 boldFont 재사용
                boldAndColoredCellStyle.setFont(boldFont);

                // 배경색 설정
                XSSFColor backgroundColor = new XSSFColor(new java.awt.Color(166, 166, 166), null); // #A6A6A6 색상
                ((XSSFCellStyle) boldAndColoredCellStyle).setFillForegroundColor(backgroundColor);
                boldAndColoredCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
                // 스타일 적용
                keywordCell.setCellStyle(boldAndColoredCellStyle);

                naverTitleRow.createCell(1).setCellValue(keyword);

                sheet.createRow(rowNum++); // 빈 행 추가

                // Bold 스타일과 새로운 배경색(#FFFF00) 설정 (for문 밖에서 정의)
                CellStyle boldAndYellowCellStyle = workbook.createCellStyle();
                boldAndYellowCellStyle.setFont(boldFont); // 기존 boldFont 재사용

                // 배경색 설정
                XSSFColor yellowBackgroundColor = new XSSFColor(new java.awt.Color(255, 255, 0), null); // #FFFF00 색상
                ((XSSFCellStyle) boldAndYellowCellStyle).setFillForegroundColor(yellowBackgroundColor);
                boldAndYellowCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);


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
                    row.getCell(0).setCellStyle(boldAndYellowCellStyle); // 스타일 적용
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
                // Bold 스타일 적용 (배경색 없음)
                CellStyle boldOnlyCellStyle = workbook.createCellStyle();
                boldOnlyCellStyle.setFont(boldFont); // 기존 boldFont 재사용
                // 스타일 적용
                genderTitleRow.getCell(0).setCellStyle(boldOnlyCellStyle);

                Map<String, Integer> genderData = (Map<String, Integer>) kakaoData.get("gender");
                Row genderRow = sheet.createRow(rowNum++);

                // Bold 스타일과 배경색(#E97132) 설정
                CellStyle boldAndOrangeCellStyle = workbook.createCellStyle();
                boldAndOrangeCellStyle.setFont(boldFont); // 기존 boldFont 재사용

                // 배경색 설정
                XSSFColor orangeBackgroundColor = new XSSFColor(new java.awt.Color(233, 113, 50), null); // #E97132 색상
                ((XSSFCellStyle) boldAndOrangeCellStyle).setFillForegroundColor(orangeBackgroundColor);
                boldAndOrangeCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                genderRow.createCell(0).setCellValue("남성");
                genderRow.getCell(0).setCellStyle(boldAndOrangeCellStyle); // Bold와 배경색 적용
                Cell maleCell = genderRow.createCell(1);
                maleCell.setCellValue(genderData.get("male") / 100.0);  // 백분율로 계산
                maleCell.setCellStyle(percentStyle);
                genderRow.createCell(2).setCellValue("여성");
                genderRow.getCell(2).setCellStyle(boldAndOrangeCellStyle); // Bold와 배경색 적용
                Cell femaleCell = genderRow.createCell(3);
                femaleCell.setCellValue(genderData.get("female") / 100.0);  // 백분율로 계산
                femaleCell.setCellStyle(percentStyle);

                sheet.createRow(rowNum++); // 빈 행 추가

                // Age 데이터 작성
                Row ageTitleRow = sheet.createRow(rowNum++);
                ageTitleRow.createCell(0).setCellValue("연령별 통계");
                // Bold 스타일 적용 (배경색 없음)
                boldOnlyCellStyle.setFont(boldFont); // 기존 boldFont 재사용
                ageTitleRow.getCell(0).setCellStyle(boldOnlyCellStyle);

                Map<String, Integer> ageData = (Map<String, Integer>) kakaoData.get("age");
                int startRow = rowNum;

                // Bold 스타일과 배경색(#4EA72E) 설정 (for문 밖에서 한 번만 생성)
                CellStyle boldAndGreenCellStyle = workbook.createCellStyle();
                boldAndGreenCellStyle.setFont(boldFont); // 기존 boldFont 재사용

                // 배경색 설정
                XSSFColor greenBackgroundColor = new XSSFColor(new java.awt.Color(78, 167, 46), null); // #4EA72E 색상
                ((XSSFCellStyle) boldAndGreenCellStyle).setFillForegroundColor(greenBackgroundColor);
                boldAndGreenCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                for (Map.Entry<String, Integer> entry : ageData.entrySet()) {
                    Row ageRow = sheet.createRow(rowNum++);
                    ageRow.createCell(0).setCellValue(entry.getKey() + "대");
                    ageRow.getCell(0).setCellStyle(boldAndGreenCellStyle); // Bold와 배경색 적용

                    Cell ageCell = ageRow.createCell(1);
                    ageCell.setCellValue(entry.getValue() / 100.0);  // 백분율로 계산
                    ageCell.setCellStyle(percentStyle);
                }
                int endRow = rowNum - 1;
                rowNum = rowNum + 3; // 빈 행 추가
                // 원그래프 추가
                XSSFDrawing drawing = ((XSSFSheet) sheet).createDrawingPatriarch();
                XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 3, startRow, 8, endRow + 5);

                XSSFChart chart = drawing.createChart(anchor);
                chart.setTitleText("연령별 비율"); // 차트 제목
                chart.setTitleOverlay(false);

                XDDFChartLegend legend = chart.getOrAddLegend();
                legend.setPosition(LegendPosition.TOP_RIGHT);

                XSSFSheet xssfSheet = (XSSFSheet) sheet; // Sheet 객체를 XSSFSheet로 캐스팅

                XDDFCategoryDataSource categories = XDDFDataSourcesFactory.fromStringCellRange(xssfSheet,
                        new CellRangeAddress(startRow, endRow, 0, 0)); // 연령 범위
                XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromNumericCellRange(xssfSheet,
                        new CellRangeAddress(startRow, endRow, 1, 1)); // 값 범위

                XDDFPieChartData pieData = (XDDFPieChartData) chart.createData(ChartTypes.PIE, null, null);
                XDDFPieChartData.Series series = (XDDFPieChartData.Series) pieData.addSeries(categories, values);

                pieData.setVaryColors(true); // 각 조각의 색상 다르게 설정
                chart.plot(pieData);
                sheet.createRow(rowNum++); // 빈 행 추가
            }

            // 3. monthlyPcQcCnt 및 monthlyMobileQcCnt 추가
            Integer monthlyPcQcCnt = (Integer) data.get("monthlyPcQcCnt");
            Integer monthlyMobileQcCnt = (Integer) data.get("monthlyMobileQcCnt");
            if (monthlyPcQcCnt != null || monthlyMobileQcCnt != null) {
                Row deviceSearchTitleRow = sheet.createRow(rowNum++);

                Cell deviceSearchTitleCell = deviceSearchTitleRow.createCell(0);

                deviceSearchTitleCell.setCellValue("기기별 검색량");

                // Bold 스타일 적용
                CellStyle boldOnlyCellStyle = workbook.createCellStyle();
                boldOnlyCellStyle.setFont(boldFont); // 기존 boldFont 재사용
                deviceSearchTitleCell.setCellStyle(boldOnlyCellStyle);

                // Bold 스타일과 배경색(#E49EDD) 설정 (for문 밖에서 한 번 생성)
                CellStyle boldAndPinkCellStyle = workbook.createCellStyle();
                boldAndPinkCellStyle.setFont(boldFont); // 기존 boldFont 재사용
                XSSFColor pinkBackgroundColor = new XSSFColor(new java.awt.Color(228, 158, 221), null); // #E49EDD 색상
                ((XSSFCellStyle) boldAndPinkCellStyle).setFillForegroundColor(pinkBackgroundColor);
                boldAndPinkCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                if (monthlyPcQcCnt != null) {
                    Row pcRow = sheet.createRow(rowNum++);
                    pcRow.createCell(0).setCellValue("PC 검색량");
                    pcRow.getCell(0).setCellStyle(boldAndPinkCellStyle); // 스타일 적용
                    pcRow.createCell(1).setCellValue(monthlyPcQcCnt);
                }

                if (monthlyMobileQcCnt != null) {
                    Row mobileRow = sheet.createRow(rowNum++);
                    mobileRow.createCell(0).setCellValue("모바일 검색량");
                    mobileRow.getCell(0).setCellStyle(boldAndPinkCellStyle); // 스타일 적용
                    mobileRow.createCell(1).setCellValue(monthlyMobileQcCnt);
                }


                sheet.createRow(rowNum++); // 빈 행 추가
            }

            // 4. ratioResults 작성
            List<Map<String, Object>> ratioResults = (List<Map<String, Object>>) data.get("ratioResults");
            if (ratioResults != null && !ratioResults.isEmpty()) {
                Row ratioTitleRow = sheet.createRow(rowNum++);
                ratioTitleRow.createCell(0).setCellValue("기간별 검색량");
                ratioTitleRow.getCell(0).setCellStyle(boldCellStyle);
                Row ratioHeaderRow = sheet.createRow(rowNum++);
                ratioHeaderRow.createCell(0).setCellValue("날짜");
                ratioHeaderRow.getCell(0).setCellStyle(boldCellStyle); // "날짜" 셀에 Bold 적용
                ratioHeaderRow.createCell(1).setCellValue("값");
                ratioHeaderRow.getCell(1).setCellStyle(boldCellStyle); // "값" 셀에 Bold 적용

                int startRow = rowNum;
                // Bold 스타일과 배경색(#94DCF8) 설정 (for문 밖에서 한 번만 생성)
                CellStyle boldAndBlueCellStyle = workbook.createCellStyle();
                boldAndBlueCellStyle.setFont(boldFont); // 기존 boldFont 재사용

                // 배경색 설정
                XSSFColor blueBackgroundColor = new XSSFColor(new java.awt.Color(148, 220, 248), null); // #94DCF8 색상
                ((XSSFCellStyle) boldAndBlueCellStyle).setFillForegroundColor(blueBackgroundColor);
                boldAndBlueCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

                for (Map<String, Object> ratio : ratioResults) {
                    Row ratioRow = sheet.createRow(rowNum++);

                    // 날짜 설정
                    Object periodObj = ratio.get("period");
                    ratioRow.createCell(0).setCellValue(periodObj != null ? periodObj.toString() : "");
                    ratioRow.getCell(0).setCellStyle(boldAndBlueCellStyle); // Bold와 배경색 적용

                    // 값이 존재하고, 숫자 타입인지 확인 후 정수로 변환하여 설정
                    Object estimatedValueObj = ratio.get("estimatedValue");
                    if (estimatedValueObj instanceof Number) {
                        int estimatedValue = ((Number) estimatedValueObj).intValue();
                        ratioRow.createCell(1).setCellValue(estimatedValue);
                    } else {
                        ratioRow.createCell(1).setCellValue(0.0); // 값이 없으면 기본값 0으로 설정
                    }
                }
                int endRow = rowNum - 1;

                // 꺾은선 그래프 추가
                XSSFDrawing drawing = ((XSSFSheet) sheet).createDrawingPatriarch();
                XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 3, startRow, 10, startRow + 20); // 그래프 위치 조정

                XSSFChart chart = drawing.createChart(anchor);
                chart.setTitleText("기간별 검색량 추이"); // 그래프 제목
                chart.setTitleOverlay(false);

                XDDFCategoryAxis categoryAxis = chart.createCategoryAxis(AxisPosition.BOTTOM); // 하단 축
                categoryAxis.setTitle("기간");

                XDDFValueAxis valueAxis = chart.createValueAxis(AxisPosition.LEFT); // 왼쪽 값 축
                valueAxis.setTitle("검색량");
                valueAxis.setCrosses(AxisCrosses.AUTO_ZERO);

                XDDFChartLegend legend = chart.getOrAddLegend();
                legend.setPosition(LegendPosition.BOTTOM);

                XDDFCategoryDataSource categories = XDDFDataSourcesFactory.fromStringCellRange((XSSFSheet) sheet,
                        new CellRangeAddress(startRow, endRow, 0, 0)); // 날짜 범위
                XDDFNumericalDataSource<Double> values = XDDFDataSourcesFactory.fromNumericCellRange((XSSFSheet) sheet,
                        new CellRangeAddress(startRow, endRow, 1, 1)); // 값 범위

                XDDFLineChartData lineData = (XDDFLineChartData) chart.createData(ChartTypes.LINE, categoryAxis, valueAxis);
                XDDFLineChartData.Series series = (XDDFLineChartData.Series) lineData.addSeries(categories, values);
                series.setTitle("검색량", null); // 범례 제목 설정
                series.setSmooth(true); // 꺾은선 부드럽게
                series.setMarkerStyle(MarkerStyle.CIRCLE); // 데이터 포인트 마커 스타일

                chart.plot(lineData);

            }

            // 각 열의 너비를 자동으로 조정
            // 열의 너비를 조정
            sheet.setColumnWidth(0, 3500); // 첫 번째 열 (기기별 검색량 제목)
            sheet.autoSizeColumn(1); // 두 번째 열 너비 자동 조정

            // 4. 엑셀 파일로 출력
            workbook.write(out);
            return out.toByteArray();
        }catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("엑셀 파일 생성 중 오류 발생", e);
        }
    }

}