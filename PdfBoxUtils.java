public class PdfBoxUtils {

    /**
     * 插入一张图片
     * @param document
     * @param contentStream 输出流
     * @param imgFile 图片文件
     * @param xStart x主标
     * @param yStart y主标
     * @param width 图片宽
     * @param hight 图片高
     * @throws IOException
     */
    public static void drawImage(PDDocument document, PDPageContentStream contentStream, File imgFile, float xStart, float yStart, float width, float hight) throws IOException {
        PDImageXObject pdImage = PDImageXObject.createFromFileByContent(imgFile, document);
        contentStream.drawImage(pdImage, xStart, yStart, width, hight );
    }


    /**
     * 画一条线
     * @param contentStream
     * @param xStart
     * @param yStart
     * @param xEnd
     * @param yEnd
     * @throws IOException
     */
    public static void drawLine(PDPageContentStream contentStream, float xStart, float yStart, float xEnd, float yEnd) throws IOException {
        contentStream.moveTo(xStart, yStart);
        contentStream.lineTo(xEnd, yEnd);
        contentStream.stroke();
    }

    /**
     * 定义文本输出流开始
     * @param contentStream
     * @param leading 行距
     * @param offSetX 第一行的x坐标间距
     * @param offSetY 第一行的y主表间距
     * @throws IOException
     */
    public static void beginTextSteam(PDPageContentStream contentStream, Float leading, Float offSetX, Float offSetY) throws IOException {
        // 输出流开始
        contentStream.beginText();
        // 行间距
        contentStream.setLeading(leading);
        // 书写行定位
        contentStream.newLineAtOffset(offSetX, offSetY);
    }

    /**
     * 定义文本输出流结束
     * @param contentStream
     * @throws IOException
     */
    public static void endTextSteam(PDPageContentStream contentStream) throws IOException {
        contentStream.endText();
    }

    /**
     * 创建一定数量的空行
     * @param contentStream
     * @param emptyNum
     * @throws IOException
     */
    public static void createEmptyParagraph(PDPageContentStream contentStream, Integer emptyNum) throws IOException {
        for (int i = 0; i < emptyNum; i++) {
            contentStream.newLine();
        }
    }


    /**
     * 写一个段落
     * @param contentStream
     * @param text
     * @throws IOException
     */
    public static void drawParagraph(PDPageContentStream contentStream, String text) throws IOException {
        contentStream.showText(text);
        contentStream.newLine();
    }

    /**
     * 写一个段落并设定字体
     * @param contentStream
     * @param text
     * @throws IOException
     */
    public static void drawParagraph(PDPageContentStream contentStream, String text, PDFont font, Integer fontSize) throws IOException {
        contentStream.setFont(font, fontSize);
        contentStream.showText(text);
        contentStream.newLine();
    }


    /**
     * 将html转为pdf文件（中文）
     * <p>采用的是openhtmltopdf插件</p>
     * <p>注意html中每一个标签必须要有结尾标签</p>
     * <p>如果要解决中文乱码问题，请在<head></head>标签中加上css样式
     * <style>
     *  *{
     *      font-family: arialuni(这里填上对应的中文字体名)
     *        }
     * </style></p>
     * @param outputStream pdf文件输出流
     * @param htmlFile html文件
     * @param fontInputStream 字体文件输出流（自定义字体文件，解决中文乱码问题）如：simsun.tff(宋体)
     * @param fontFamily 字体名，如：simsun(宋体)
     * @throws IOException
     */
    public static void convertHtmlToPdf(OutputStream outputStream, File htmlFile, InputStream fontInputStream, String fontFamily) throws IOException {
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.useFont(setFSFont(fontInputStream), fontFamily);
        builder.withFile(htmlFile);
        builder.toStream(outputStream);
        builder.run();
    }

    /**
     * 将html转为pdf文件（中文）
     * <p>采用的是openhtmltopdf插件</p>
     * <p>注意html中每一个标签必须要有结尾标签</p>
     * <p>如果要解决中文乱码问题，请在<head></head>标签中加上css样式
     * <style>
     *  *{
     *      font-family: arialuni(这里填上对应的中文字体名)
     *        }
     * </style></p>
     * @param outputStream pdf文件输出流
     * @param htmlFileStr Provides a string containing XHTML/XML to convert to PDF.
     * @param fontInputStream 字体文件输出流（自定义字体文件，解决中文乱码问题）如：simsun.tff(宋体)
     * @param fontFamily 字体名，如：simsun(宋体)
     * @throws IOException
     */
    public static void convertHtmlToPdf(OutputStream outputStream, String htmlFileStr, InputStream fontInputStream, String fontFamily) throws IOException {
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.useFont(setFSFont(fontInputStream), fontFamily);
        builder.withHtmlContent(htmlFileStr, null);
        builder.toStream(outputStream);
        builder.run();
    }

    /**
     * 将html转为pdf文件
     * <p>采用的是openhtmltopdf插件</p>
     * <p>注意html中每一个标签必须要有结尾标签</p>
     * @param outputStream pdf文件输出流
     * @param htmlFile html文件
     * @throws IOException
     */
    public static void convertHtmlToPdf(OutputStream outputStream, File htmlFile) throws IOException {
        PdfRendererBuilder builder = new PdfRendererBuilder();
        builder.useFastMode();
        builder.withFile(htmlFile);
        builder.toStream(outputStream);
        builder.run();
    }

    /**
     * 返回自定义字体
     * @param fontInputStream
     * @return
     */
    private static FSSupplier<InputStream> setFSFont(InputStream fontInputStream) {
        return () -> fontInputStream;
    }