package com.miquido.vtv.codsservices.dataobjects;

import com.miquido.vtv.bo.Id;
import lombok.Data;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 10:59
 * To change this template use File | Settings | File Templates.
 */
@Data
public class Relationship {

    Id id;
    String description;
    Id entryId;
    Id relatedEntryId;
    Type type;
    String status;
    private Date dateModified;
    private Date dateEntered;


    public static enum Type {
        Friend("friend");
        String name;
        private Type(String name) {
            this.name = name;
        }
        public static Type forName(String name) {
            if (name==null) return null;
            for (Type type : Type.class.getEnumConstants()) {
                if (type.name.equalsIgnoreCase(name))
                    return type;
            }
            return null;
        }
        public String toString() {
            return name;
        }
    }

}
