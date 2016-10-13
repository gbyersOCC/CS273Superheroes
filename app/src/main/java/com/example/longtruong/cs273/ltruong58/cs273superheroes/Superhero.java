package com.example.longtruong.cs273.ltruong58.cs273superheroes;

import java.util.ArrayList;

/** Superhero class
 * Store and process data for superhero object
 * Created by Long Truong on 10/10/2016.
 */

public class Superhero {
    static final String SUPERHERO_NAME = "Superhero_Name";
    static final String SUPERPOWER = "Superpower";
    static final String ONE_THING = "One_Thing";

    private String Name;
    private String Username;
    private String Superpower;
    private String OneThing;
    private String ImageName;

    // Suppose that all image names are username with extension png. (I already fixed some records in the Json file)
    /** get Name of Superhero
     * @return
     */
    public String getName() {
        return Name;
    }

    /** Set Name of Superhero
     * @param name
     */
    public void setName(String name) {
        Name = name;
    }

    /** Get Userame of Superhero
     * @return
     */
    public String getUsername() {
        return Username;
    }

    /** Set Username of Superhero
     * @param username
     */
    public void setUsername(String username) {
        Username = username;
    }

    /** Get Superpower of Superhero
     * @return
     */
    public String getSuperpower() {
        return Superpower;
    }

    /** Set Superpower of Superhero
     * @param superpower
     */
    public void setSuperpower(String superpower) {
        Superpower = superpower;
    }

    /** Get One Thing of Superhero
     * @return
     */
    public String getOneThing() {
        return OneThing;
    }

    /** Set One thing of Superhero
     * @param oneThing
     */
    public void setOneThing(String oneThing) {
        OneThing = oneThing;
    }

    /** Get ImageName of Superhero
     * @return
     */
    public String getImageName() {
        return ImageName;
    }

    /** Set ImageName of Superhero
     * @param imageName
     */
    public void setImageName(String imageName) {
        ImageName = imageName;
    }

    /** Get attribute of Superhero base on quizType
     * @param quizType
     * @return
     */
    public  String getButtonText(String quizType) {
        switch(quizType){
            case SUPERHERO_NAME:
                return getName();

            case SUPERPOWER:
                return getSuperpower();

            case ONE_THING:
                return  getOneThing();

        }
        return "";
    }

}

