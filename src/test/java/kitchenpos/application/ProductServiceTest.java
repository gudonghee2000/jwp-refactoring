package kitchenpos.application;

import static kitchenpos.common.constants.Constants.야채곱창_가격;
import static kitchenpos.common.constants.Constants.야채곱창_이름;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.math.BigDecimal;
import kitchenpos.common.builder.ProductBuilder;
import kitchenpos.domain.Product;
import kitchenpos.dto.request.ProductCreateRequest;
import kitchenpos.dto.response.ProductResponse;
import kitchenpos.dto.response.ProductsResponse;
import kitchenpos.dao.ProductDao;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;

class ProductServiceTest extends ServiceTest {

    private final ProductService productService;
    private final ProductDao productDao;

    @Autowired
    ProductServiceTest(final ProductService productService,
                       final ProductDao productDao) {
        this.productService = productService;
        this.productDao = productDao;
    }

    @DisplayName("상품을 등록한다.")
    @Test
    void 상품을_등록한다() {
        // given
        ProductCreateRequest 상품_생성_요청 = new ProductCreateRequest(야채곱창_이름, 야채곱창_가격);

        // when
        ProductResponse actual = productService.create(상품_생성_요청);

        // then
        assertAll(
                () -> assertThat(actual.getId()).isNotNull(),
                () -> assertThat(actual.getName()).isEqualTo(야채곱창_이름)
        );
    }

    @DisplayName("상품을 등록할 때, 가격이 0원 보다 작으면 예외가 발생한다.")
    @ParameterizedTest
    @ValueSource(ints = {-1, -2, -100})
    void 상품을_등록할_때_가격이_0원_보다_작으면_예외가_발생한다(int 잘못된_가격) {
        // given
        ProductCreateRequest 상품_생성_요청 = new ProductCreateRequest(야채곱창_이름, BigDecimal.valueOf(잘못된_가격));

        // when & then
        assertThatThrownBy(() -> productService.create(상품_생성_요청))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가격은 0원 이상의 정수로 입력해주세요.");
    }

    @DisplayName("상품을 등록할 때, 가격이 null 이면 예외가 발생한다.")
    @Test
    void 상품을_등록할_때_가격이_null_이면_예외가_발생한다() {
        // given
        ProductCreateRequest 상품_생성_요청 = new ProductCreateRequest(야채곱창_이름, null);

        // when & then
        assertThatThrownBy(() -> productService.create(상품_생성_요청))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("가격은 0원 이상의 정수로 입력해주세요.");
    }

    @DisplayName("상품 목록을 조회한다.")
    @Test
    void 상품_목록을_조회한다() {
        // given
        Product 야채곱창 = new ProductBuilder()
                .name(야채곱창_이름)
                .price(야채곱창_가격)
                .build();
        productDao.save(야채곱창);

        // when
        ProductsResponse 상품들 = productService.list();

        // then
        assertThat(상품들.getProductResponses()).hasSize(1);
    }
}
