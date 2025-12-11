package domain.backend.activity.repository;

import domain.backend.activity.domain.Activity;
import domain.backend.activity.domain.Status;
import domain.backend.auth.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, Long> {
    Page<Activity> findAllByStatus(Status status, Pageable pageable);
    Page<Activity> findAllByCreatedByAndStatusIn(User user, Collection<Status> statuses, Pageable pageable);

    @Query("SELECT a FROM Activity a " +
           "LEFT JOIN FETCH a.hostedByList h " +
           "LEFT JOIN FETCH h.organization " +
           "LEFT JOIN FETCH a.wastes " +
           "LEFT JOIN FETCH a.pictures " +
           "WHERE a.activityId = :id")
    Optional<Activity> findByIdWithAllDetails(@Param("id") Long id);
}
