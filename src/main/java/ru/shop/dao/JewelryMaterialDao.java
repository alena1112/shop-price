package ru.shop.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.shop.model.JewelryMaterial;
import ru.shop.model.JewelryMaterialId;

import java.util.List;

public interface JewelryMaterialDao extends JpaRepository<JewelryMaterial, JewelryMaterialId> {
    void deleteAllByJewelry(Long jewelry);

    @Modifying
    @Query(value = "insert into jewelry_material(jewelry_id, material_id, number) values (:jewelryId, :materialId, :number)",
            nativeQuery = true)
    void saveJewelryMaterial(@Param("jewelryId") Long jewelryId, @Param("materialId") Long materialId,
                             @Param("number") Integer number);

    @Query("select jm from JewelryMaterial jm where jm.jewelry = :jewelryId")
    List<JewelryMaterial> getMaterialsByJewelryId(@Param("jewelryId") Long jewelryId);
}
