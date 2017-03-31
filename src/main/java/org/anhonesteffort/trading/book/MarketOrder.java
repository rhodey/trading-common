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

public class MarketOrder extends Order {

  private final double funds;
  private       double fundsRemaining;
  private       double volumeRemoved;

  public MarketOrder(String orderId, Side side, double size, double funds) {
    super(orderId, side, 0d, size);
    this.funds          = funds;
    this.fundsRemaining = funds;
    volumeRemoved       = 0d;
  }

  public double getFunds() {
    return funds;
  }

  public double getFundsRemaining() {
    return fundsRemaining;
  }

  public double getVolumeRemoved() {
    return volumeRemoved;
  }

  @Override
  protected void subtract(double size, double price) {
    super.subtract(size, price);
    fundsRemaining -= (price * size);
    volumeRemoved  += size;
  }

  public double getSizeRemainingFor(double price) {
    double fundsTakeSize = fundsRemaining / price;

    if (funds > 0d && size > 0d) {
      return Math.min(fundsTakeSize, sizeRemaining);
    } else if (funds > 0d) {
      return fundsTakeSize;
    } else if (size > 0d) {
      return sizeRemaining;
    } else {
      return 0d;
    }
  }

}
