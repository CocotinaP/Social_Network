package com.example.socialnetwork_1connetiondb.utils;

import com.example.socialnetwork_1connetiondb.domain.Entity;

import java.time.format.DateTimeFormatter;
import java.util.Iterator;

public class Utils {
    /**
     * Generate an unique long id.
     * @param list of entities which have id
     * @return an unique id that does not exist in the list
     * @param <E> the entity
     */
    public <E extends Entity<Long>> Long generateLongId (Iterable<E> list){
        Iterator<E> iterator = list.iterator();
        Long[] idMax = {0L};
        list.forEach(entity->{
            if (idMax[0] < entity.getId()){
                idMax[0] = entity.getId();
            }
        });
        idMax[0]++;
        return idMax[0];
    }

    public static DateTimeFormatter dateFormat(){
        return DateTimeFormatter.ofPattern("dd/MMMM/yyyy HH:mm");
    }
}
