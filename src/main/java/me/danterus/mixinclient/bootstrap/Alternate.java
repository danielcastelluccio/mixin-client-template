package me.danterus.mixinclient.bootstrap;

import com.google.common.base.Preconditions;
import com.google.common.util.concurrent.*;

public class Alternate {

    public static <T> T firstNonNull(T first, T second) {
        return first != null ? first : Preconditions.checkNotNull(second);
    }

    public static <V> void addCallback(ListenableFuture<V> future, FutureCallback<? super V> callback) {
        Futures.addCallback(future, callback, MoreExecutors.directExecutor());
    }

}
