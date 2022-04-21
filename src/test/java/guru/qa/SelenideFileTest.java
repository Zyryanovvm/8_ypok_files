package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.selenide.Selenide;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Автотесты с файлами")
public class SelenideFileTest {

    ClassLoader ggl = SelenideFileTest.class.getClassLoader();

    @DisplayName("Тестирование файла readme.md")
    @Test
    void testReadmeFiles() throws Exception {
        Selenide.open("https://github.com/junit-team/junit5/blob/main/README.md");
        File downloadFile = Selenide.$("#raw-url").download();
        try (InputStream is = new FileInputStream(downloadFile)) {
            assertThat(new String(is.readAllBytes(), StandardCharsets.UTF_8))
                    .contains("This repository is the home of the next generation of JUnit");
        }
    }

    @DisplayName("Проверка количества страниц у файла PDF")
    @Test
    void testPdfFiles() throws Exception {
        InputStream stream = ggl.getResourceAsStream("pdf/junit-user-guide-5.8.2.pdf");
        PDF pdf = new PDF(stream);
        Assertions.assertEquals(166, pdf.numberOfPages);
    }

    @DisplayName("Проверка содержимового файла XLS (8 строка,5 столбец)")
    @Test
    void testXlsFiles() throws Exception {
        InputStream stream = ggl.getResourceAsStream("xls/XLS_10.xls");
        XLS xls = new XLS(stream);
        String stringCellValue = xls.excel.getSheetAt(0).getRow(7).getCell(4).getStringCellValue();
        org.assertj.core.api.Assertions.assertThat(stringCellValue).contains("Great Britain");

    }

    @DisplayName("Проверка содержимового файла CSV")
    @Test
    void testCsvFiles() throws Exception {
        try (InputStream stream = ggl.getResourceAsStream("csv/cucu.csv");
             CSVReader reader = new CSVReader(new InputStreamReader(stream, "UTF-8"))) {

            List<String[]> content = reader.readAll();
            org.assertj.core.api.Assertions.assertThat(content).contains(
                    new String[]{"Name","Surname"},
                    new String[]{"Vladimir","Zyryanov"},
                    new String[]{"Petr","Yan"}
            );
        }
    }

    @DisplayName("Проверка файлов содержимых в ZIP файле")
    @Test
    void testZipFiles() throws Exception {
        ZipFile zf = new ZipFile(new File("src/test/resources/zip/super.zip"));
        ZipInputStream is = new ZipInputStream(ggl.getResourceAsStream("zip/super.zip"));
        ZipEntry entry;
        while ((entry = is.getNextEntry()) != null) {
            if (entry.getName().equals("junit-user-guide-5.8.2.pdf")) {
                try (InputStream stream = zf.getInputStream(entry)) {
                    assert stream != null;
                    PDF pdf = new PDF(stream);
                    Assertions.assertEquals(166, pdf.numberOfPages);
                }
            }
            if (entry.getName().equals("cucu.csv")) {
                try (InputStream stream = zf.getInputStream(entry)) {
                    assert stream != null;
                    try (CSVReader reader = new CSVReader(new InputStreamReader(stream, "UTF-8")))
                        {
                            List<String[]> content = reader.readAll();
                        org.assertj.core.api.Assertions.assertThat(content).contains(
                                new String[]{"Name", "Surname"},
                                new String[]{"Vladimir", "Zyryanov"},
                                new String[]{"Petr", "Yan"}
                        );
                        }
                    }
                }
            if (entry.getName().equals("XLS_10.xls")) {
                try (InputStream stream = zf.getInputStream(entry)) {
                    assert stream != null;
                    XLS xls = new XLS(stream);
                    String stringCellValue = xls.excel.getSheetAt(0).getRow(6).getCell(2)
                            .getStringCellValue();
                    org.assertj.core.api.Assertions.assertThat(stringCellValue).contains("Brumm");
                }
            }

        }

    }


}
