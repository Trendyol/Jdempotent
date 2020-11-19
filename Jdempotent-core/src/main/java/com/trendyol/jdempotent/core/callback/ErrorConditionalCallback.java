package com.trendyol.jdempotent.core.callback;

/**
 * 
 * A callback interface that need to clear cache for custom error condition
 *
 */
public interface ErrorConditionalCallback {

    /**
     * a error state flag
     *
     * @param response
     * @return
     */
    boolean onErrorCondition(Object response);

    /**
     *
     * exception to throw when custom error occurs
     *
     * @return
     */
    RuntimeException onErrorCustomException();
    
}