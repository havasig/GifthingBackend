package havag.gifthing.authentication

import havag.gifthing.authentication.payload.request.LoginRequest
import havag.gifthing.authentication.payload.request.RolesRequest
import havag.gifthing.authentication.payload.request.SignupRequest
import havag.gifthing.authentication.payload.response.JwtResponse
import havag.gifthing.authentication.payload.response.MessageResponse
import havag.gifthing.repositories.RoleRepository
import havag.gifthing.repositories.UserRepository
import havag.gifthing.models.ERole
import havag.gifthing.models.Role
import havag.gifthing.models.user.User
import havag.gifthing.security.jwt.JwtUtils
import havag.gifthing.security.services.UserDetailsImpl
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.function.Consumer
import java.util.stream.Collectors
import javax.validation.Valid
import kotlin.collections.HashSet


@CrossOrigin(origins = ["*"], maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
class AuthController {
	@Autowired
	var authenticationManager: AuthenticationManager? = null

	@Autowired
	var userRepository: UserRepository? = null

	@Autowired
	var roleRepository: RoleRepository? = null

	@Autowired
	var encoder: PasswordEncoder? = null

	@Autowired
	var jwtUtils: JwtUtils? = null

	@PostMapping("/login")
	fun authenticateUser(@RequestBody loginRequest: @Valid LoginRequest?): ResponseEntity<*> {
		val authentication = authenticationManager!!.authenticate(
			UsernamePasswordAuthenticationToken(loginRequest!!.username, loginRequest.password)
		)
		SecurityContextHolder.getContext().authentication = authentication
		val jwt = jwtUtils!!.generateJwtToken(authentication)
		val userDetails = authentication.principal as UserDetailsImpl
		val roles = userDetails.authorities.stream()
			.map { item: GrantedAuthority -> item.authority }
			.collect(Collectors.toList())
		return ResponseEntity.ok(
			JwtResponse(
				jwt,
				userDetails.id,
				userDetails.username,
				userDetails.email,
				roles
			)
		)
	}

	@PostMapping("/signup")
	fun registerUser(@RequestBody signUpRequest: @Valid SignupRequest?): ResponseEntity<*> {
		if (userRepository!!.existsByUsername(signUpRequest!!.username)!!) {
			return ResponseEntity
				.badRequest()
				.body(MessageResponse("Error: Username is already taken!"))
		}
		if (userRepository!!.existsByEmail(signUpRequest.email)!!) {
			return ResponseEntity
				.badRequest()
				.body(MessageResponse("Error: Email is already in use!"))
		}

		val user = User(
			signUpRequest.username!!,
			signUpRequest.email!!,
			encoder!!.encode(signUpRequest.password)
		)
		val roles: MutableSet<Role> = HashSet<Role>()
		val userRole: Role = roleRepository!!.findByName(ERole.ROLE_USER)
			.orElseThrow { RuntimeException("Error: User role is not found.") }
		roles.add(userRole)
		user.setRoles(roles)
		userRepository!!.save(user)
		return ResponseEntity.ok(MessageResponse("User registered successfully!"))
	}

	@PostMapping("/set-roles/{id}")
	@PreAuthorize("hasRole('ADMIN')")
	fun setAdmin(@PathVariable(value = "id") id: Long, @RequestBody rolesRequest: @Valid RolesRequest?): ResponseEntity<*> {
		val user = userRepository!!.findById(id)
		if(user.isPresent) {
			val realUser = user.get()

			val strRoles: Set<String> =  rolesRequest!!.roles!!
			val newRoles: MutableSet<Role> = HashSet<Role>()
			strRoles.forEach(Consumer { role: String? ->
				when (role) {
					"admin" -> {
						val adminRole: Role = roleRepository!!.findByName(ERole.ROLE_ADMIN)
							.orElseThrow { RuntimeException("Error: Role is not found.") }
						newRoles.add(adminRole)
					}
					"mod" -> {
						val modRole: Role = roleRepository!!.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow { RuntimeException("Error: Role is not found.") }
						newRoles.add(modRole)
					}
					"user" -> {
						val userRole: Role = roleRepository!!.findByName(ERole.ROLE_USER)
							.orElseThrow { RuntimeException("Error: Role is not found.") }
						newRoles.add(userRole)
					}
					else -> { }
					//TODO: not found
				}
			})
			realUser.setRoles(newRoles)
			userRepository!!.save(realUser)
			return ResponseEntity.ok(MessageResponse("User roles updated successfully!"))
		} else {
			return ResponseEntity
				.badRequest()
				.body(MessageResponse("Error: User not exists!"))
		}
	}

	/* TODO: logout
	@PostMapping("/logout")
	fun logout(@RequestBody logoutRequest: @Valid LogoutRequest?): ResponseEntity<*> {

	}
	 */
}
