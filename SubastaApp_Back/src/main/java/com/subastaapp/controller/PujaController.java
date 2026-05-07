package com.subastaapp.controller;

import com.subastaapp.businesslogic.PujaService;
import com.subastaapp.dto.request.PujaRequest;
import com.subastaapp.dto.response.PujaResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class PujaController {

    private final PujaService pujaService;

    @PostMapping("/{id}/pujas")
    public ResponseEntity<PujaResponse> pujar(@PathVariable Long id,
                                               @Valid @RequestBody PujaRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pujaService.pujar(id, req));
    }

    @GetMapping("/{id}/pujas")
    public ResponseEntity<List<PujaResponse>> historial(@PathVariable Long id) {
        return ResponseEntity.ok(pujaService.historial(id));
    }
}
