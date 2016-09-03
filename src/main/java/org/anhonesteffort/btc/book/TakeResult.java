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

import java.util.List;

public class TakeResult {

  private final Order taker;
  private final List<Order> makers;
  private final long takeSize;
  private final long takeValue;

  public TakeResult(Order taker, List<Order> makers, long takeSize) {
    this.taker     = taker;
    this.makers    = makers;
    this.takeSize  = takeSize;
    this.takeValue = makers.stream().mapToLong(Order::getValueRemoved).sum();
  }

  public Order getTaker() {
    return taker;
  }

  public List<Order> getMakers() {
    return makers;
  }

  public long getTakeSize() {
    return takeSize;
  }

  public long getTakeValue() {
    return takeValue;
  }

  public void clearMakerValueRemoved() {
    makers.forEach(Order::clearValueRemoved);
  }

}
