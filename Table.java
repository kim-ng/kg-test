import lombok.Data;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Panda
 */
@Data
public class Table {
    /**
     * Table 位置
     */
    private float margin;
    private float height;
    private PDRectangle pageSize;
    private float rowHeight;

    /**
     * table 字体
     */
    private PDFont textFont;
    private float fontSize;

    /**
     * table 内容
     */
    private Integer numberOfRows;
    private List<Column> header;
    private List<List<String>> records;
    private float cellMargin;


    public float getWidth() {
        float tableWidth = 0f;
        for (Column column : header) {
            tableWidth += column.getWidth();
        }
        return tableWidth;
    }

    public List<String> getColumnsNamesAsArray() {
        List<String> columnNames = new ArrayList<>(getNumberOfColumns());
        header.forEach(e -> columnNames.add(e.getName()));
        return columnNames;
    }

    public Integer getNumberOfColumns() {
        return this.getHeader().size();
    }

    public Integer getNumberOfRows() {
        return this.records.size();
    }

    /**
     * 获取page显示多少行数据
     * @return
     */
    public Integer getRowsPerPage() {
        return new Double(Math.floor(this.getHeight() / this.getRowHeight())).intValue() - 1;
    }
}