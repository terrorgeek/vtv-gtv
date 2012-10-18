package com.miquido.vtv.codsservices.dataobjects;


import lombok.Data;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Created with IntelliJ IDEA.
 * User: ljazgar
 * Date: 03.08.12
 * Time: 11:37
 * To change this template use File | Settings | File Templates.
 */
@Data @RequiredArgsConstructor(suppressConstructorProperties = true)
public class LoginInput {

    @NonNull
    private String login;
    /**
     * MD5 Hashed password.
     */
    @NonNull
    private String passwordHash;

    /**
     * optional, used to identify your application
     */
    private String applicationName;

//    public LoginInput(String login, String passwordHash) {
//        this.login = login;
//        this.passwordHash = passwordHash;
//    }

}
