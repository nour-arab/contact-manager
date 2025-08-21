package model;

import java.io.Serializable;    
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Contact implements Serializable {
    private String firstName;
    private String lastName;
    private String city;
    private List<PhoneNumber> phoneNumbers;

    public Contact(String firstName, String lastName, String city) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.phoneNumbers = new ArrayList<>();
    }

    public Contact(String firstName, String lastName, String city, List<PhoneNumber> phoneNumbers) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.city = city;
        this.phoneNumbers = phoneNumbers != null ? phoneNumbers : new ArrayList<>();
    }

    public void addPhoneNumber(PhoneNumber number) {
        this.phoneNumbers.add(number);
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getCity() { return city; }
    public List<PhoneNumber> getPhoneNumbers() { return phoneNumbers; }

    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setCity(String city) { this.city = city; }
    public void setPhoneNumbers(List<PhoneNumber> phoneNumbers) {
        this.phoneNumbers = phoneNumbers;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Contact contact = (Contact) o;
        
        // Two contacts are considered equal if they have the same name
        return firstName.equals(contact.firstName) && 
               lastName.equals(contact.lastName);
    }


    @Override
    public String toString() {
        return firstName + " " + lastName + " - " + city ;
    }
    
}
