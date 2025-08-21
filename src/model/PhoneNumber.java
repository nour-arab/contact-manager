package model;

import java.io.Serializable;

public class PhoneNumber implements Serializable {
    private final int regionCode;
    private final int number;

    public PhoneNumber(int regionCode, int number) {
        this.regionCode = regionCode;
        this.number = number;
    }

    public int getRegionCode() { return regionCode; }
    public int getNumber()    { return number;    }

    @Override
    public String toString() {
        
        return String.format("%02d-%06d", regionCode, number);
    }
}
