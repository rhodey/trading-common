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

public class LimitOrderBookTest extends BaseTest {

  @Test
  public void testAddRemoveClearAsk() {
    final LimitOrderBook BOOK = new LimitOrderBook(10);

    BOOK.add(newAsk("00", 10, 20));
    BOOK.add(newAsk("01", 30, 40));

    assert BOOK.remove(Order.Side.ASK, 10l, "00").isPresent();
    BOOK.clear();
    assert !BOOK.remove(Order.Side.ASK, 30l, "01").isPresent();
  }

  @Test
  public void testAddRemoveClearBid() {
    final LimitOrderBook BOOK = new LimitOrderBook(10);

    BOOK.add(newBid("00", 10, 20));
    BOOK.add(newBid("01", 30, 40));

    assert BOOK.remove(Order.Side.BID, 10l, "00").isPresent();
    BOOK.clear();
    assert !BOOK.remove(Order.Side.BID, 30l, "01").isPresent();
  }

  @Test
  public void testAskWontTakeEmptyBook() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
    final TakeResult     RESULT = BOOK.add(newAsk(10, 10));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();
  }

  @Test
  public void testBidWontTakeEmptyBook() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
    final TakeResult     RESULT = BOOK.add(newBid(10, 10));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();
  }

  @Test
  public void testMarketAskWontTakeEmptyBook() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
    final MarketOrder    TAKER  = newMarketAsk("00", 10, 20);
    final TakeResult     RESULT = BOOK.add(TAKER);

    assert RESULT.getTakeSize()      == 0;
    assert RESULT.getTakeValue()     == 0;
    assert RESULT.getMakers().size() == 0;

    assert !BOOK.remove(Order.Side.ASK, 10l, "00").isPresent();
    assert !BOOK.remove(Order.Side.ASK, 20l, "00").isPresent();
  }

  @Test
  public void testMarketBidWontTakeEmptyBook() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
    final MarketOrder    TAKER  = newMarketBid("00", 10, 20);
    final TakeResult     RESULT = BOOK.add(TAKER);

    assert RESULT.getTakeSize()      == 0;
    assert RESULT.getTakeValue()     == 0;
    assert RESULT.getMakers().size() == 0;

    assert !BOOK.remove(Order.Side.BID, 10l, "00").isPresent();
    assert !BOOK.remove(Order.Side.BID, 20l, "00").isPresent();
  }

  @Test
  public void testAskWontTakeSmallerBid() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newBid(8, 10));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newAsk(9, 10));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();
  }

  @Test
  public void testBidWontTakeLargerAsk() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newAsk(8, 10));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newBid(7, 10));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();
  }

  @Test
  public void testOneAskTakesOneSmallerSizeBid() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newBid(10, 5));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newAsk(10, 20));
    assert RESULT.getTakeSize()      == 5;
    assert RESULT.getTakeValue()     == 10 * 5;
    assert RESULT.getMakers().size() == 1;
  }

  @Test
  public void testOneAskTakesOneEqualSizeBid() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newBid(10, 5));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newAsk(10, 5));
    assert RESULT.getTakeSize()      == 5;
    assert RESULT.getTakeValue()     == 10 * 5;
    assert RESULT.getMakers().size() == 1;
  }

  @Test
  public void testOneAskTakesOneLargerSizeBid() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newBid(10, 15));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newAsk(10, 5));
    assert RESULT.getTakeSize()      == 5;
    assert RESULT.getTakeValue()     == 10 * 5;
    assert RESULT.getMakers().size() == 1;
  }

  @Test
  public void testOneBidTakesOneSmallerSizeAsk() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newAsk(10, 5));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newBid(10, 8));
    assert RESULT.getTakeSize()      == 5;
    assert RESULT.getTakeValue()     == 10 * 5;
    assert RESULT.getMakers().size() == 1;
  }

  @Test
  public void testOneBidTakesOneEqualSizeAsk() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newAsk(10, 5));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newBid(10, 5));
    assert RESULT.getTakeSize()      == 5;
    assert RESULT.getTakeValue()     == 10 * 5;
    assert RESULT.getMakers().size() == 1;
  }

  @Test
  public void testOneBidTakesOneLargerSizeAsk() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newAsk(10, 5));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newBid(10, 2));
    assert RESULT.getTakeSize()      == 2;
    assert RESULT.getTakeValue()     == 10 * 2;
    assert RESULT.getMakers().size() == 1;
  }

  @Test
  public void testOneMarketSizeAskTakesOneSmallerSizeBid() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newBid(10, 5));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newMarketAsk(10, -1));
    assert RESULT.getTakeSize()      == 5;
    assert RESULT.getTakeValue()     == 10 * 5;
    assert RESULT.getMakers().size() == 1;
  }

  @Test
  public void testOneMarketSizeAskTakesOneEqualSizeBid() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newBid(10, 5));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newMarketAsk(5, -1));
    assert RESULT.getTakeSize()      == 5;
    assert RESULT.getTakeValue()     == 10 * 5;
    assert RESULT.getMakers().size() == 1;
  }

  @Test
  public void testOneMarketSizeAskTakesOneLargerSizeBid() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newBid(10, 8));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newMarketAsk(5, -1));
    assert RESULT.getTakeSize()      == 5;
    assert RESULT.getTakeValue()     == 10 * 5;
    assert RESULT.getMakers().size() == 1;
  }

  @Test
  public void testOneMarketFundsAskTakesOneSmallerSizeBid() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newBid(1, 5));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newMarketAsk(-1, 10));
    assert RESULT.getTakeSize()      == 5;
    assert RESULT.getTakeValue()     == 5;
    assert RESULT.getMakers().size() == 1;
  }

  @Test
  public void testOneMarketFundsAskTakesOneEqualSizeBid() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newBid(1, 5));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newMarketAsk(-1, 5));
    assert RESULT.getTakeSize()      == 5;
    assert RESULT.getTakeValue()     == 5;
    assert RESULT.getMakers().size() == 1;
  }

  @Test
  public void testOneMarketFundsAskTakesOneLargerSizeBid() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newBid(1, 10));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newMarketAsk(-1, 5));
    assert RESULT.getTakeSize()      == 5;
    assert RESULT.getTakeValue()     == 5;
    assert RESULT.getMakers().size() == 1;
  }

  @Test
  public void testOneMarketSizeBidTakesOneEqualSizeAsk() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newAsk(44852, 5));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newMarketBid(5, -1));
    assert RESULT.getTakeSize()      == 5;
    assert RESULT.getTakeValue()     == (44852 * 5);
    assert RESULT.getMakers().size() == 1;
  }

  @Test
  public void testTwoAsksTakesOneSmallerSizeBid() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newBid(10, 20));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newAsk(10, 5));
    assert RESULT.getTakeSize()      == 5;
    assert RESULT.getTakeValue()     == 10 * 5;
    assert RESULT.getMakers().size() == 1;
    RESULT.clearMakerValueRemoved();

    RESULT = BOOK.add(newAsk(10, 25));
    assert RESULT.getTakeSize()      == 15;
    assert RESULT.getTakeValue()     == 10 * 15;
    assert RESULT.getMakers().size() == 1;
  }

  @Test
  public void testTwoAsksTakesOneEqualSizeBid() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newBid(10, 20));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newAsk(10, 12));
    assert RESULT.getTakeSize()      == 12;
    assert RESULT.getTakeValue()     == 10 * 12;
    assert RESULT.getMakers().size() == 1;
    RESULT.clearMakerValueRemoved();

    RESULT = BOOK.add(newAsk(10, 8));
    assert RESULT.getTakeSize()      == 8;
    assert RESULT.getTakeValue()     == 10 * 8;
    assert RESULT.getMakers().size() == 1;
  }

  @Test
  public void testTwoAsksTakesOneLargerSizeBid() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newBid(10, 30));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newAsk(10, 12));
    assert RESULT.getTakeSize()      == 12;
    assert RESULT.getTakeValue()     == 10 * 12;
    assert RESULT.getMakers().size() == 1;
    RESULT.clearMakerValueRemoved();

    RESULT = BOOK.add(newAsk(10, 8));
    assert RESULT.getTakeSize()      == 8;
    assert RESULT.getTakeValue()     == 10 * 8;
    assert RESULT.getMakers().size() == 1;
  }

  @Test
  public void testTwoBidsTakesOneSmallerSizeAsk() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newAsk(10, 20));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newBid(10, 5));
    assert RESULT.getTakeSize()      == 5;
    assert RESULT.getTakeValue()     == 10 * 5;
    assert RESULT.getMakers().size() == 1;
    RESULT.clearMakerValueRemoved();

    RESULT = BOOK.add(newBid(10, 25));
    assert RESULT.getTakeSize()      == 15;
    assert RESULT.getTakeValue()     == 10 * 15;
    assert RESULT.getMakers().size() == 1;
  }

  @Test
  public void testTwoBidsTakesOneEqualSizeAsk() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newAsk(10, 20));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newBid(10, 9));
    assert RESULT.getTakeSize()      == 9;
    assert RESULT.getTakeValue()     == 10 * 9;
    assert RESULT.getMakers().size() == 1;
    RESULT.clearMakerValueRemoved();

    RESULT = BOOK.add(newBid(10, 11));
    assert RESULT.getTakeSize()      == 11;
    assert RESULT.getTakeValue()     == 10 * 11;
    assert RESULT.getMakers().size() == 1;
  }

  @Test
  public void testTwoBidsTakesOneLargerSizeAsk() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newAsk(10, 30));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newBid(10, 5));
    assert RESULT.getTakeSize()      == 5;
    assert RESULT.getTakeValue()     == 10 * 5;
    assert RESULT.getMakers().size() == 1;
    RESULT.clearMakerValueRemoved();

    RESULT = BOOK.add(newBid(10, 6));
    assert RESULT.getTakeSize()      == 6;
    assert RESULT.getTakeValue()     == 10 * 6;
    assert RESULT.getMakers().size() == 1;
  }

  @Test
  public void testTwoMarketAsksTakesOneSmallerSizeBid() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newBid(20, 15));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newMarketAsk(10, -1));
    assert RESULT.getTakeSize()      == 10;
    assert RESULT.getTakeValue()     == 10 * 20;
    assert RESULT.getMakers().size() == 1;
    RESULT.clearMakerValueRemoved();

    RESULT = BOOK.add(newMarketAsk(10, -1));
    assert RESULT.getTakeSize()      == 5;
    assert RESULT.getTakeValue()     == 5 * 20;
    assert RESULT.getMakers().size() == 1;
  }

  @Test
  public void testOneAskTakesTwoSmallerSizeBids() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newBid(10, 5));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newBid(12, 4));
    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newAsk(8, 20));
    assert RESULT.getTakeSize()      == 5 + 4;
    assert RESULT.getTakeValue()     == (10 * 5) + (12 * 4);
    assert RESULT.getMakers().size() == 2;
  }

  @Test
  public void testOneAskTakesTwoEqualSizeBids() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newBid(10, 13));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newBid(12, 7));
    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newAsk(8, 13 + 7));
    assert RESULT.getTakeSize()      == 13 + 7;
    assert RESULT.getTakeValue()     == (10 * 13) + (12 * 7);
    assert RESULT.getMakers().size() == 2;
  }

  @Test
  public void testOneAskTakesTwoLargerSizeBids() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newBid(10, 15));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newBid(12, 21));
    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newAsk(8, 23));
    assert RESULT.getTakeSize()      == 23;
    assert RESULT.getTakeValue()     == (12 * 21) + (10 * 2);
    assert RESULT.getMakers().size() == 2;
  }

  @Test
  public void testOneBidTakesTwoSmallerSizeAsks() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newAsk(10, 5));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newAsk(12, 4));
    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newBid(13, 20));
    assert RESULT.getTakeSize()      == 5 + 4;
    assert RESULT.getTakeValue()     == (10 * 5) + (12 * 4);
    assert RESULT.getMakers().size() == 2;
  }

  @Test
  public void testOneBidTakesTwoEqualSizeAsks() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newAsk(10, 32));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newAsk(12, 64));
    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newBid(12, 32 + 64));
    assert RESULT.getTakeSize()      == 32 + 64;
    assert RESULT.getTakeValue()     == (10 * 32) + (12 * 64);
    assert RESULT.getMakers().size() == 2;
  }

  @Test
  public void testOneBidTakesTwoLargerSizeAsks() {
    final LimitOrderBook BOOK   = new LimitOrderBook(10);
          TakeResult     RESULT = BOOK.add(newAsk(10, 31));

    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newAsk(12, 33));
    assert RESULT.getTakeSize()  == 0;
    assert RESULT.getTakeValue() == 0;
    assert RESULT.getMakers().isEmpty();

    RESULT = BOOK.add(newBid(12, 34));
    assert RESULT.getTakeSize()      == 34;
    assert RESULT.getTakeValue()     == (10 * 31) + (12 * 3);
    assert RESULT.getMakers().size() == 2;
  }

}
