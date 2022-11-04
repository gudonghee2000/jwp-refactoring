package kitchenpos.menugroup.presentation;

import java.net.URI;
import java.util.List;
import kitchenpos.menugroup.dto.request.MenuGroupCreateRequest;
import kitchenpos.menugroup.dto.response.MenuGroupResponse;
import kitchenpos.menugroup.dto.response.MenuGroupsResponse;
import kitchenpos.menugroup.service.MenuGroupService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MenuGroupRestController {
    private final MenuGroupService menuGroupService;

    public MenuGroupRestController(final MenuGroupService menuGroupService) {
        this.menuGroupService = menuGroupService;
    }

    @PostMapping("/api/menu-groups")
    public ResponseEntity<MenuGroupResponse> create(@RequestBody final MenuGroupCreateRequest request) {
        MenuGroupResponse response = menuGroupService.create(request);
        URI uri = URI.create("/api/menu-groups/" + response.getId());
        return ResponseEntity.created(uri).body(response);
    }

    @GetMapping("/api/menu-groups")
    public ResponseEntity<List<MenuGroupResponse>> list() {
        MenuGroupsResponse menuGroupsResponse = menuGroupService.list();
        return ResponseEntity.ok().body(menuGroupsResponse.getMenuGroupResponses());
    }
}