package com.subastaapp.controller;

import com.subastaapp.seeder.DatabaseSeeder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/seed")
@RequiredArgsConstructor
public class AdminSeederController {

    private final DatabaseSeeder databaseSeeder;

    @PostMapping
    public ResponseEntity<String> seed() {
        databaseSeeder.seed();
        return ResponseEntity.ok("Seeder ejecutado.");
    }
}
