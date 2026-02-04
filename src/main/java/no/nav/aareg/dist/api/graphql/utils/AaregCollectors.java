package no.nav.aareg.dist.api.graphql.utils;

import java.util.stream.Collector;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toList;

public class AaregCollectors {

    public static <T> Collector<T, ?, T> toSingleton() {
        return collectingAndThen(
                toList(),
                list -> {
                    if (list.size() != 1) {
                        throw new IllegalStateException();
                    }
                    return list.getFirst();
                }
        );
    }
}
