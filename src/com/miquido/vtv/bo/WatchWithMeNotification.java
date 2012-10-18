package com.miquido.vtv.bo;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 16.09.12
 * Time: 18:22
 * To change this template use File | Settings | File Templates.
 */
public class WatchWithMeNotification extends Notification {

    public static enum Status {
        New("new"), Read("read"), Accepted("accepted"), Rejected("rejected");
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

    public WatchWithMeNotification() {
        super(Type.watchWithMe);
    }

    public Id getChannelId() {
        return getSubjectId();
    }
    public void setChannelId(Id channelId) {
        setSubjectId(channelId);
    }

    public Status getStatus() {
        return Status.forName(getStatusString());
    }
    public void setStatus(Status status) {
        setStatusString(status.toString());
    }

}
