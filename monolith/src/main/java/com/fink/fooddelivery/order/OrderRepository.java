package com.fink.fooddelivery.order;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    @Query("select o from Order o "
            + "left join fetch o.items i "
            + "left join fetch i.menuItem "
            + "left join fetch o.restaurant "
            + "where o.id = :id")
    Optional<Order> findWithItemsById(@Param("id") Long id);

    @Query("select distinct o from Order o "
            + "left join fetch o.items i "
            + "left join fetch i.menuItem "
            + "left join fetch o.restaurant "
            + "where o.user.id = :userId "
            + "order by o.createdAt desc")
    List<Order> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId);
}
