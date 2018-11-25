package com.baidu;

import org.apache.solr.client.solrj.beans.Field;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @auther SyntacticSugar
 * @data 2018/11/25 0025上午 9:26
 */
public class Item implements Serializable {
    private static final long serialVersionUID=1L;
    @Field("id")
    private String id;
    @Field("title")
    private String title;
    @Field("price")
    private Float  price;
    //


    @Override
    public String toString() {
        return "Item{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", price=" + price +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Float getPrice() {
        return price;
    }

    public void setPrice(Float price) {
        this.price = price;
    }
}
