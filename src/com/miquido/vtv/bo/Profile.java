package com.miquido.vtv.bo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * User profile.
 */
@Data
public class Profile extends Entity {

    private String name;
    private String description;
    private String firstName;
    private String lastName;
    private Id avatarId;
    private String facebookId;

    private boolean online=false;
    private Id currentChannelId =null;
    private Id requestedChannelId = null;
}
