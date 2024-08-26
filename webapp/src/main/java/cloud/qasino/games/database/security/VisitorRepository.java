package cloud.qasino.games.database.security;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {

    // @formatter:off

    // counts
    Long countByAlias(String alias);

    // lifecycle of a visitor - aim to not used the dto's
    Visitor findOneByEmail(String email);
    @Query(value = "SELECT * FROM VISITOR u WHERE u.USERNAME = ?1", nativeQuery = true)
    Visitor findByUsername(@Param("username") String username);
    Optional<Visitor> findVisitorByVisitorId(Long visitorId);
    Optional<Visitor> findVisitorByAliasAndAliasSequence(String alias, int aliasSequence);
    @Query(value = "SELECT *        FROM VISITOR ORDER BY VISITOR_ID ",
      countQuery = "SELECT count(*) FROM VISITOR ",
            nativeQuery = true)
    Page<Visitor> findAllVisitorsWithPage(Pageable pageable);
    void removeUserByUsername(String username);
}
