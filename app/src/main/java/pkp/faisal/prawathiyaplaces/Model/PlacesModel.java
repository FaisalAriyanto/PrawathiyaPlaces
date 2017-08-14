package pkp.faisal.prawathiyaplaces.Model;

/**
 * Created by Faisal on 5/9/2017.
 */

public class PlacesModel {
    public String Uid;
    public String PlaceId;
    public String Name;
    public String Vicinity;
    public String PhotoUrl;
    public String MasterCategoryId;
    public String Lat;
    public String Lon;
    public String Category;

    public PlacesModel() {

    }

    public PlacesModel(
            String Name,
            String Vicinity,
            String PhotoUrl,
            String MasterCategoryId,
            String Lat,
            String Lon
    ) {
        this.Name = Name;
        this.Vicinity = Vicinity;
        this.PhotoUrl = PhotoUrl;
        this.MasterCategoryId = MasterCategoryId;
        this.Lat = Lat;
        this.Lon = Lon;
    }

}
