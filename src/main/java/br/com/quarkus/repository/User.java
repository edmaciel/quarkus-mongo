package br.com.quarkus.repository;

import java.util.Date;

public class User {

    private String document;
    private int codeOrigin;
    private Date updateDate;

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public int getCodeOrigin() {
        return codeOrigin;
    }

    public void setCodeOrigin(int codeOrigin) {
        this.codeOrigin = codeOrigin;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "User{" +
                "document='" + document + '\'' +
                ", codeOrigin=" + codeOrigin +
                ", updateDate=" + updateDate +
                '}';
    }
}
