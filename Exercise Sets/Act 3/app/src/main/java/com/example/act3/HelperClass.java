package com.example.act3;

public class HelperClass {
    private String username;
    private String email;
    private String password;
    private String interest;
    private Integer phone;
    private Long birthtime;
    private Long birthdate;

    // Default constructor required for calls to DataSnapshot.getValue(HelperClass.class)
    public HelperClass() {}

    public HelperClass(String username, String email, String password, String interest, Integer phone, Long birthtime, Long birthdate) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.interest = interest;
        this.phone = phone;
        this.birthtime = birthtime;
        this.birthdate = birthdate;
    }

    // Getters and Setters
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getInterest() { return interest; }
    public void setInterest(String interest) { this.interest = interest; }

    public Integer getPhone() { return phone; }
    public void setPhone(Integer phone) { this.phone = phone; }

    public Long getBirthtime() { return birthtime; }
    public void setBirthtime(Long birthtime) { this.birthtime = birthtime; }

    public Long getBirthdate() { return birthdate; }
    public void setBirthdate(Long birthdate) { this.birthdate = birthdate; }
}
