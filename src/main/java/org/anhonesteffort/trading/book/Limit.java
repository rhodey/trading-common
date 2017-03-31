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

package org.anhonesteffort.trading.book;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

public class Limit {

  private final Map<String, Order> orderMap = new HashMap<>();
  private final Queue<Order> orderQueue;
  private final double price;
  private double volume;

  public Limit(double price, int initSize) {
    orderQueue  = new ArrayDeque<>(initSize);
    this.price  = price;
    this.volume = 0d;
  }

  public double getPrice() {
    return price;
  }

  public double getVolume() {
    return volume;
  }

  public Optional<Order> peek() {
    return Optional.ofNullable(orderQueue.peek());
  }

  public void add(Order order) {
    orderMap.put(order.getOrderId(), order);
    orderQueue.add(order);
    volume += order.getSizeRemaining();
  }

  public Optional<Order> remove(String orderId) {
    Optional<Order> order = Optional.ofNullable(orderMap.remove(orderId));
    if (order.isPresent()) {
      orderQueue.remove(order.get());
      volume -= order.get().getSizeRemaining();
    }
    return order;
  }

  public Optional<Order> reduce(String orderId, double size) {
    Optional<Order> order = Optional.ofNullable(orderMap.get(orderId));
    if (order.isPresent()) {
      order.get().subtract(size, price);
      volume -= size;
      if (order.get().getSizeRemaining() <= 0d) {
        orderMap.remove(orderId);
        orderQueue.remove(order.get());
      }
    }
    return order;
  }

  private double getTakeSize(Order taker) {
    if (taker instanceof MarketOrder) {
      return ((MarketOrder) taker).getSizeRemainingFor(price);
    } else {
      return taker.getSizeRemaining();
    }
  }

  private Optional<Order> takeLiquidityFromNextMaker(Order taker, double takeSize) {
    Optional<Order> maker = Optional.ofNullable(orderQueue.peek());
    if (maker.isPresent()) {
      double volumeRemoved = maker.get().takeSize(takeSize);

      if (maker.get().getSizeRemaining() <= 0d) {
        orderMap.remove(maker.get().getOrderId());
        orderQueue.remove();
      }

      volume -= volumeRemoved;
      taker.subtract(volumeRemoved, maker.get().getPrice());
    }
    return maker;
  }

  public List<Order> takeLiquidity(Order taker) {
    List<Order>     makers   = new ArrayList<>();
    double          takeSize = getTakeSize(taker);
    Optional<Order> maker    = null;

    while (takeSize > 0d) {
      maker = takeLiquidityFromNextMaker(taker, takeSize);
      if (maker.isPresent()) {
        makers.add(maker.get());
        takeSize = getTakeSize(taker);
      } else {
        break;
      }
    }

    return makers;
  }

  public void clear() {
    orderQueue.clear();
    orderMap.clear();
    volume = 0d;
  }

}
