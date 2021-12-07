package comedy.reposytories;

import comedy.model.Header;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HeaderRepository extends CrudRepository<Header, String> {
}
