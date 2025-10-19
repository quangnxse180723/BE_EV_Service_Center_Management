package swp.group4.be_ev_service_center_management.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO for customer login.
 * Fields:
 * - usernameOrEmail: username or email used to authenticate (required)
 * - password: plain password entered (required, validated length; encoding/verification occurs in service)
 * - rememberMe: optional flag to request long-lived session
 * - deviceInfo: optional client/device metadata (e.g., browser, app version) for logging/notifications
 */
public record CustomerLoginRequest(
        @NotBlank(message = "usernameOrEmail is required")
        String usernameOrEmail,

        @NotBlank(message = "password is required")
        @Size(min = 6, max = 128, message = "password must be between 6 and 128 characters")
        String password,

        boolean rememberMe,

        String deviceInfo
) {}