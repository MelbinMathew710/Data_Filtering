package com.example.filtering_data;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class MyCamelRoute extends RouteBuilder {

    @Value("${pageNo}")
    private Integer pageNo;

    @Value("${ids}")
    private String ids;

    @Override
    public void configure() throws Exception {
        rest()
                .get("/filter-data")
                .produces("application/json")
                .to("direct:start");

        from("direct:start")
                .setHeader("CamelHttpMethod", constant("GET"))
                .toD("https://reqres.in/api/users?page=${property.pageNo}")
                .unmarshal().json()
                .filter().method(this, "filterIds")
                .marshal().json(true)
                .convertBodyTo(String.class)
                .log("Filtered data: ${body}");
    }

    public boolean filterIds(String jsonPath) {
        String[] idArray = ids.split(",");
        for (String id : idArray) {
            if (jsonPath.contains("\"id\":" + id)) {
                return true;
            }
        }
        return false;
    }
}