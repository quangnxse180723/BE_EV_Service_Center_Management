package swp.group4.be_ev_service_center_management.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomerRequest {
    
    @Size(min = 2, max = 100, message = "Full name must be between 2 and 100 characters")
    private String fullName;
    
    @Pattern(regexp = "^(0[3|5|7|8|9])[0-9]{8}$", message = "Invalid Vietnamese phone number")
    private String phone;
    
    @Email(message = "Invalid email format")
    private String email;
    
    @Size(max = 255, message = "Address must not exceed 255 characters")
    private String address;
}
