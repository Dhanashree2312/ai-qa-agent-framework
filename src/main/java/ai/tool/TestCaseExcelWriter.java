package ai.tool;

import ai.model.TestCase;
import ai.model.TestStep;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

public class TestCaseExcelWriter {

    public void write(
            List<TestCase> testCases,
            String filePath) {

        if (testCases == null ||
                testCases.isEmpty()) {

            throw new IllegalArgumentException(
                    "Test cases cannot be null or empty"
            );
        }

        try (Workbook workbook =
                     new XSSFWorkbook()) {

            Sheet sheet =
                    workbook.createSheet(
                            "Test Cases"
                    );

            CellStyle headerStyle =
                    createHeaderStyle(workbook);

            CellStyle bodyStyle =
                    workbook.createCellStyle();

            bodyStyle.setWrapText(true);

            createHeader(
                    sheet,
                    headerStyle
            );

            int rowNumber = 1;

            for (TestCase testCase :
                    testCases) {

                List<TestStep> steps =
                        testCase.getTestSteps();

                for (TestStep step :
                        steps) {

                    Row row =
                            sheet.createRow(
                                    rowNumber++
                            );

                    createCell(
                            row,
                            0,
                            testCase.getTestcaseName(),
                            bodyStyle
                    );

                    createCell(
                            row,
                            1,
                            testCase
                                    .getTestcaseDescription(),
                            bodyStyle
                    );

                    createCell(
                            row,
                            2,
                            testCase.getPreConditions(),
                            bodyStyle
                    );

                    createCell(
                            row,
                            3,
                            testCase.getPriority(),
                            bodyStyle
                    );

                    createCell(
                            row,
                            4,
                            testCase.getType(),
                            bodyStyle
                    );

                    createCell(
                            row,
                            5,
                            testCase.getFeature(),
                            bodyStyle
                    );

                    Cell stepNumber =
                            row.createCell(6);

                    stepNumber.setCellValue(
                            step.getStepNumber()
                    );

                    stepNumber.setCellStyle(
                            bodyStyle
                    );

                    createCell(
                            row,
                            7,
                            step.getAction(),
                            bodyStyle
                    );

                    createCell(
                            row,
                            8,
                            step.getExpectedResult(),
                            bodyStyle
                    );

                    createCell(
                            row,
                            9,
                            formatTestData(
                                    testCase.getTestData()
                            ),
                            bodyStyle
                    );
                }
            }

            sheet.createFreezePane(0, 1);

            sheet.setAutoFilter(
                    new CellRangeAddress(
                            0,
                            rowNumber - 1,
                            0,
                            9
                    )
            );

            int[] widths = {
                    30,
                    40,
                    35,
                    15,
                    15,
                    20,
                    10,
                    45,
                    45,
                    30
            };

            for (int i = 0;
                 i < widths.length;
                 i++) {

                sheet.setColumnWidth(
                        i,
                        widths[i] * 256
                );
            }

            try (FileOutputStream outputStream =
                         new FileOutputStream(
                                 filePath
                         )) {

                workbook.write(
                        outputStream
                );
            }

        } catch (IOException e) {

            throw new RuntimeException(
                    "Failed to create Excel test case file",
                    e
            );
        }
    }

    private void createHeader(
            Sheet sheet,
            CellStyle headerStyle) {

        Row header =
                sheet.createRow(0);

        String[] headers = {
                "Test Case Name",
                "Description",
                "Pre-Conditions",
                "Priority",
                "Type",
                "Feature",
                "Step #",
                "Action",
                "Expected Result",
                "Test Data"
        };

        for (int i = 0;
             i < headers.length;
             i++) {

            Cell cell =
                    header.createCell(i);

            cell.setCellValue(
                    headers[i]
            );

            cell.setCellStyle(
                    headerStyle
            );
        }
    }

    private CellStyle createHeaderStyle(
            Workbook workbook) {

        CellStyle style =
                workbook.createCellStyle();

        Font font =
                workbook.createFont();

        font.setBold(true);

        style.setFont(font);
        style.setWrapText(true);

        return style;
    }

    private void createCell(
            Row row,
            int column,
            String value,
            CellStyle style) {

        Cell cell =
                row.createCell(column);

        cell.setCellValue(
                value == null ? "" : value
        );

        cell.setCellStyle(style);
    }

    private String formatTestData(
            Map<String, String> testData) {

        if (testData == null ||
                testData.isEmpty()) {

            return "";
        }

        StringBuilder result =
                new StringBuilder();

        for (Map.Entry<String, String> entry :
                testData.entrySet()) {

            if (result.length() > 0) {
                result.append(", ");
            }

            result.append(
                            entry.getKey()
                    )
                    .append("=")
                    .append(
                            entry.getValue()
                    );
        }

        return result.toString();
    }
}