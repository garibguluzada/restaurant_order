package com.orderme.backend.order;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "orders")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer orderid;

    private String tableid;

    private String status;

    private BigDecimal totalcost;

    private String mealname;

    @Column(name = "createdat", columnDefinition = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdat = LocalDateTime.now();

    // Getters and Setters

    public Integer getOrderid() {
        return orderid;
    }

    public void setOrderid(Integer orderid) {
        this.orderid = orderid;
    }

    public String getTableid() {
        return tableid;
    }

    public void setTableid(String tableid) {
        this.tableid = tableid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTotalcost() {
        return totalcost;
    }

    public void setTotalcost(BigDecimal totalcost) {
        this.totalcost = totalcost;
    }

    public String getMealname() {
        return mealname;
    }

    public void setMealname(String mealname) {
        this.mealname = mealname;
    }

    public LocalDateTime getCreatedat() {
        return createdat;
    }

    public void setCreatedat(LocalDateTime createdat) {
        this.createdat = createdat;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(orderid, order.orderid) &&
               Objects.equals(tableid, order.tableid) &&
               Objects.equals(status, order.status) &&
               Objects.equals(totalcost, order.totalcost) &&
               Objects.equals(mealname, order.mealname) &&
               Objects.equals(createdat, order.createdat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orderid, tableid, status, totalcost, mealname, createdat);
    }
}
