/*
 * Copyright (C) 2016 An Honest Effort LLC.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.anhonesteffort.btc.book;

public class Order {

  public enum Side { ASK, BID }

  protected final String orderId;
  protected final Side   side;
  protected final long   price;
  protected final long   size;
  protected       long   sizeRemaining;
  protected       long   valueRemoved;

  public Order(String orderId, Side side, long price, long size) {
    this.orderId       = orderId;
    this.side          = side;
    this.price         = price;
    this.size          = size;
    this.sizeRemaining = size;
    this.valueRemoved  = 0l;
  }

  public String getOrderId() {
    return orderId;
  }

  public Side getSide() {
    return side;
  }

  public long getPrice() {
    return price;
  }

  public long getSize() {
    return size;
  }

  public long getSizeRemaining() {
    return sizeRemaining;
  }

  public long getValueRemoved() {
    return valueRemoved;
  }

  public void clearValueRemoved() {
    this.valueRemoved = 0l;
  }

  protected void subtract(long size, long price) {
    sizeRemaining -= size;
  }

  public long takeSize(long size) {
    long taken     = Math.min(size, sizeRemaining);
    sizeRemaining -= taken;
    valueRemoved  += price * taken;
    return taken;
  }

}
