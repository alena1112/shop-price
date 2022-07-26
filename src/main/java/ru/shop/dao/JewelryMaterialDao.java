package ru.shop.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.shop.model.JewelryMaterial;
import ru.shop.model.JewelryMaterialId;

import java.util.List;

public interface JewelryMaterialDao extends JpaRepository<JewelryMaterial, JewelryMaterialId> {
    void deleteAllByJewelryMaterialId_Jewelry(Long jewelry);

    @Query("select jm from JewelryMaterial jm where jm.jewelryMaterialId.jewelry = :jewelryId")
    List<JewelryMaterial> getMaterialsByJewelryId(@Param("jewelryId") Long jewelryId);
}
