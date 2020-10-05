package ru.myitschool.entity;
import org.springframework.boot.autoconfigure.security.SecurityProperties;

public class Users extends SecurityProperties.User {
    private Integer parent_id;
    private String name;
    private String child_phone;
    private String parent_phone;


    public void setChild_phone(String child_phone) {
        this.child_phone = child_phone;
    }

    public void setParent_phone(String parent_phone) {
        this.parent_phone = parent_phone;
    }

    public void setParent_id(Integer parent_id) {
        this.parent_id = parent_id;
    }

    public Integer getParent_id() {
        return parent_id;
    }

    public String getName() {
        return name;
    }


    public String getChild_phone() {
        return child_phone;
    }

    public String getParent_phone() {
        return parent_phone;
    }

}