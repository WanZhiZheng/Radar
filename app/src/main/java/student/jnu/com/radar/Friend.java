package student.jnu.com.radar;

import java.io.Serializable;

/**
 * Created by ASUS on 2018/12/14.
 */

public class Friend implements Serializable{
    private  String name;
    private  String number;
    private String latitude;
    private String longitude;

    public Friend(String name,String number,String latitude,String longitude){
        this.name=name;
        this.number=number;
        this.latitude=latitude;
        this.longitude=longitude;
    }
    public String getName(){
        return name;
    }

    public String getNumber(){
        return number;
    }

    public void setName(String name){
        this.name=name;
    }

    public void setNumber(String number){
        this.number=number;
    }

    public void setLatitude(String latitude){
        this.latitude=latitude;
    }
    public void setLongitude(String longitude){
        this.longitude=longitude;
    }

    public String getLatitude(){
        return latitude;
    }
    public String getLongitude(){
        return longitude;
    }

}
