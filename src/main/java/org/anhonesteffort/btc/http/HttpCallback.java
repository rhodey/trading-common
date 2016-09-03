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

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public abstract class HttpCallback<T> implements Callback {

  protected final CompletableFuture<T> future;

  protected HttpCallback(CompletableFuture<T> future) {
    this.future = future;
  }

  protected abstract void complete(Call call, Response response) throws Exception;

  @Override
  public void onResponse(Call call, Response response) {
    try {

      if (!response.isSuccessful()) {
        future.completeExceptionally(new HttpException(
            "http returned " +
                "code: " + response.code() + ", " +
                "msg: " + (response.message() == null ? "''" : response.message()) + ", " +
                "body: " + response.body().string()
        ));
      } else {
        complete(call, response);
      }

    } catch (Throwable throwable) {
      future.completeExceptionally(throwable);
    } finally {
      response.body().close();
    }
  }

  @Override
  public void onFailure(Call call, IOException e) {
    future.completeExceptionally(e);
  }

}
