package com.orderme.backend.menu;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "menu_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mealid;

    private String pictureofmeal;
    private String mealname;
    private Double price;
    private String mealcontent;
    private String category;
    private String additionalproducts;
}
