package stringgenerator.Database;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UniqueStringsRepository extends JpaRepository<UniqueStrings, Integer> {
}
