package com.sar2016.panczuk.monstersgo;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Random;

/**
 * Created by olivier on 18/01/17.
 */

public class Monster {
    Marker marker;
    String name;
    String imageName;
    int id;
    int level;
    String user;
    HashMap<String,String> monsterImages = new HashMap<>();

    public Monster(String name){
        this.populateHashMap();
        this.name = name;
        this.imageName = monsterImages.get(name);
        this.level = 1 + (int)(Math.random() * (5 - 1));
    }

    public void setMarker(Marker marker) {
        this.marker = marker;
    }

    public String getImageName() {
        return imageName;
    }

    public String getName() {
        return name;
    }

    public Marker getMarker() {
        return marker;
    }

    public static Monster getRandomMonster(){
        Random random = new Random();

        int randomNumber = random.nextInt(10 - 0) + 0;

        switch(randomNumber){
            case 0:
                return new Monster("Ankylausaurus");
            case 1:
                return new Monster("Brachiosaurus");
            case 2:
                return new Monster("Ceratosaurus");
            case 3:
                return new Monster("Paracelorophus");
            case 4:
                return new Monster("Pteranodon");
            case 5:
                return new Monster("Spinausaurus");
            case 6:
                return new Monster("Stegausaurus");
            case 7:
                return new Monster("Tyranosaurus");
            case 8:
                return new Monster("Triceratops");
            case 9:
                return new Monster("Velociraptor Male");
            case 10:
                return new Monster("Velociraptor Female");
            default:
                return new Monster("Tyranosaurus");
        }
    }

    private void populateHashMap(){
        this.monsterImages.put("Ankylausaurus", "ankylausaurus");
        this.monsterImages.put("Brachiosaurus", "brachiosaurus");
        this.monsterImages.put("Ceratosaurus", "ceratosaurus");
        this.monsterImages.put("Paracelorophus", "paracelorophus");
        this.monsterImages.put("Pteranodon", "pteranodon");
        this.monsterImages.put("Spinausaurus", "spinausaurus");
        this.monsterImages.put("Stegausaurus", "stegausaurus");
        this.monsterImages.put("Tyranosaurus", "trex");
        this.monsterImages.put("Triceratops", "triceratops");
        this.monsterImages.put("Velociraptor Male", "velociraptor");
        this.monsterImages.put("Velociraptor Female", "velociraptor2");
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public int getLevel(){
        return this.level;
    }

    public void setLevel(int level) {
        this.level = level;
    }
}
