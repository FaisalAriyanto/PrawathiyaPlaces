package pkp.faisal.prawathiyaplaces.Model;

/**
 * Created by Faisal on 5/9/2017.
 */

public class CategoryModel {
    public String Uid;
    public String Text;
    public String Value;

    public CategoryModel(){}

    public CategoryModel(String Text,
                         String Value) {
        this.Text = Text;
        this.Value = Value;
    }
}
