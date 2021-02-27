import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Panda
 */
@Data
@AllArgsConstructor
public class Column {
    private String name;
    private float width;
}