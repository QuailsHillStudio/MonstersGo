package com.sar2016.panczuk.monstersgo;

import com.google.android.gms.maps.model.Marker;

import java.util.Random;

/**
 * Created by olivier on 18/01/17.
 */

public class Monster {
    Marker marker;
    String name;
    String imageName;

    public Monster(String name, String imageName){
        this.name = name;
        this.imageName = imageName;
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
                return new Monster("Ankylausaurus", "ankylausaurus");
            case 1:
                return new Monster("Brachiosaurus", "brachiosaurus");
            case 2:
                return new Monster("Ceratosaurus", "ceratosaurus");
            case 3:
                return new Monster("Paracelorophus", "paracelorophus");
            case 4:
                return new Monster("Pteranodon", "pteranodon");
            case 5:
                return new Monster("Spinausaurus", "spinausaurus");
            case 6:
                return new Monster("Stegausaurus", "stegausaurus");
            case 7:
                return new Monster("Tyranosaurus", "trex");
            case 8:
                return new Monster("Triceratops", "triceratops");
            case 9:
                return new Monster("Velociraptor Male", "velociraptor");
            case 10:
                return new Monster("Velociraptor Female", "velociraptor2");
            default:
                return new Monster("Tyranosaurus", "trex");
        }
    }
}
