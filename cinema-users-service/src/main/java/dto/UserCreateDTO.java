package dto;

import enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class UserCreateDTO {

    @NotBlank(message = "You should enter fist name")
    private String firstName;

    @NotBlank(message = "You should enter last name")
    private String lastName;

    @Email(message = "Email format is invalid")
    @NotBlank(message = "You should enter email")
    private String email;

    @NotBlank(message = "You should enter password")
    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d).{6,}$",
            message = "Password must contain at least 6 characters and one digit"
    )
    private String password;

    private String phoneNumber;
    private String dateOfBirth;

    Role role;

    public UserCreateDTO(String firstName, String lastName, String email,
                         String password, String phoneNumber, String dateOfBirth,
                         Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.phoneNumber = phoneNumber;
        this.dateOfBirth = dateOfBirth;
        this.role = role;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public Role getRole() {
        return role;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setRole(Role role) {
        this.role = role;
    }
}
