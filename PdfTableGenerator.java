import com.zmg.panda.utils.pdfbox.PdfBoxUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Andy
 */
public class PdfTableGenerator {

    /**
     * 生成table，无main业务首页
     * @param document
     * @param table
     * @throws IOException
     */
    public void generatePDF(PDDocument document, Table table) throws IOException{
        // 每页的行数
        Integer rowsPerPage = table.getRowsPerPage();
        // 计算需要多少页
        int numberOfPages = new Double(Math.ceil(table.getNumberOfRows().floatValue() / rowsPerPage)).intValue();

        // 生成每一页
        generateEachPage(document, table, rowsPerPage, numberOfPages);
    }

    /**
     * 生成table，含main业务首页
     * @param doc
     * @param firstTablePage main业务首页
     * @param table
     * @throws IOException
     */
    public void drawTableCustom(PDDocument doc, FirstTablePage firstTablePage, Table table) throws IOException {
        // 处理第一页是和业务相关，非独立的
        if (firstTablePage != null) {
            handleMainPage(firstTablePage, table);
        }
        // 每页的行数
        int rowsPerPage = table.getRowsPerPage();
        // 计算需要多少页
        int numberOfPages = new Double(Math.ceil(table.getNumberOfRows().floatValue() / rowsPerPage)).intValue();
        // 剩下的的页
        generateEachPage(doc, table, rowsPerPage, numberOfPages);
    }

    /**
     * 处理pdf拥有table的第一页
     * @param firstTablePage
     * @param table
     * @throws IOException
     */
    private void handleMainPage(FirstTablePage firstTablePage, Table table) throws IOException {
        Integer dataNum = firstTablePage.getDataNum();
        PDPageContentStream contentStream = firstTablePage.getContentStream();
        contentStream.setFont(table.getTextFont(), table.getFontSize());
        List<List<String>> content = table.getRecords();
        dataNum = dataNum > content.size() ? content.size() : dataNum;
        List<List<String>> firstPageContent = new ArrayList<>(dataNum);
        Iterator<List<String>> iterator = content.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            List<String> next = iterator.next();
            firstPageContent.add(next);
            iterator.remove();
            index ++;
            if (index >= dataNum) {
                break;
            }
        }
        table.setRecords(content);
        drawFirstCurrentPage(table, firstPageContent, contentStream, firstTablePage.getMargin());
    }

    /**
     * 遍历自动生成page
     * @param doc
     * @param table
     * @param rowsPerPage
     * @param numberOfPages
     * @throws IOException
     */
    private void generateEachPage(PDDocument doc, Table table, Integer rowsPerPage, int numberOfPages) throws IOException {
        for (int pageCount = 0; pageCount < numberOfPages; pageCount++) {
            PDPage page = generatePage(doc, table);
            PDPageContentStream contentStream = generateContentStream(doc, page, table);
            List<List<String>> currentPageContent = getContentForCurrentPage(table, rowsPerPage, pageCount);
            drawCurrentPage(table, currentPageContent, contentStream);
        }
    }

    /**
     * 写页面
     * @param table
     * @param currentPageContent
     * @param contentStream
     * @throws IOException
     */
    private void drawCurrentPage(Table table, List<List<String>> currentPageContent, PDPageContentStream contentStream)
            throws IOException {
        float tableTopY = table.getPageSize().getHeight() - table.getMargin();
        drawPage(table, currentPageContent, contentStream, tableTopY);
    }

    /**
     * 在页面中写入table
     * @param table
     * @param currentPageContent
     * @param contentStream
     * @param tableTopY
     * @throws IOException
     */
    private void drawPage(Table table, List<List<String>> currentPageContent, PDPageContentStream contentStream, float tableTopY) throws IOException {
        // 给table画网格
        drawTableGrid(table, currentPageContent, contentStream, tableTopY);

        // 游标开始点
        float nextTextX = table.getMargin() + table.getCellMargin();
        // 考虑字体高度计算单元格中文本的中心对齐方式
        float nextTextY = tableTopY - (table.getRowHeight() / 2)
                - ((table.getTextFont().getFontDescriptor().getFontBoundingBox().getHeight() / 1000 * table.getFontSize()) / 4);

        // 写入table的表头
        writeContentLine(table.getColumnsNamesAsArray(), contentStream, nextTextX, nextTextY, table);
        nextTextY -= table.getRowHeight();
        nextTextX = table.getMargin() + table.getCellMargin();

        // 写入表数据
        for (int i = 0; i < currentPageContent.size(); i++) {
            writeContentLine(currentPageContent.get(i), contentStream, nextTextX, nextTextY, table);
            nextTextY -= table.getRowHeight();
            nextTextX = table.getMargin() + table.getCellMargin();
        }

        contentStream.close();
    }

    /**
     * 写入含有业务的第一页数据
     * @param table
     * @param currentPageContent
     * @param contentStream
     * @throws IOException
     */
    private void drawFirstCurrentPage(Table table, List<List<String>> currentPageContent, PDPageContentStream contentStream, Float margin)
            throws IOException {
        float tableTopY = table.getPageSize().getHeight() - table.getMargin() - margin;

        // 在页面中写入table
        drawPage(table, currentPageContent, contentStream, tableTopY);
    }

    /**
     * 为table每一行写入数据
     * @param lineContent
     * @param contentStream
     * @param nextTextX
     * @param nextTextY
     * @param table
     * @throws IOException
     */
    private void writeContentLine(List<String> lineContent, PDPageContentStream contentStream, float nextTextX, float nextTextY,
                                  Table table) throws IOException {
        for (int i = 0; i < table.getNumberOfColumns(); i++) {
            String text = lineContent.get(i);
            contentStream.beginText();
            contentStream.newLineAtOffset(nextTextX, nextTextY);
            contentStream.showText(text != null ? text : "");
            contentStream.endText();
            nextTextX += table.getHeader().get(i).getWidth();
        }
    }

    /**
     * 画页面中的table网格
     * @param table
     * @param currentPageContent
     * @param contentStream
     * @param tableTopY
     * @throws IOException
     */
    private void drawTableGrid(Table table, List<List<String>> currentPageContent, PDPageContentStream contentStream, float tableTopY)
            throws IOException {
        // 画行线
        float nextY = tableTopY;
        for (int i = 0; i <= currentPageContent.size() + 1; i++) {
            PdfBoxUtils.drawLine(contentStream, table.getMargin(), nextY, table.getMargin() + table.getWidth(), nextY);
            nextY -= table.getRowHeight();
        }

        // 画列线
        final float tableYLength = table.getRowHeight() + (table.getRowHeight() * currentPageContent.size());
        final float tableBottomY = tableTopY - tableYLength;
        float nextX = table.getMargin();
        for (int i = 0; i < table.getNumberOfColumns(); i++) {
            PdfBoxUtils.drawLine(contentStream, nextX, tableTopY, nextX, tableBottomY);
            nextX += table.getHeader().get(i).getWidth();
        }
        PdfBoxUtils.drawLine(contentStream, nextX, tableTopY, nextX, tableBottomY);
    }

    /**
     * 获取page中需要展示的数据行
     * @param table
     * @param rowsPerPage
     * @param pageCount
     * @return
     */
    private List<List<String>> getContentForCurrentPage(Table table, Integer rowsPerPage, int pageCount) {
        int startRange = pageCount * rowsPerPage;
        int endRange = (pageCount * rowsPerPage) + rowsPerPage;
        if (endRange > table.getNumberOfRows()) {
            endRange = table.getNumberOfRows();
        }
        List<List<String>> content = table.getRecords();
        List<List<String>> result = new ArrayList<>(endRange - startRange);
        for (int i = startRange; i < endRange; i ++){
            result.add(content.get(i));
        }
        return result;
    }

    /**
     * 生成page
     * @param doc
     * @param table
     * @return
     */
    private PDPage generatePage(PDDocument doc, Table table) {
        PDPage page = new PDPage(table.getPageSize());
        doc.addPage(page);
        return page;
    }

    /**
     * 生成页面画笔输出流
     * @param doc
     * @param page
     * @param table
     * @return
     * @throws IOException
     */
    private PDPageContentStream generateContentStream(PDDocument doc, PDPage page, Table table) throws IOException {
        PDPageContentStream contentStream = new PDPageContentStream(doc, page);
        contentStream.setFont(table.getTextFont(), table.getFontSize());
        return contentStream;
    }
}
