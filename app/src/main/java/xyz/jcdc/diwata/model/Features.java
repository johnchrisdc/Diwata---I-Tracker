package xyz.jcdc.diwata.model;

/**
 * Created by jcdc on 4/22/17.
 */

public class Features {

    private Properties properties;

    private String type;

    private Geometry geometry;

    public Properties getProperties ()
    {
        return properties;
    }

    public void setProperties (Properties properties)
    {
        this.properties = properties;
    }

    public String getType ()
    {
        return type;
    }

    public void setType (String type)
    {
        this.type = type;
    }

    public Geometry getGeometry ()
    {
        return geometry;
    }

    public void setGeometry (Geometry geometry)
    {
        this.geometry = geometry;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [properties = "+properties+", type = "+type+", geometry = "+geometry+"]";
    }

}
