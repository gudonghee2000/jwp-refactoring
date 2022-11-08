package kitchenpos.table.presentation;

import java.net.URI;
import java.util.List;
import kitchenpos.table.dto.request.OrderTableCreateRequest;
import kitchenpos.table.dto.request.OrderTableUpdateEmptyRequest;
import kitchenpos.table.dto.request.OrderTableUpdateGuestRequest;
import kitchenpos.table.dto.response.OrderTableResponse;
import kitchenpos.table.dto.response.OrderTablesResponse;
import kitchenpos.table.service.TableService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TableRestController {

    private final TableService tableService;

    public TableRestController(final TableService tableService) {
        this.tableService = tableService;
    }

    @PostMapping("/api/tables")
    public ResponseEntity<OrderTableResponse> create(@RequestBody final OrderTableCreateRequest request) {
        final OrderTableResponse response = tableService.create(request);
        final URI uri = URI.create("/api/tables/" + response.getId());
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/api/tables")
    public ResponseEntity<List<OrderTableResponse>> list() {
        OrderTablesResponse response = tableService.list();
        return ResponseEntity.ok().body(response.getOrderTableResponses());
    }

    @PutMapping("/api/tables/{orderTableId}/empty")
    public ResponseEntity<OrderTableResponse> changeEmpty(
            @PathVariable final Long orderTableId,
            @RequestBody final OrderTableUpdateEmptyRequest request) {
        return ResponseEntity.ok().body(tableService.changeEmpty(orderTableId, request));
    }

    @PutMapping("/api/tables/{orderTableId}/number-of-guests")
    public ResponseEntity<OrderTableResponse> changeNumberOfGuests(
            @PathVariable final Long orderTableId,
            @RequestBody final OrderTableUpdateGuestRequest request) {
        return ResponseEntity.ok().body(tableService.changeNumberOfGuests(orderTableId, request));
    }
}