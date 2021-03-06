package havag.gifthing.team

import havag.gifthing.models.team.dto.TeamRequest
import havag.gifthing.models.team.dto.TeamResponse
import org.springframework.http.HttpStatus

interface ITeamService {

	fun findById(teamId: Long): TeamResponse?
	fun create(team: TeamRequest): TeamResponse
	fun getMyTeams(): MutableList<TeamResponse>
	/*
	fun update(team: TeamRequest): TeamResponse?
	fun delete(teamId: Long): HttpStatus
	fun addMember(teamId: Long, userId: Long): TeamResponse?
	fun removeMember(teamId: Long, userId: Long): TeamResponse?
	fun findAll(): MutableIterable<TeamResponse>
	 */
}