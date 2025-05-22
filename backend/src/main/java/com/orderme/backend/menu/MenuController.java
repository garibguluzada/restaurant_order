package com.orderme.backend.menu;

import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:5500", allowCredentials = "true", allowedHeaders = "*", methods = {
    RequestMethod.GET, RequestMethod.POST, RequestMethod.OPTIONS })
@RestController
@RequestMapping("/menu")
public class MenuController {
    private final MenuItemService service;

    public MenuController(MenuItemService service) {
        this.service = service;
    }

    @GetMapping
    public List<MenuItem> getMenu() {
        return service.getAllMenuItems();
    }

    @PostMapping
    public MenuItem addMenuItem(@RequestBody MenuItem item) {
        return service.saveMenuItem(item);
    }

    // Add PUT/DELETE here
}
