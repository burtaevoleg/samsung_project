package ru.myitschool.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.myitschool.entity.Users;

@Component
public class UsersRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public int createUser(String parent_phone_number, String child_phone_number, String unique_code) {
        return jdbcTemplate.update("INSERT INTO project.users (parent_phone_number, child_phone_number, unique_code) VALUES (?,?,?)", parent_phone_number,
                child_phone_number,unique_code);
    }

    public int activateUser(String unique_code){
        return jdbcTemplate.update("UPDATE project.users SET activate_code = ? WHERE unique_code = ?",true, unique_code );
    }

    public Users getIdUser(String unique_code) {
        return (Users) jdbcTemplate.queryForObject("SELECT * FROM project.users WHERE unique_code=?",new UserMapper(), unique_code );
    }
}