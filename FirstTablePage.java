import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;

/**
 * @author Andy
 */
@Data
public class FirstTablePage {

    private PDPage firstPdPage;

    @ApiModelProperty("第一页显示的数据条数")
    private Integer dataNum;

    private Float margin;

    private PDPageContentStream contentStream;

}
