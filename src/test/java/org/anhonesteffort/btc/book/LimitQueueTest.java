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

import org.junit.Test;

import java.util.List;
import java.util.Optional;

public class LimitQueueTest extends BaseTest {

  @Test
  public void testAddPeekRemoveClearAsks() {
    final LimitQueue ASKS = new LimitQueue(Order.Side.ASK, 10);

    ASKS.addOrder(newAsk("00", 10, 1));
    ASKS.addOrder(newAsk("01", 10, 2));
    ASKS.addOrder(newAsk("02", 20, 2));
    ASKS.addOrder(newAsk("03",  5, 2));

    Optional<Limit> BEST_ASK = ASKS.peek();
    assert BEST_ASK.isPresent();
    assert BEST_ASK.get().getPrice()  == 5;
    assert BEST_ASK.get().getVolume() == 2;
    assert ASKS.removeOrder(5l, "03").isPresent();

    BEST_ASK = ASKS.peek();
    assert BEST_ASK.isPresent();
    assert BEST_ASK.get().getPrice()  == 10;
    assert BEST_ASK.get().getVolume() ==  3;
    assert ASKS.removeOrder(10l, "01").isPresent();

    BEST_ASK = ASKS.peek();
    assert BEST_ASK.isPresent();
    assert BEST_ASK.get().getPrice()  == 10;
    assert BEST_ASK.get().getVolume() ==  1;
    assert ASKS.removeOrder(20l, "02").isPresent();

    BEST_ASK = ASKS.peek();
    assert BEST_ASK.isPresent();
    assert BEST_ASK.get().getPrice()  == 10;
    assert BEST_ASK.get().getVolume() ==  1;

    ASKS.clear();
    assert !ASKS.removeOrder(10l, "00").isPresent();
    assert !ASKS.peek().isPresent();
  }

  @Test
  public void testAddPeekRemoveClearBids() {
    final LimitQueue BIDS = new LimitQueue(Order.Side.BID, 10);

    BIDS.addOrder(newBid("00", 10, 1));
    BIDS.addOrder(newBid("01", 10, 2));
    BIDS.addOrder(newBid("02", 20, 2));
    BIDS.addOrder(newBid("03",  5, 2));

    Optional<Limit> BEST_BID = BIDS.peek();
    assert BEST_BID.isPresent();
    assert BEST_BID.get().getPrice()  == 20;
    assert BEST_BID.get().getVolume() ==  2;
    assert BIDS.removeOrder(20l, "02").isPresent();

    BEST_BID = BIDS.peek();
    assert BEST_BID.isPresent();
    assert BEST_BID.get().getPrice()  == 10;
    assert BEST_BID.get().getVolume() ==  3;
    assert BIDS.removeOrder(10l, "01").isPresent();

    BEST_BID = BIDS.peek();
    assert BEST_BID.isPresent();
    assert BEST_BID.get().getPrice()  == 10;
    assert BEST_BID.get().getVolume() ==  1;
    assert BIDS.removeOrder(10l, "00").isPresent();

    BEST_BID = BIDS.peek();
    assert BEST_BID.isPresent();
    assert BEST_BID.get().getPrice()  == 5;
    assert BEST_BID.get().getVolume() == 2;

    BIDS.clear();
    assert !BIDS.removeOrder(5l, "03").isPresent();
    assert !BIDS.peek().isPresent();
  }

  @Test
  public void testRemoveAskLiquidity() {
    final LimitQueue ASKS = new LimitQueue(Order.Side.ASK, 10);
          Order      BID  = newBid(15, 5);

    ASKS.addOrder(newAsk(10, 1));
    ASKS.addOrder(newAsk(10, 2));
    ASKS.addOrder(newAsk(20, 2));
    ASKS.addOrder(newAsk(5, 2));

    List<Order> MAKERS = ASKS.takeLiquidityFromBestLimit(BID);
    assert BID.getSizeRemaining()           == 3;
    assert MAKERS.size()                    == 1;
    assert MAKERS.get(0).getPrice()         == 5;
    assert MAKERS.get(0).getSizeRemaining() == 0;

    MAKERS = ASKS.takeLiquidityFromBestLimit(BID);
    assert BID.getSizeRemaining()           ==  0;
    assert MAKERS.size()                    ==  2;
    assert MAKERS.get(0).getPrice()         == 10;
    assert MAKERS.get(0).getSizeRemaining() ==  0;
    assert MAKERS.get(0).getPrice()         == 10;
    assert MAKERS.get(0).getSizeRemaining() ==  0;

    MAKERS = ASKS.takeLiquidityFromBestLimit(BID);
    assert MAKERS.size() == 0;

    BID    = newBid(20, 3);
    MAKERS = ASKS.takeLiquidityFromBestLimit(BID);
    assert BID.getSizeRemaining()           ==  1;
    assert MAKERS.size()                    ==  1;
    assert MAKERS.get(0).getPrice()         == 20;
    assert MAKERS.get(0).getSizeRemaining() ==  0;

    MAKERS = ASKS.takeLiquidityFromBestLimit(BID);
    assert MAKERS.size() == 0;
    assert !ASKS.peek().isPresent();
  }

  @Test
  public void testRemoveBidLiquidity() {
    final LimitQueue BIDS = new LimitQueue(Order.Side.BID, 10);
          Order      ASK  = newAsk(15, 5);

    BIDS.addOrder(newBid(10, 1));
    BIDS.addOrder(newBid(10, 2));
    BIDS.addOrder(newBid(20, 2));
    BIDS.addOrder(newBid(5, 2));

    List<Order> MAKERS = BIDS.takeLiquidityFromBestLimit(ASK);
    assert ASK.getSizeRemaining()           ==  3;
    assert MAKERS.size()                    ==  1;
    assert MAKERS.get(0).getPrice()         == 20;
    assert MAKERS.get(0).getSizeRemaining() ==  0;

    MAKERS = BIDS.takeLiquidityFromBestLimit(ASK);
    assert ASK.getSizeRemaining() == 3;
    assert MAKERS.size()          == 0;

    ASK    = newAsk(5, 5);
    MAKERS = BIDS.takeLiquidityFromBestLimit(ASK);
    assert ASK.getSizeRemaining()           ==  2;
    assert MAKERS.size()                    ==  2;
    assert MAKERS.get(0).getPrice()         == 10;
    assert MAKERS.get(0).getSizeRemaining() ==  0;
    assert MAKERS.get(0).getPrice()         == 10;
    assert MAKERS.get(0).getSizeRemaining() ==  0;

    MAKERS = BIDS.takeLiquidityFromBestLimit(ASK);
    assert ASK.getSizeRemaining()           == 0;
    assert MAKERS.size()                    == 1;
    assert MAKERS.get(0).getPrice()         == 5;
    assert MAKERS.get(0).getSizeRemaining() == 0;

    MAKERS = BIDS.takeLiquidityFromBestLimit(ASK);
    assert MAKERS.size() == 0;
    assert !BIDS.peek().isPresent();
  }

  @Test
  public void testRemoveAskLiquidityWithMarketBids() {
    final LimitQueue  ASKS = new LimitQueue(Order.Side.ASK, 10);
          MarketOrder BID  = newMarketBid(5, -1);

    ASKS.addOrder(newAsk(10, 1));
    ASKS.addOrder(newAsk(10, 2));
    ASKS.addOrder(newAsk(20, 2));
    ASKS.addOrder(newAsk(5, 2));

    List<Order> MAKERS = ASKS.takeLiquidityFromBestLimit(BID);

    assert BID.getVolumeRemoved()           == 2;
    assert MAKERS.size()                    == 1;
    assert MAKERS.get(0).getPrice()         == 5;
    assert MAKERS.get(0).getSizeRemaining() == 0;

    MAKERS = ASKS.takeLiquidityFromBestLimit(BID);
    assert BID.getVolumeRemoved()           ==  5;
    assert MAKERS.size()                    ==  2;
    assert MAKERS.get(0).getPrice()         == 10;
    assert MAKERS.get(0).getSizeRemaining() ==  0;
    assert MAKERS.get(0).getPrice()         == 10;
    assert MAKERS.get(0).getSizeRemaining() ==  0;

    MAKERS = ASKS.takeLiquidityFromBestLimit(BID);
    assert MAKERS.size() == 0;

    BID    = newMarketBid(3, -1);
    MAKERS = ASKS.takeLiquidityFromBestLimit(BID);
    assert BID.getVolumeRemoved()           ==  2;
    assert BID.getSizeRemaining()           ==  1;
    assert MAKERS.size()                    ==  1;
    assert MAKERS.get(0).getPrice()         == 20;
    assert MAKERS.get(0).getSizeRemaining() ==  0;

    MAKERS = ASKS.takeLiquidityFromBestLimit(BID);
    assert MAKERS.size() == 0;
    assert !ASKS.peek().isPresent();
  }

  @Test
  public void testRemoveBidLiquidityWithMarketAsks() {
    final LimitQueue  BIDS = new LimitQueue(Order.Side.BID, 10);
          MarketOrder ASK  = newMarketAsk(5, -1);

    BIDS.addOrder(newBid(10, 1));
    BIDS.addOrder(newBid(10, 2));
    BIDS.addOrder(newBid(20, 2));
    BIDS.addOrder(newBid(5, 2));

    List<Order> MAKERS = BIDS.takeLiquidityFromBestLimit(ASK);

    assert ASK.getVolumeRemoved()           ==  2;
    assert MAKERS.size()                    ==  1;
    assert MAKERS.get(0).getPrice()         == 20;
    assert MAKERS.get(0).getSizeRemaining() ==  0;

    MAKERS = BIDS.takeLiquidityFromBestLimit(ASK);
    assert ASK.getVolumeRemoved()           ==  5;
    assert ASK.getSizeRemaining()           ==  0;
    assert MAKERS.size()                    ==  2;
    assert MAKERS.get(0).getPrice()         == 10;
    assert MAKERS.get(0).getSizeRemaining() ==  0;
    assert MAKERS.get(0).getPrice()         == 10;
    assert MAKERS.get(0).getSizeRemaining() ==  0;

    MAKERS = BIDS.takeLiquidityFromBestLimit(ASK);
    assert MAKERS.size() == 0;

    ASK    = newMarketAsk(3, -1);
    MAKERS = BIDS.takeLiquidityFromBestLimit(ASK);
    assert ASK.getVolumeRemoved()           == 2;
    assert ASK.getSizeRemaining()           == 1;
    assert MAKERS.size()                    == 1;
    assert MAKERS.get(0).getPrice()         == 5;
    assert MAKERS.get(0).getSizeRemaining() == 0;

    MAKERS = BIDS.takeLiquidityFromBestLimit(ASK);
    assert MAKERS.size() == 0;
    assert !BIDS.peek().isPresent();
  }

}
