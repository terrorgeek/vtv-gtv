package com.miquido.vtv.bo;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * User profile.
 */
@Data
public class Profile {

    private String id;
    private String name;
    private Date dateModified;
    private String description;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String avatarId;
    private String avatarURL;
    private String facebookId;

    private boolean online=false;
    private String watchedChannel=null;

}
