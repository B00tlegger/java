package comedy.reposytories;

import comedy.model.Voting;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VotingRepository extends CrudRepository<Voting, Integer> {
}
