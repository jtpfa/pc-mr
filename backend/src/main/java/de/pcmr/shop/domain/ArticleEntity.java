package de.pcmr.shop.domain;

import org.hibernate.validator.constraints.Range;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.validation.constraints.Size;

import static de.pcmr.shop.domain.AbstractEntity.TABLE_PREFIX;

/**
 * The article entity. It represents the articles that can be ordered.
 * Includes validation constraints.
 *
 * @author Fynn Lohse
 */

@Entity
@Table(name = TABLE_PREFIX + "articles")
public class ArticleEntity extends AbstractEntity {

    @Column(nullable = false)
    @Size(min = 4, max = 255, message = "Der Name muss zwischen {min} und {max} Zeichen lang sein")
    private String name;

    @Column(nullable = false, length = 4096)
    @Size(min = 4, max = 4096, message = "Die Kurzbeschreibung muss zwischen {min} und {max} Zeichen lang sein")
    @Lob
    private String description;

    @Column(nullable = false, length = 32768)
    @Size(min = 4, max = 32768, message = "Die Details müssen zwischen {min} und {max} Zeichen lang sein")
    @Lob
    private String details;

    @Column(nullable = false)
    @Range(min = 0, max = 99999999, message = "Der Preis muss zwischen {min} und {max} Cent liegen")
    private Integer price;

    @Column(nullable = false)
    @Range(min = 0, max = 999999, message = "Der Lagerbestand muss zwischen {min} und {max} liegen")
    private Integer stock;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public Integer getPrice() {
        return price;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }
}
