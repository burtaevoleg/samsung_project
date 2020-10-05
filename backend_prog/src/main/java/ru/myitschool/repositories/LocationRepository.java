package ru.myitschool.repositories;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;


import java.sql.*;


@Component
public class LocationRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    public int createLocation(Double latitude,Double longitude, Integer idPerson,String timezone) {
        return jdbcTemplate.update("INSERT INTO project.locations (longitude, latitude, parent_id, timezone) VALUES (?,?,?,?::timestamp without time zone)",longitude,latitude,idPerson,timezone);
    }


    public JSONArray getLocation(Integer id) {
        JSONObject jsonObject;
        JSONArray jsonArr = new JSONArray();

        try {
            Connection conn = jdbcTemplate.getDataSource().getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM project.locations WHERE parent_id="+id+" ORDER BY id DESC LIMIT 1"; // TODO not right sql
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                jsonObject = new JSONObject();
                jsonObject.put("latitude", rs.getString("latitude"));
                jsonObject.put("longitude", rs.getString("longitude"));
                jsonObject.put("timezone",rs.getTimestamp("timezone"));
                jsonArr.put(jsonObject);
            }
        } catch (SQLException e) {
            e.getLocalizedMessage();
            return null;
        } catch (JSONException e) {
            e.getLocalizedMessage();
            return null;
        }
        return jsonArr;
    }

    public JSONArray getLocations(Integer parent_id,String date_before,String date_after) {
        JSONObject jsonObject;
        JSONArray jsonArr = new JSONArray();

        try {
            Connection conn = jdbcTemplate.getDataSource().getConnection();
            Statement stmt = conn.createStatement();
            String sql = "SELECT * FROM project.locations WHERE timezone>'"+date_before+"' AND timezone<'"+date_after+"' AND parent_id="+parent_id+""; // TODO not right sql
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                jsonObject = new JSONObject();
                jsonObject.put("latitude", rs.getString("latitude"));
                jsonObject.put("longitude", rs.getString("longitude"));
                jsonObject.put("timezone",rs.getTimestamp("timezone"));
                jsonArr.put(jsonObject);
            }
        } catch (SQLException e) {
            e.getLocalizedMessage();
            return null;
        } catch (JSONException e) {
            e.getLocalizedMessage();
            return null;
        }
        return jsonArr;
    }


}