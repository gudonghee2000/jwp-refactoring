package kitchenpos.application;

import static kitchenpos.common.constants.Constants.데시멀_야채곱창_가격;
import static kitchenpos.common.constants.Constants.루나세트_이름;
import static kitchenpos.common.constants.Constants.야채곱창_가격;
import static kitchenpos.common.constants.Constants.야채곱창_수량;
import static kitchenpos.common.constants.Constants.야채곱창_이름;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import java.util.List;
import kitchenpos.common.builder.MenuBuilder;
import kitchenpos.common.builder.MenuGroupBuilder;
import kitchenpos.common.builder.MenuProductBuilder;
import kitchenpos.common.builder.ProductBuilder;
import kitchenpos.domain.Menu;
import kitchenpos.domain.MenuGroup;
import kitchenpos.domain.MenuProduct;
import kitchenpos.domain.Product;
import kitchenpos.dto.request.MenuCreateRequest;
import kitchenpos.dto.request.MenuProductCreateRequest;
import kitchenpos.dto.response.MenuResponse;
import kitchenpos.dto.response.MenusResponse;
import kitchenpos.repository.MenuGroupRepository;
import kitchenpos.repository.MenuRepository;
import kitchenpos.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.InvalidDataAccessApiUsageException;

public class MenuServiceTest extends ServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private MenuGroupRepository menuGroupRepository;

    private Product 야채곱창;
    private MenuGroup 루나세트;
    private MenuProduct 루나_야채곱창;

    @BeforeEach
    void setUp() {
        야채곱창 = new ProductBuilder()
                .name(야채곱창_이름)
                .price(BigDecimal.valueOf(야채곱창_가격))
                .build();
        야채곱창 = productRepository.save(야채곱창);

        루나_야채곱창 = new MenuProductBuilder()
                .product(야채곱창)
                .quantity(1)
                .build();

        루나세트 = new MenuGroupBuilder()
                .name(루나세트_이름)
                .build();
        루나세트 = menuGroupRepository.save(루나세트);
    }

    @DisplayName("메뉴를 등록한다.")
    @Test
    void 메뉴를_등록한다() {
        // given
        MenuProductCreateRequest 메뉴_상품_등록_요청 = new MenuProductCreateRequest(야채곱창.getId(), 야채곱창_수량);
        MenuCreateRequest 야채곱창_메뉴_생성_요청 = new MenuCreateRequest(야채곱창_이름, 데시멀_야채곱창_가격, 루나세트.getId(),
                List.of(메뉴_상품_등록_요청));

        // when
        MenuResponse actual = menuService.create(야채곱창_메뉴_생성_요청);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(야채곱창_이름)
        );
    }

    @DisplayName("메뉴를 등록할 때, 가격이 0원 보다 작으면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, -2, -100})
    void 메뉴를_등록할_때_가격이_0원_보다_작으면_예외가_발생한다(int 잘못된_가격) {
        // given
        MenuProductCreateRequest 메뉴_상품_등록_요청 = new MenuProductCreateRequest(야채곱창.getId(), 야채곱창_수량);
        MenuCreateRequest 야채곱창_메뉴_생성_요청 = new MenuCreateRequest(야채곱창_이름, BigDecimal.valueOf(잘못된_가격), 루나세트.getId(),
                List.of(메뉴_상품_등록_요청));

        // when & then
        assertThatThrownBy(() -> menuService.create(야채곱창_메뉴_생성_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 등록할 때, 가격이 null 이면 예외가 발생한다.")
    @Test
    void 메뉴를_등록할_때_가격이_null_이면_예외가_발생한다() {
        // given
        MenuProductCreateRequest 메뉴_상품_등록_요청 = new MenuProductCreateRequest(야채곱창.getId(), 야채곱창_수량);
        MenuCreateRequest 야채곱창_메뉴_생성_요청 = new MenuCreateRequest(야채곱창_이름, null, 루나세트.getId(), List.of(메뉴_상품_등록_요청));

        // when & then
        assertThatThrownBy(() -> menuService.create(야채곱창_메뉴_생성_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 등록할 때, 메뉴 그룹이 존재하지 않으면 예외가 발생한다.")
    @Test
    void 메뉴를_등록할_때_메뉴_그룹이_존재하지_않으면_예외가_발생한다() {
        // given
        Long 잘못된_메뉴그룹_아이디 = -1L;
        MenuProductCreateRequest 메뉴_상품_등록_요청 = new MenuProductCreateRequest(야채곱창.getId(), 야채곱창_수량);
        MenuCreateRequest 야채곱창_메뉴_생성_요청 = new MenuCreateRequest(야채곱창_이름, 데시멀_야채곱창_가격, 잘못된_메뉴그룹_아이디,
                List.of(메뉴_상품_등록_요청));

        // when & then
        assertThatThrownBy(() -> menuService.create(야채곱창_메뉴_생성_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴를 등록할 때, 등록되지 않은 상품이면 예외가 발생한다.")
    @Test
    void 메뉴를_등록할_때_등록되지_않은_상품이면_예외가_발생한다() {
        // given
        Long 등록되지_않은_상품_아이디 = 999L;
        MenuProductCreateRequest 메뉴_상품_등록_요청 = new MenuProductCreateRequest(등록되지_않은_상품_아이디, 야채곱창_수량);
        MenuCreateRequest 야채곱창_메뉴_생성_요청 = new MenuCreateRequest(야채곱창_이름, 데시멀_야채곱창_가격, 루나세트.getId(),
                List.of(메뉴_상품_등록_요청));

        // when & then
        assertThatThrownBy(() -> menuService.create(야채곱창_메뉴_생성_요청))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
    }

    @DisplayName("메뉴를 등록할 때, 메뉴 가격이 메뉴상품 가격 합보다 크면 예외가 발생한다.")
    @Test
    void 메뉴를_등록할_때_메뉴_가격이_메뉴상품_가격_합보다_크면_예외가_발생한다() {
        // given
        BigDecimal 흑자_가격 = BigDecimal.valueOf(20000);
        MenuProductCreateRequest 메뉴_상품_등록_요청 = new MenuProductCreateRequest(야채곱창.getId(), 야채곱창_수량);
        MenuCreateRequest 야채곱창_메뉴_생성_요청 = new MenuCreateRequest(야채곱창_이름, 흑자_가격, 루나세트.getId(), List.of(메뉴_상품_등록_요청));

        // when & then
        assertThatThrownBy(() -> menuService.create(야채곱창_메뉴_생성_요청))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @DisplayName("메뉴 목록을 조회한다.")
    @Test
    void 메뉴_목록을_조회한다() {
        // given
        Menu 야채곱창_메뉴 = 메뉴_생성(야채곱창_이름, 야채곱창_가격, 루나세트.getId(), List.of(루나_야채곱창));
        menuRepository.save(야채곱창_메뉴);

        // when
        MenusResponse 메뉴들 = menuService.list();

        // then
        assertThat(메뉴들.getMenuResponses()).hasSize(1);
    }

    private Menu 메뉴_생성(final String name, final int price, final Long menuGroupId,
                       final List<MenuProduct> products) {
        return new MenuBuilder()
                .name(name)
                .price(BigDecimal.valueOf(price))
                .menuGroupId(menuGroupId)
                .menuProducts(products)
                .build();
    }

    private MenuProduct 메뉴상품_생성(final Product product, final long quantity) {
        return new MenuProductBuilder()
                .product(product)
                .quantity(quantity)
                .build();
    }
}
