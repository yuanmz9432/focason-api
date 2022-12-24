package com.lemonico.core.props;



import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = PathProps.PREFIX)
public class PathProps
{
    public static final String PREFIX = "path";

    private String root;
    private String image;
    private String logo;
    private String store;
    private String wms;
    private String temporary;
    private String order;
    private String ntmOrder;
    private String yahoo;
    private String font;
}
