package com.orderme.backend.menu;

import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class MenuItemService {
    private final MenuItemRepository repo;

    public MenuItemService(MenuItemRepository repo) {
        this.repo = repo;
    }

    public List<MenuItem> getAllMenuItems() {
        return repo.findAll();
    }

    public MenuItem saveMenuItem(MenuItem item) {
        return repo.save(item);
    }

    // Add update/delete as needed
}
