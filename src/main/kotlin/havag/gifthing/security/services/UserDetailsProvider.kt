package havag.gifthing.security.services

import havag.gifthing.models.user.dto.UserResponse
import havag.gifthing.repositories.UserRepository
import havag.gifthing.user.UserService
import org.springframework.security.core.context.SecurityContextHolder

class UserDetailsProvider(val userRepository: UserRepository) {

	fun getUser(): UserResponse {
		val tmpUser = userRepository.findById(getUserDetail()!!.id)
		if (getUserDetail() == null || tmpUser.isEmpty) RuntimeException("USER_IS_NOT_LOGGED_IN")
		return tmpUser.get().toUserResponse()
	}

	private fun getUserDetail(): UserDetailsImpl? {
		return if (SecurityContextHolder.getContext()?.authentication?.principal is String) {
			null
		} else {
			SecurityContextHolder.getContext()?.authentication?.principal as UserDetailsImpl?
		}
	}
}
