package swp.group4.be_ev_service_center_management.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.time.LocalDate;

public class CustomerRegisterRequest implements Serializable {

    @NotBlank(message = "fullName is required")
    private String fullName;

    @NotBlank(message = "email is required")
    @Email(message = "email must be valid")
    private String email;

    @NotBlank(message = "phoneNumber is required")
    @Pattern(regexp = "^[0-9+\\-\\s]{7,20}$", message = "phoneNumber must contain only digits, +, - or spaces")
    private String phoneNumber;

    @NotBlank(message = "password is required")
    @Size(min = 6, max = 128, message = "password must be between 6 and 128 characters")
    private String password;

    @NotBlank(message = "confirmPassword is required")
    private String confirmPassword;

    private String address;

    @Past(message = "dateOfBirth must be in the past")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    @AssertTrue(message = "You must accept terms and conditions")
    private boolean acceptTerms;

    private String deviceInfo;
    private String preferredContactChannel; // e.g., "EMAIL", "SMS"

    public CustomerRegisterRequest() {}

    public CustomerRegisterRequest(String fullName, String email, String phoneNumber,
                                   String password, String confirmPassword, String address,
                                   LocalDate dateOfBirth, boolean acceptTerms,
                                   String deviceInfo, String preferredContactChannel) {
        this.fullName = fullName;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.confirmPassword = confirmPassword;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.acceptTerms = acceptTerms;
        this.deviceInfo = deviceInfo;
        this.preferredContactChannel = preferredContactChannel;
    }

    // getters / setters
    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getConfirmPassword() { return confirmPassword; }
    public void setConfirmPassword(String confirmPassword) { this.confirmPassword = confirmPassword; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public boolean isAcceptTerms() { return acceptTerms; }
    public void setAcceptTerms(boolean acceptTerms) { this.acceptTerms = acceptTerms; }

    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }

    public String getPreferredContactChannel() { return preferredContactChannel; }
    public void setPreferredContactChannel(String preferredContactChannel) { this.preferredContactChannel = preferredContactChannel; }

    @AssertTrue(message = "password and confirmPassword must match")
    public boolean isPasswordsMatching() {
        if (password == null) return false;
        return password.equals(confirmPassword);
    }
}