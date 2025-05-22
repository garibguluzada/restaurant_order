package com.orderme.backend.table;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import java.util.Objects;

@Entity
@Table(name = "_tables")
public class RestaurantTable {
    @Id
    private String tableid;
    
    @Column(name = "tablenum")
    private Integer tablenum;

    public String getTableid() {
        return tableid;
    }

    public void setTableid(String tableid) {
        this.tableid = tableid;
    }

    public Integer getTablenum() {
        return tablenum;
    }

    public void setTablenum(Integer tablenum) {
        this.tablenum = tablenum;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RestaurantTable that = (RestaurantTable) o;
        return Objects.equals(tableid, that.tableid) &&
               Objects.equals(tablenum, that.tablenum);
    }

    @Override
    public int hashCode() {
        return Objects.hash(tableid, tablenum);
    }
} 