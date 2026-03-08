package com.example.schoolservlet.utils;

import com.example.schoolservlet.models.SchoolClass;
import com.example.schoolservlet.models.Student;
import com.example.schoolservlet.models.StudentSubject;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.WebColors;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.VerticalAlignment;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;

public class GenerateBulletinPdf {
    public static void generatePDF(HttpServletRequest request, HttpServletResponse response, Student student,
                            List<StudentSubject> studentSubjects, SchoolClass schoolClass) throws IOException {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=boletim.pdf");

        PdfWriter writer = new PdfWriter(response.getOutputStream());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);
        String[] sentences = {
                "Não há saber mais ou saber menos: há saberes diferentes.",
                "Educação não transforma o mundo. Educação muda pessoas. Pessoas transformam o mundo.",
                "Ninguém nasce feito, é experimentando-nos no mundo que nós nos fazemos.",
                "Ensinar não é transferir conhecimento, mas criar as possibilidades para a sua própria produção ou a sua construção.",
                "A educação, qualquer que seja ela, é sempre uma teoria do conhecimento posta em prática.",
                "A inclusão acontece quando se aprende com as diferenças e não com as igualdades."
        };
        String sentence = sentences[(int) (Math.random() * sentences.length)];
        Date now = new Date();

//        HEADER
        Paragraph title =
                new Paragraph("Boletim Escolar").setBold().setFontSize(18);

        ImageData imageData = ImageDataFactory.create(
               request.getServletContext().getRealPath("/assets/img/logo_pequena.png")
        );

        Image icon = new Image(imageData);
        icon.setWidth(80);

        float[] colunasHeader = {1, 3};
        Table headerTable = new Table(colunasHeader);
        headerTable.useAllAvailableWidth();

        headerTable.addCell(new Cell().add(icon).setBorder(Border.NO_BORDER));

        headerTable.addCell(
                new Cell()
                        .add(title)
                        .setVerticalAlignment(VerticalAlignment.MIDDLE)
                        .setBorder(Border.NO_BORDER)
        );

        document.add(headerTable);

//        INFORMAÇÕES GERAIS
        float[] colunasInfo = {1, 1};
        Table infoTable = new Table(colunasInfo);
        infoTable.useAllAvailableWidth();

        infoTable.addCell(
                infoCell("Aluno(a): " + OutputFormatService.formatName(student.getName()),
                        TextAlignment.LEFT).setFontSize(8)
        );

        infoTable.addCell(
                infoCell("Turma: " + OutputFormatService.formatName(schoolClass.getSchoolYear()),
                        TextAlignment.RIGHT).setFontSize(8)
        );

        infoTable.addCell(
                infoCell("Unidade: Colégio Vértice",
                        TextAlignment.LEFT).setFontSize(8)
        );

        infoTable.addCell(
                infoCell("EMISSÃO: "+ OutputFormatService.formatDate(now),
                        TextAlignment.RIGHT).setFontSize(8)
        );

        String situacao_final = "Aprovado";
        for (StudentSubject studentSubject: studentSubjects){
            if(studentSubject.getStatus().equals("Reprovado")){
                situacao_final = "Reprovado";
                break;
            }
            if(studentSubject.getStatus().equals("Pendente")){
                situacao_final = "Pendente";
                break;
            }
        }

        infoTable.addCell(
                new Cell()
                        .add(new Paragraph("SITUAÇÃO FINAL: "+situacao_final))
                        .setBorder(Border.NO_BORDER).setFontSize(10)
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setPadding(5).setBold()
        );

        infoTable.setBorder(new SolidBorder(ColorConstants.GRAY, 1));
        infoTable.setMarginTop(25);

        document.add(infoTable);

//      table
        float[] colunas = {4, 1, 1, 1, 1.5f};
        Table table = new Table(colunas);
        table.useAllAvailableWidth();
        table.setBorder(new SolidBorder(ColorConstants.GRAY, 1));
        table.setMarginTop(25);

        table.addHeaderCell("Disciplinas");
        table.addHeaderCell("1° Nota").setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell("2º Nota").setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell("Média Final").setTextAlignment(TextAlignment.CENTER);
        table.addHeaderCell("Situação").setTextAlignment(TextAlignment.CENTER);

        for (StudentSubject studentSubject: studentSubjects) {

            table.addCell(defaultCell(OutputFormatService.formatName(studentSubject.getSubject().getName()), TextAlignment.LEFT));

            table.addCell(gradeCell(studentSubject.getGrade1()));
            table.addCell(gradeCell(studentSubject.getGrade2()));
            table.addCell(gradeCell(studentSubject.getAverage()));

            String situation = studentSubject.getStatus();
            String color = "#808080";

            if(situation.equals("Reprovado")){
                color = "#BB1717";
            }else if (situation.equals("Aprovado")){
                color = "#17BB17";
            }

            table.addCell(new Cell()
                    .add(new Paragraph(situation).setFontSize(9))
                    .setTextAlignment(TextAlignment.CENTER)
                    .setVerticalAlignment(VerticalAlignment.MIDDLE)
                    .setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                    .setFontColor(WebColors.getRGBColor(color))
                    .setPadding(4));
        }

        document.add(table);

        float pageHeight = pdf.getDefaultPageSize().getHeight();
        float pageWidth = pdf.getDefaultPageSize().getWidth();
        float margin = 36f;

        Paragraph citation = new Paragraph()
                .add(new Text("Paulo Freire:\n").setBold())
                .add(new Text(String.format("\"%s\"", sentence)).setItalic())
                .setFontSize(8)
                .setFontColor(WebColors.getRGBColor("#888888"))
                .setFixedPosition(margin, margin, pageWidth - margin * 2)
                .setTextAlignment(TextAlignment.CENTER);

        document.add(citation);

        document.close();
    }

    private static Cell infoCell(String texto, TextAlignment alinhamento) {
        return new Cell()
                .add(new Paragraph(texto))
                .setFontSize(8)
                .setBorder(Border.NO_BORDER)
                .setTextAlignment(alinhamento)
                .setPadding(5);
    }

    private static Cell defaultCell(String texto, TextAlignment alinhamento) {
        return new Cell()
                .add(new Paragraph(texto).setFontSize(9))
                .setTextAlignment(alinhamento)
                .setVerticalAlignment(VerticalAlignment.MIDDLE)
                .setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                .setPadding(4);
    }

    private static Cell gradeCell(Double grade) {

        Paragraph p;

        if (grade == null) {
            p = new Paragraph("-").setFontSize(9);
        } else {
            Text t = new Text(String.format("%.2f", grade))
                    .setFontSize(9);

            if (grade < Constants.MIN_GRADE_TO_BE_APPROVAL) {
                t.setFontColor(ColorConstants.RED);
            }

            p = new Paragraph().add(t);
        }

        return new Cell()
                .add(p)
                .setTextAlignment(TextAlignment.CENTER)
                .setBorder(new SolidBorder(ColorConstants.GRAY, 1))
                .setPadding(4);
    }
}
