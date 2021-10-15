package com.jworks.qup.service.misc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author bodmas
 * @since Oct 11, 2021.
 */
public interface Utils {

    /**
     * Determines whether a list is sequential.
     * <p>
     * A list is sequential if every element in it is bigger than it's preceding element by 1.
     * @param list the list to check
     * @return true if the list is sequential
     */
    static boolean isSequential(List<Long> list) {
        if (list.isEmpty())
            return true;
        ArrayList<Long> copyList = new ArrayList<>(list);
        Collections.sort(copyList);
        Iterator<Long> iterator = copyList.iterator();
        long last = iterator.next();
        while (iterator.hasNext()) {
            long current = iterator.next();
            if (current != last + 1)
                return false;
            last = current;
        }
        return true;
    }
}
