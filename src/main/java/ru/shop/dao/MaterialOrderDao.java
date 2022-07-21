package ru.shop.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.shop.model.MaterialOrder;
import ru.shop.model.Shop;

import java.util.List;

public interface MaterialOrderDao extends JpaRepository<MaterialOrder, Long> {

    boolean existsMaterialOrderByName(String name);

    @Query("select mo.name from MaterialOrder mo where mo.shop = :shop")
    List<String> getAllOrderNames(@Param("shop") Shop shop);
}
