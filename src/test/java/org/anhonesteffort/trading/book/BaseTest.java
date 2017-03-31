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

public class BaseTest {

  private Integer nextOrderId = 0;

  protected Order newAsk(String orderId, double price, double size) {
    return new Order(orderId, Order.Side.ASK, price, size);
  }

  protected Order newAsk(double price, double size) {
    return newAsk((nextOrderId++).toString(), price, size);
  }

  protected MarketOrder newMarketAsk(String orderId, double size, double funds) {
    return new MarketOrder(orderId, Order.Side.ASK, size, funds);
  }

  protected MarketOrder newMarketAsk(double size, double funds) {
    return newMarketAsk((nextOrderId++).toString(), size, funds);
  }

  protected Order newBid(String orderId, double price, double size) {
    return new Order(orderId, Order.Side.BID, price, size);
  }

  protected Order newBid(double price, double size) {
    return newBid((nextOrderId++).toString(), price, size);
  }

  protected MarketOrder newMarketBid(String orderId, double size, double funds) {
    return new MarketOrder(orderId, Order.Side.BID, size, funds);
  }

  protected MarketOrder newMarketBid(double size, double funds) {
    return newMarketBid((nextOrderId++).toString(), size, funds);
  }

}
