package org.waterwood.waterfunservice.infrastructure.utils;

import java.util.Collection;
import java.util.Collections;

public final class CollectionUtil {
    public static <T>  boolean isEmpty(Collection<T> collection){
        return collection == null || collection.isEmpty();
    }
}
