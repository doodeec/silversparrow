package com.doodeec.silversparrow.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import rx.functions.Func1;

/**
 * @author Dusan Bartos
 */
public final class ValidationUtils {

    /**
     * Validator class to make validation more generic
     * First you need to setup predicates for key-specific fields
     * these predicates will be used in the end to determine if value is valid or not
     * Predicate also contains resource id of error message to show if value is not valid
     *
     * When predicates are set, you can call validate, with a map parameter containing all entries
     * with the same string key as the matching predicate
     */
    public static class Validator {

        private Map<String, Tuple2<Func1<String, Boolean>, Integer>> predicateMap = new HashMap<>();

        public void setPredicateMap(Map<String, Tuple2<Func1<String, Boolean>, Integer>> predicateMap) {
            this.predicateMap = predicateMap;
        }

        /**
         * Validates given map of values
         *
         * @param values values to validate
         *
         * @return null if all values are valid, else list of error messages
         */
        public List<Integer> validate(Map<String, String> values) {
            final List<Integer> messages = new ArrayList<>();

            Tuple2<Func1<String, Boolean>, Integer> predicate;
            for (String key : values.keySet()) {
                if (predicateMap != null && predicateMap.containsKey(key)) {
                    predicate = predicateMap.get(key);
                    if (!predicate.getFirst().call(values.get(key))) {
                        messages.add(predicate.getSecond());
                    }
                }
            }

            return messages.isEmpty() ? null : messages;
        }
    }
}
