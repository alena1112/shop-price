package ru.shop.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.shop.service.PriceHelper;

import javax.persistence.*;
import javax.persistence.Entity;

@Entity
@Table(name = "material")
@Getter
@Setter
@NoArgsConstructor
public class Material extends IdentifiableEntity {
    @Column(name = "name")
    private String name;

    @Column(name = "price")
    private Double price;

    @Column(name = "delivery")
    private Double delivery;

    @Column(name = "number")
    private Integer number;

    @Column(name = "image_url")
    private String imageURL;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_order_id")
    private MaterialOrder order;

    public Material(Long id) {
        this.id = id;
    }

    @Transient
    public Double getUnitPriceWithDelivery() {
        return PriceHelper.round((price + delivery) / number);
    }
}
