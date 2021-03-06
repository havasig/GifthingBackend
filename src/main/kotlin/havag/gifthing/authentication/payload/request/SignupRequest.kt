package havag.gifthing.authentication.payload.request

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.Size

class SignupRequest (
	var username: @NotBlank @Size(min = 3, max = 20) String,
	var email: @NotBlank @Size(max = 50) @Email String,
	var password: @NotBlank @Size(min = 6, max = 40) String,
	var firstname: @NotBlank @Size(min = 3, max = 40) String? = null,
	var lastname: @NotBlank @Size(min = 3, max = 40) String? = null
)