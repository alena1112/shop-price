package ru.shop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "jewelry_material")
@IdClass(JewelryMaterialId.class)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JewelryMaterial implements Serializable {
    @Id
    @Column(name = "jewelry_id")
    private Long jewelry;

    @Id
    @Column(name = "material_id", insertable=false, updatable=false)
    private Long materialId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private Material material;

    @Column(name = "number")
    private Integer number;
}
