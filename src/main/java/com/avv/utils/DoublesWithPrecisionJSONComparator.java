package com.avv.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.skyscreamer.jsonassert.comparator.DefaultComparator;

/**
 * Flexible Comparison for JSONAssert double comparison
 *
 * There are many improvements possible, please consider this a working prototype.
 *
 * @author Alexander Voll
 */

public class DoublesWithPrecisionJSONComparator extends DefaultComparator {
    private Double precision;

    public DoublesWithPrecisionJSONComparator(JSONCompareMode mode, Double precision) {
        super(mode);
        this.precision = precision;
    }

    public static void assertEquals( String expected, String actual, double precision) throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON(expected, actual, new DoublesWithPrecisionJSONComparator(JSONCompareMode.LENIENT, precision));
        if (result.failed()) {
            throw new AssertionError(result.getMessage());
        }
    }

    @Override
    public void compareValues(String prefix, Object expectedValue, Object actualValue, JSONCompareResult result)
            throws JSONException {
        if (expectedValue instanceof Number && actualValue instanceof Number) {
            if (Math.abs(((Number)expectedValue).doubleValue() - ((Number)actualValue).doubleValue()) > precision){
                result.fail(prefix, expectedValue, actualValue);
            }
        } else if (expectedValue.getClass().isAssignableFrom(actualValue.getClass())) {
            if (expectedValue instanceof JSONArray) {
                compareJSONArray(prefix, (JSONArray) expectedValue, (JSONArray) actualValue, result);
            } else if (expectedValue instanceof JSONObject) {
                compareJSON(prefix, (JSONObject) expectedValue, (JSONObject) actualValue, result);
            } else if (!expectedValue.equals(actualValue)) {
                result.fail(prefix, expectedValue, actualValue);
            }
        } else {
            result.fail(prefix, expectedValue, actualValue);
        }
    }
}
