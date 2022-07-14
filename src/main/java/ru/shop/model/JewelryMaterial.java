package ru.shop.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Table(name = "jewelry_material")
@Getter
@Setter
public class JewelryMaterial extends IdentifiableEntity {
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "jewelry_id")
    @Column(name = "jewelry_id")
    private Long jewelry;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_id")
    private Material material;

    @Column(name = "number")
    private Integer number;
}
