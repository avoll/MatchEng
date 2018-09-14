package com.avv.orderbook;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;

/**
 * The price and quantity basic request value object
 *
 * There are many improvements possible, please consider this a working prototype.
 *
 * @author Alexander Voll
 */

public class PriceQtyPair {
    @JsonProperty("qty")
    protected int qty = 0;
    @JsonProperty("prc")
    protected double price = 0.0;

    public PriceQtyPair() {}

    public PriceQtyPair(@JsonProperty("prc") double price, @JsonProperty("qty") int qty) {
        this.qty = qty;
        this.price = price;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "PriceQtyPair(" +
                "qty=" + qty +
                " @ price=" + price +
                ')';
    }

    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        PriceQtyPair that = (PriceQtyPair) object;
        return qty == that.qty &&
                Double.compare(that.price, price) == 0;
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), price, qty);
    }

    public void set(double price, int qty) {
        this.qty = qty;
        this.price = price;
    }
    public void set(PriceQtyPair priceQty) {
        this.qty = priceQty.qty;
        this.price = priceQty.price;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}