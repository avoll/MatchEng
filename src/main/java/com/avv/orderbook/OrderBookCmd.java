package com.avv.orderbook;

import org.springframework.web.context.request.async.DeferredResult;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Objects;

/**
 * Command pattern value object for Disruptor's Ring
 *
 * There are many improvements possible, please consider this a working prototype.
 *
 * @author Alexander Voll
 */

public class OrderBookCmd extends PriceQtyPair {
    protected CmdType cmdType = CmdType.UNSET;
    protected DeferredResult<String>  deferredResult = null;

    public OrderBookCmd() {}
    public OrderBookCmd(CmdType cmdType, double price, int qty) {
        set(cmdType, price, qty);
    }

    @JsonIgnore
    public CmdType getCmdType() {
        return cmdType;
    }

    @JsonIgnore
    public DeferredResult<String> getDeferredResult() {
        return deferredResult;
    }

    @java.lang.Override
    public java.lang.String toString() {
        return "OrderBookCmd(" +
                "cmdType=" + cmdType +
                ", qty=" + qty +
                ", price=" + price +
                ')';
    }

    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        if (!super.equals(object)) return false;
        OrderBookCmd that = (OrderBookCmd) object;
        return qty == that.qty &&
                Double.compare(that.price, price) == 0 &&
                cmdType == that.cmdType;
    }

    public int hashCode() {
        return Objects.hash(super.hashCode(), cmdType);
    }

    public void set(CmdType cmdType, double price, int qty) {
        set(cmdType, price, qty, null);
    }
    public void set(CmdType cmdType, double price, int qty, DeferredResult<String>  deferredResult) {
        super.set(price, qty);
        this.cmdType = cmdType;
        this.deferredResult = deferredResult;
    }

    public void set(CmdType cmdType, PriceQtyPair priceQty) {
        set(cmdType, priceQty, null);
    }
    public void set(CmdType cmdType, PriceQtyPair priceQty, DeferredResult<String>  deferredResult) {
        super.set(priceQty);
        this.cmdType = cmdType;
        this.deferredResult = deferredResult;
    }
}