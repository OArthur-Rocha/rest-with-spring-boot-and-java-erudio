package com.erudio.integrationtests.vo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.xml.bind.annotation.XmlRootElement;

import java.io.Serializable;
import java.util.Objects;

@XmlRootElement()
public class AccountCredentialsVo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userName;
    private String passWord;

    @JsonCreator
    public AccountCredentialsVo(@JsonProperty("usarName") String userName, @JsonProperty("passWord") String passWord) {
        this.userName = userName;
        this.passWord = passWord;
    }

    public AccountCredentialsVo() {
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassWord() {
        return passWord;
    }

    public void setPassWord(String passWord) {
        this.passWord = passWord;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AccountCredentialsVo that = (AccountCredentialsVo) o;

        if (!Objects.equals(userName, that.userName)) return false;
        return Objects.equals(passWord, that.passWord);
    }

    @Override
    public int hashCode() {
        int result = userName != null ? userName.hashCode() : 0;
        result = 31 * result + (passWord != null ? passWord.hashCode() : 0);
        return result;
    }
}
