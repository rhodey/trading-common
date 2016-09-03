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

package org.anhonesteffort.btc.http;

import okhttp3.OkHttpClient;

import java.util.concurrent.TimeUnit;

public class HttpClient {

  private static OkHttpClient client;

  public static OkHttpClient getInstance() {
    if (client == null) {
      client = new OkHttpClient.Builder()
          .connectTimeout(5l, TimeUnit.SECONDS)
          .readTimeout(5l, TimeUnit.SECONDS)
          .writeTimeout(5l, TimeUnit.SECONDS)
          .build();
    }

    return client;
  }

}
