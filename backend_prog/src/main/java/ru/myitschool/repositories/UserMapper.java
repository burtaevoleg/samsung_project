package ru.myitschool.repositories;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.jdbc.core.RowMapper;
import ru.myitschool.entity.Users;

import java.sql.ResultSet;
import java.sql.SQLException;

public class UserMapper implements RowMapper<SecurityProperties.User> {

    public Users mapRow(ResultSet resultSet, int i) throws SQLException {
        Users users = new Users();
        users.setParent_id(resultSet.getInt("parent_id"));                           //названия столбцов из базы данных
        users.setChild_phone(resultSet.getString("child_phone_number"));
        users.setParent_phone(resultSet.getString("parent_phone_number"));
        return users;
    }
}
