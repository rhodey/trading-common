package org.anhonesteffort.trading.book;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;

public class OrderEvent implements Externalizable {

  public enum Type {
    OPEN, TAKE, REDUCE, SYNC_START, SYNC_END
  }

  protected Type type;
  protected String orderId;
  protected Order.Side side;
  protected long price;
  protected long size;

  public OrderEvent() { }

  public OrderEvent(Type type, String orderId, Order.Side side, long price, long size) {
    this.type    = type;
    this.orderId = orderId;
    this.side    = side;
    this.price   = price;
    this.size    = size;
  }

  public static OrderEvent open(Order order) {
    return new OrderEvent(Type.OPEN, order.getOrderId(), order.getSide(), order.getPrice(), order.getSize());
  }

  public static OrderEvent take(Order order) {
    return new OrderEvent(Type.TAKE, order.getOrderId(), order.getSide(), order.getPrice(), order.getSize());
  }

  public static OrderEvent reduce(Order order, long reduceBy) {
    return new OrderEvent(Type.REDUCE, order.getOrderId(), order.getSide(), order.getPrice(), reduceBy);
  }

  public static OrderEvent cancel(Order order) {
    return new OrderEvent(Type.REDUCE, order.getOrderId(), order.getSide(), order.getPrice(), order.getSize());
  }

  public static OrderEvent syncStart() {
    return new OrderEvent(Type.SYNC_START, "", Order.Side.ASK, -1l, -1l);
  }

  public static OrderEvent syncEnd() {
    return new OrderEvent(Type.SYNC_END, "", Order.Side.ASK, -1l, -1l);
  }

  public Type getType() {
    return type;
  }

  public String getOrderId() {
    return orderId;
  }

  public Order.Side getSide() {
    return side;
  }

  public long getPrice() {
    return price;
  }

  public long getSize() {
    return size;
  }

  @Override
  public void writeExternal(ObjectOutput out) throws IOException {
    switch (type) {
      case OPEN:
        out.writeInt(0);
        break;

      case TAKE:
        out.writeInt(1);
        break;

      case REDUCE:
        out.writeInt(2);
        break;

      case SYNC_START:
        out.writeInt(3);
        break;

      case SYNC_END:
        out.writeInt(4);
        break;
    }

    out.writeUTF(orderId);
    out.writeInt(side == Order.Side.ASK ? 0 : 1);
    out.writeLong(price);
    out.writeLong(size);
  }

  @Override
  public void readExternal(ObjectInput in) throws IOException {
    switch (in.readInt()) {
      case 0:
        type = Type.OPEN;
        break;

      case 1:
        type = Type.TAKE;
        break;

      case 2:
        type = Type.REDUCE;
        break;

      case 3:
        type = Type.SYNC_START;
        break;

      case 4:
        type = Type.SYNC_END;
        break;
    }

    orderId = in.readUTF();
    side    = (in.readInt() == 0) ? Order.Side.ASK : Order.Side.BID;
    price   = in.readLong();
    size    = in.readLong();
  }

}
