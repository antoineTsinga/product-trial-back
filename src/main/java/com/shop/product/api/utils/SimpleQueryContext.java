package com.shop.product.api.utils;

import jakarta.persistence.criteria.Fetch;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import net.kaczmarzyk.spring.data.jpa.utils.QueryContext;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class SimpleQueryContext implements QueryContext {

    private final Map<String, Join<?, ?>> evaluatedJoins = new HashMap<>();
    private final Map<String, Fetch<?, ?>> evaluatedFetches = new HashMap<>();

    @Override
    public boolean existsJoin(String s, Root<?> root) {
        return evaluatedJoins.containsKey(s);
    }

    @Override
    public Join<?, ?> getEvaluated(String s, Root<?> root) {
        return evaluatedJoins.get(s);
    }

    @Override
    public void putLazyVal(String s, Function<Root<?>, Join<?, ?>> function) {
        evaluatedJoins.put(s, function.apply(null));
    }

    @Override
    public Fetch<?, ?> getEvaluatedJoinFetch(String s) {
        return evaluatedFetches.get(s);
    }

    @Override
    public void putEvaluatedJoinFetch(String s, Fetch<?, ?> fetch) {
        evaluatedFetches.put(s, fetch);
    }
}