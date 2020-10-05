package ru.myitschool.controllers;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.myitschool.repositories.LocationRepository;
import java.util.Date;

@RestController
@RequestMapping("location")
public class LocationController {


    @Autowired
    private LocationRepository locationRepository;

    @RequestMapping(value = "/addLocation", method=RequestMethod.PUT,consumes="text/plain")
    public int createLocation(@RequestBody String param){
        Double latitude = null;
        Double longitude=null;
        String timezone=null;
        Integer parent_id = null;
        try{
            JSONObject json = new JSONObject(param);
            latitude = json.getDouble("latitude");
            longitude = json.getDouble("longitude");
            timezone=json.getString("timezone");
            parent_id = json.getInt("idperson");
        }catch(JSONException e){
            e.getLocalizedMessage();
            return 0;
        }
        return locationRepository.createLocation(latitude,longitude,parent_id,timezone);
    }

    @RequestMapping(value = "/getlastlocations", method=RequestMethod.GET)
    public String getLastLocations(@RequestParam("idperson") Integer id){
        return locationRepository.getLocation(id).toString();
    }


    @RequestMapping(value = "/getlocations", method=RequestMethod.GET)
    public String getAllLocations(@RequestParam("idperson") Integer id,@RequestParam("date_before") String date_before,@RequestParam("date_after") String date_after){
        return locationRepository.getLocations(id,date_before,date_after).toString();
    }
}