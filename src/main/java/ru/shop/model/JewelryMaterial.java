package ru.shop.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "jewelry_material")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JewelryMaterial implements Serializable {
    @EmbeddedId
    private JewelryMaterialId jewelryMaterialId;

    @Column(name = "number")
    private Integer number;
}
