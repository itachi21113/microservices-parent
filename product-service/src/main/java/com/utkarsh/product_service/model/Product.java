package com.utkarsh.product_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document; // This import is important
import java.math.BigDecimal;

// This annotation is CRITICAL.
// It links this class to your "ecommers" collection in MongoDB.
@Document(value = "ecommers")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class Product {

    @Id // Marks this field as the unique ID
    private String id;

    private String name;
    private String description;
    private BigDecimal price;
}