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

public class MarketOrder extends Order {

  private final long funds;
  private       long fundsRemaining;
  private       long volumeRemoved;

  public MarketOrder(String orderId, Side side, long size, long funds) {
    super(orderId, side, 0l, size);
    this.funds          = funds;
    this.fundsRemaining = funds;
    volumeRemoved       = 0l;
  }

  public long getFunds() {
    return funds;
  }

  public long getFundsRemaining() {
    return fundsRemaining;
  }

  public long getVolumeRemoved() {
    return volumeRemoved;
  }

  @Override
  protected void subtract(long size, long price) {
    super.subtract(size, price);
    fundsRemaining -= (price * size);
    volumeRemoved  += size;
  }

  public long getSizeRemainingFor(long price) {
    long fundsTakeSize = fundsRemaining / price;

    if (funds > 0l && size > 0l) {
      return Math.min(fundsTakeSize, sizeRemaining);
    } else if (funds > 0l) {
      return fundsTakeSize;
    } else if (size > 0l) {
      return sizeRemaining;
    } else {
      return 0l;
    }
  }

}
