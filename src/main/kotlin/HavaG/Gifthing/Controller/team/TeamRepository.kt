package HavaG.Gifthing.controller.team

import HavaG.Gifthing.models.Team
import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository

@Repository
interface TeamRepository : CrudRepository<Team, Long>{
    //TODO: TeamRepository
}