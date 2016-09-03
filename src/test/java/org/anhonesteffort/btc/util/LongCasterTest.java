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

package org.anhonesteffort.btc.util;

import org.junit.Test;

public class LongCasterTest {

  @Test
  public void test() {
    final LongCaster CASTER = new LongCaster(0.000000000001d, 100d);

    assert CASTER.fromDouble(0.000000000001d) ==            1l;
    assert CASTER.fromDouble(0.000000000010d) ==           10l;
    assert CASTER.fromDouble(0.000000000100d) ==          100l;
    assert CASTER.fromDouble(0.000000001000d) ==         1000l;
    assert CASTER.fromDouble(0.000000010000d) ==        10000l;
    assert CASTER.fromDouble(0.000000100000d) ==       100000l;
    assert CASTER.fromDouble(0.000001000000d) ==      1000000l;
    assert CASTER.fromDouble(0.000010000000d) ==     10000000l;
    assert CASTER.fromDouble(0.000100000000d) ==    100000000l;
    assert CASTER.fromDouble(0.001000000000d) ==   1000000000l;
    assert CASTER.fromDouble(0.010000000000d) ==  10000000000l;
    assert CASTER.fromDouble(0.100000000000d) == 100000000000l;
    assert CASTER.fromDouble(0.123456789012d) == 123456789012l;

    assert CASTER.toDouble(CASTER.fromDouble(1020d))               == 1020d;
    assert CASTER.toDouble(CASTER.fromDouble(10.20d))              == 10.20d;
    assert CASTER.toDouble(CASTER.fromDouble(1.337d))              == 1.34d;
    assert CASTER.toDouble(CASTER.fromDouble(12345.333333333337d)) == 12345.33d;
  }

}
