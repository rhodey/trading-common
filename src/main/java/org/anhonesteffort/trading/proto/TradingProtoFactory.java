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

package org.anhonesteffort.trading.proto;

import org.anhonesteffort.trading.book.Order;
import org.anhonesteffort.trading.book.OrderEvent;

import static org.anhonesteffort.trading.proto.TradingProto.BaseMessage;
import static org.anhonesteffort.trading.proto.TradingProto.Error;

public class TradingProtoFactory {

  public BaseMessage error(String message) {
    return BaseMessage.newBuilder()
        .setType(BaseMessage.Type.ERROR)
        .setError(Error.newBuilder().setMessage(message))
        .build();
  }

  private TradingProto.OrderEvent.Type typeFor(OrderEvent.Type type) {
    if (type == OrderEvent.Type.OPEN) {
      return TradingProto.OrderEvent.Type.OPEN;
    } else if (type == OrderEvent.Type.TAKE) {
      return TradingProto.OrderEvent.Type.TAKE;
    } else {
      return TradingProto.OrderEvent.Type.REDUCE;
    }
  }

  private TradingProto.OrderEvent.Side sideFor(Order.Side side) {
    if (side == Order.Side.ASK) {
      return TradingProto.OrderEvent.Side.ASK;
    } else {
      return TradingProto.OrderEvent.Side.BID;
    }
  }

  public BaseMessage orderEvent(OrderEvent event) {
    return BaseMessage.newBuilder()
        .setType(BaseMessage.Type.ORDER_EVENT)
        .setOrderEvent(TradingProto.OrderEvent.newBuilder()
                .setType(typeFor(event.getType()))
                .setOrderId(event.getOrderId())
                .setSide(sideFor(event.getSide()))
                .setPrice(event.getPrice())
                .setSize(event.getSize())
                .build()
        ).build();
  }

}
