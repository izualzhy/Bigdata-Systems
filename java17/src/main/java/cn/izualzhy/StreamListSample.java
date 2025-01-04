package cn.izualzhy;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public class StreamListSample {
    public static void main(String[] args) {
        System.out.println(getResGroupIds(null, null));
        System.out.println(getResGroupIds(null, List.of(1, 2, 3)));
        System.out.println(getResGroupIds(List.of(1, 2, 3), null));
        System.out.println(getResGroupIds(List.of(1, 2, 3), List.of(1, 2, 3)));
    }

    private static List<String> getResGroupIds(List<Integer> publicResGroupIds, List<Integer> privateResGroupIds) {
        return Stream.concat(
                Optional.ofNullable(publicResGroupIds).orElse(Collections.emptyList()).stream(),
                Optional.ofNullable(privateResGroupIds).orElse(Collections.emptyList()).stream()
        ).map(Object::toString).toList();
    }
}
