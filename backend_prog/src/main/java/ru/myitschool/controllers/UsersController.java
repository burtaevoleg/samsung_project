package ru.myitschool.controllers;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.myitschool.entity.Users;
import ru.myitschool.repositories.UsersRepository;

@RestController
@RequestMapping("users")
public class UsersController {

    @Autowired
    private UsersRepository user;
    @RequestMapping(value = "/create", method=RequestMethod.PUT, consumes="text/plain")
    public int createParent(@RequestBody String param){
        String parent_phone_number = null;
        String child_phone_number=null;
        String unique_code=null;
        try{
            JSONObject json = new JSONObject(param);
            parent_phone_number = json.getString("parent_phone_number");
            child_phone_number = json.getString("chile_phone_number");
            unique_code=json.getString("unique_code");
        }catch(JSONException e){
            e.getLocalizedMessage();
            return 0;
        }
        return user.createUser(parent_phone_number,child_phone_number,unique_code);
    }

    @RequestMapping(value = "/activate", method=RequestMethod.PUT, consumes="text/plain")
    public int activateChildren(@RequestBody String param){
        String unique_code=null;
        try{
            JSONObject json = new JSONObject(param);
            unique_code=json.getString("unique_code");
        }catch(JSONException e){
            e.getLocalizedMessage();
            return 0;
        }
        return user.activateUser(unique_code);
    }
    @RequestMapping(value = "/getuser", method=RequestMethod.GET)
    public Users getUser(@RequestParam("unique_code") String unique_code) {
        //Integer id =null;
        //JSONArray jsonArray;
        return user.getIdUser(unique_code);
    }
}