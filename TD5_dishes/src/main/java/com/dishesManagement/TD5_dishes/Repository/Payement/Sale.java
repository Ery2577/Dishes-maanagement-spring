package com.dishesManagement.TD5_dishes.Repository.Payement;

import com.dishesManagement.TD5_dishes.Entity.Order;

import java.time.Instant;

public class Sale { private Integer id;
    private Instant creationDatetime;
    private Order order;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Instant getCreationDatetime() {
        return creationDatetime;
    }

    public void setCreationDatetime(Instant creationDatetime) {
        this.creationDatetime = creationDatetime;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "id=" + id +
                ", creationDatetime=" + creationDatetime +
                ", order=" + order +
                '}';
    }

}
