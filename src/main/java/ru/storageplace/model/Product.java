package ru.storageplace.model;

import java.math.BigDecimal;

public class Product {
    private Long id;
    private String name;
    private String article;
    private BigDecimal lengthCm;
    private BigDecimal widthCm;
    private BigDecimal heightCm;
    private BigDecimal weightKg;

    public Product() {
    }

    public Product(Long id, String name, String article,
                   BigDecimal lengthCm, BigDecimal widthCm,
                   BigDecimal heightCm, BigDecimal weightKg) {
        this.id = id;
        this.name = name;
        this.article = article;
        this.lengthCm = lengthCm;
        this.widthCm = widthCm;
        this.heightCm = heightCm;
        this.weightKg = weightKg;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArticle() {
        return article;
    }

    public void setArticle(String article) {
        this.article = article;
    }

    public BigDecimal getLengthCm() {
        return lengthCm;
    }

    public void setLengthCm(BigDecimal lengthCm) {
        this.lengthCm = lengthCm;
    }

    public BigDecimal getWidthCm() {
        return widthCm;
    }

    public void setWidthCm(BigDecimal widthCm) {
        this.widthCm = widthCm;
    }

    public BigDecimal getHeightCm() {
        return heightCm;
    }

    public void setHeightCm(BigDecimal heightCm) {
        this.heightCm = heightCm;
    }

    public BigDecimal getWeightKg() {
        return weightKg;
    }

    public void setWeightKg(BigDecimal weightKg) {
        this.weightKg = weightKg;
    }

    @Override
    public String toString() {
        return name + " (" + article + ")";
    }
}