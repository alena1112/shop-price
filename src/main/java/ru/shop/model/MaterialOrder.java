package ru.shop.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "material_order")
@Getter
@Setter
@ToString
public class MaterialOrder extends IdentifiableEntity {
    private static SimpleDateFormat FORMAT_DATE = new SimpleDateFormat("dd.MM.yyyy");

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<Material> materials;

    @Column(name = "delivery_price")
    private double deliveryPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "shop")
    private Shop shop;

    @Column(name = "purchase_date")
    private Date purchaseDate;

    public String getFormatOrder() {
        return String.format("%s, дата %s, доставка %s", shop.name(), FORMAT_DATE.format(purchaseDate), deliveryPrice);
    }
}
