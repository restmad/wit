// Copyright (c) 2013-2016, febit.org. All Rights Reserved.
package org.febit.wit.lang;

/**
 *
 * @author zqq90
 */
public interface Iter {

    boolean hasNext();

    Object next();

    boolean isFirst();

    int index();
}
