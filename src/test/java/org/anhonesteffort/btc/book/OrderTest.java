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

public class OrderTest {

  @Test
  public void test() {
    final Order ORDER = new Order("lol", Order.Side.BID, 10, 20);

    assert ORDER.getOrderId().equals("lol");
    assert ORDER.getSide().equals(Order.Side.BID);
    assert ORDER.getPrice()         == 10;
    assert ORDER.getSize()          == 20;
    assert ORDER.getSizeRemaining() == 20;
    assert ORDER.getValueRemoved()  == 0;

    assert ORDER.takeSize(5)        == 5;
    assert ORDER.getSizeRemaining() == 15;
    assert ORDER.getValueRemoved()  == 5 * ORDER.getPrice();

    ORDER.clearValueRemoved();
    assert ORDER.getValueRemoved() == 0;

    assert ORDER.takeSize(20)       == 15;
    assert ORDER.getSizeRemaining() == 0;
    assert ORDER.getValueRemoved()  == 15 * ORDER.getPrice();

    ORDER.clearValueRemoved();
    assert ORDER.getValueRemoved() == 0;
  }

}
