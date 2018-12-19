package com.guodongbaohe.app.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class ListUtil {
    /**
     * 得到去除重复后的集合
     *
     * @param list
     * @return
     */
    public static List<String> getRemoveList(List<String> list) {
        Set set = new HashSet();
        List<String> newList = new ArrayList<>();
        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
            String object = (String) iter.next();
            if (set.add(object))
                newList.add(object);
        }
        return newList;
    }
}
