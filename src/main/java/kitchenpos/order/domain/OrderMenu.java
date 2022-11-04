package kitchenpos.order.domain;

import java.math.BigDecimal;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import kitchenpos.common.domain.Price;
import kitchenpos.menu.domain.Menu;

@Entity
public class OrderMenu {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @Embedded
    private Price price;

    protected OrderMenu() {
    }

    public OrderMenu(final String name, final BigDecimal price) {
        this.name = name;
        this.price = new Price(price);
    }

    public static OrderMenu from(final Menu menu) {
        return new OrderMenu(menu.getName(), menu.getPrice());
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price.getPrice();
    }
}