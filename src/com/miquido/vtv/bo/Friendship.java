package com.miquido.vtv.bo;

import lombok.Data;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 31.08.12
 * Time: 17:35
 * To change this template use File | Settings | File Templates.
 */
@Data
public class Friendship extends Entity {
    String description;
    Status status;
    Id profileId;
    Id friendProfileId;
    Profile friend;

    public static enum Status {
        New("new"), Accepted("accepted"), Removed("removed"), Rejected("rejected");
        String name;
        private Status(String name) {
            this.name = name;
        }
        public static Status forName(String name) {
            if (name==null) return null;
            for (Status type : Status.class.getEnumConstants()) {
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
