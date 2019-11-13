package com.rocket.summer.framework.data.redis.connection.convert;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import com.rocket.summer.framework.core.convert.converter.Converter;
import com.rocket.summer.framework.dao.DataAccessException;
import com.rocket.summer.framework.data.redis.connection.FutureResult;

/**
 * Converts the results of transaction exec using a supplied Queue of {@link FutureResult}s. Converts any Exception
 * objects returned in the list as well, using the supplied Exception {@link Converter}
 *
 * @author Jennifer Hickey
 * @param <T> The type of {@link FutureResult} of the individual tx operations
 */
public class TransactionResultConverter<T> implements Converter<List<Object>, List<Object>> {

    private Queue<FutureResult<T>> txResults = new LinkedList<FutureResult<T>>();

    private Converter<Exception, DataAccessException> exceptionConverter;

    public TransactionResultConverter(Queue<FutureResult<T>> txResults,
                                      Converter<Exception, DataAccessException> exceptionConverter) {
        this.txResults = txResults;
        this.exceptionConverter = exceptionConverter;
    }

    public List<Object> convert(List<Object> execResults) {
        if (execResults == null) {
            return null;
        }
        if (execResults.size() != txResults.size()) {
            throw new IllegalArgumentException("Incorrect number of transaction results. Expected: " + txResults.size()
                    + " Actual: " + execResults.size());
        }
        List<Object> convertedResults = new ArrayList<Object>();
        for (Object result : execResults) {
            FutureResult<T> futureResult = txResults.remove();
            if (result instanceof Exception) {
                throw exceptionConverter.convert((Exception) result);
            }
            if (!(futureResult.isStatus())) {
                convertedResults.add(futureResult.convert(result));
            }
        }
        return convertedResults;
    }
}

