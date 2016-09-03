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

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class LimitOrderBook {

  protected final LimitQueue askLimits;
  protected final LimitQueue bidLimits;

  public LimitOrderBook(int initLimitSize) {
    askLimits = new LimitQueue(Order.Side.ASK, initLimitSize);
    bidLimits = new LimitQueue(Order.Side.BID, initLimitSize);
  }

  private List<Order> processAsk(Order ask) {
    List<Order> makers = new LinkedList<>();
    List<Order> next   = bidLimits.takeLiquidityFromBestLimit(ask);

    while (!next.isEmpty()) {
      makers.addAll(next);
      next = bidLimits.takeLiquidityFromBestLimit(ask);
    }

    if (ask.getSizeRemaining() > 0l && !(ask instanceof MarketOrder)) {
      askLimits.addOrder(ask);
    }

    return makers;
  }

  private List<Order> processBid(Order bid) {
    List<Order> makers = new LinkedList<>();
    List<Order> next   = askLimits.takeLiquidityFromBestLimit(bid);

    while (!next.isEmpty()) {
      makers.addAll(next);
      next = askLimits.takeLiquidityFromBestLimit(bid);
    }

    if (bid.getSizeRemaining() > 0l && !(bid instanceof MarketOrder)) {
      bidLimits.addOrder(bid);
    }

    return makers;
  }

  public LimitQueue getAskLimits() {
    return askLimits;
  }

  public LimitQueue getBidLimits() {
    return bidLimits;
  }

  private TakeResult resultFor(Order taker, List<Order> makers, long takeSize) {
    if (!(taker instanceof MarketOrder)) {
      return new TakeResult(taker, makers, (takeSize - taker.getSizeRemaining()));
    } else {
      return new TakeResult(taker, makers, ((MarketOrder) taker).getVolumeRemoved());
    }
  }

  public TakeResult add(Order taker) {
    long        takeSize = taker.getSizeRemaining();
    List<Order> makers   = null;

    if (taker.getSide().equals(Order.Side.ASK)) {
      makers = processAsk(taker);
    } else {
      makers = processBid(taker);
    }

    return resultFor(taker, makers, takeSize);
  }

  public Optional<Order> remove(Order.Side side, Long price, String orderId) {
    if (side.equals(Order.Side.ASK)) {
      return askLimits.removeOrder(price, orderId);
    } else {
      return bidLimits.removeOrder(price, orderId);
    }
  }

  public Optional<Order> reduce(Order.Side side, Long price, String orderId, long size) {
    if (side.equals(Order.Side.ASK)) {
      return askLimits.reduceOrder(price, orderId, size);
    } else {
      return bidLimits.reduceOrder(price, orderId, size);
    }
  }

  public void clear() {
    askLimits.clear();
    bidLimits.clear();
  }

}
