package it.angelo.MyCartellaClinicaElettronica.user.repositories;

import it.angelo.MyCartellaClinicaElettronica.user.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * is the repository of the user
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * search user by email
     * @param email email of the user
     * @return an email
     */
    User findByEmail(String email);

    /**
     * search user by activationCode
     * @param activationCode for enable user
     * @return an activationCode
     */
    User findByActivationCode(String activationCode);

    /**
     * search user by PasswordResetCode
     * @param passwordResetCode for reset password
     * @return a passwordResetCode
     */
    User findByPasswordResetCode(String passwordResetCode);

    /**
     * @deprecated this method belongs to another exercise
     * @return a rider
     */
    @Query(nativeQuery = true, value = "SELECT * FROM (\n" +
            "SELECT u.*, COUNT(busyOrders.id) AS numberOfOrders\n" +
            "FROM `user`AS u\n" +
            "LEFT JOIN user_roles AS ur ON ur.user_id = u.id\n" +
            "LEFT JOIN (SELECT * FROM `orders` WHERE `status` IN(4)) AS busyOrders ON busyOrders.rider_id = u.id\n" +
            "WHERE ur.role_id = 3 AND u.is_active = 1\n" +
            "GROUP BY u.id\n" +
            ") AS allRiders\n" +
            "WHERE allRiders.numberOfOrders = 0 \n" +
            "LIMIT 1")
    Optional<User> pickRider();

    /**
     * assign the first available Doctor to the appointment
     * @return an available Doctor
     */
    @Query(nativeQuery = true, value = "SELECT * FROM (\n" +
            "SELECT u.*, COUNT(busyAppointment.id) AS numberOfAppointment\n" +
            "FROM `user`AS u\n" +
            "LEFT JOIN user_roles AS ur ON ur.user_id = u.id\n" +
            "LEFT JOIN (SELECT * FROM `appointment` WHERE `status` IN(4)) AS busyAppointment ON busyAppointment.doctor_id = u.id\n" +
            "WHERE ur.role_id = 3 AND u.is_active = 1\n" +
            "GROUP BY u.id\n" +
            ") AS allDoctor\n" +
            "WHERE allDoctor.numberOfAppointment = 0 \n" +
            "LIMIT 1")
    Optional<User> pickDoctor();

    User findUserById(Long id);

    @Query(nativeQuery = true, value = "SELECT ur.role_id FROM user_roles AS ur\n" +
            "WHERE ur.user_id = :id")
    Long getSingleUserIdRoleId(@Param(value = "id")Long id);

    @Query(nativeQuery = true, value = "SELECT ur.user_id FROM user_roles AS ur\n" +
            "WHERE ur.role_id = 4")
    List<Long> getAllUserIdByRoleId();
}
