package ru.shop.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.shop.model.Material;

public interface MaterialDao extends JpaRepository<Material, Long> {
}
