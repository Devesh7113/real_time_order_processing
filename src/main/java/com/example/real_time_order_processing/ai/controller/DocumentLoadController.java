package com.example.real_time_order_processing.ai.controller;

import com.example.real_time_order_processing.ai.rag.DocumentLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/ai")
@RequiredArgsConstructor
public class DocumentLoadController
{
    private final DocumentLoader documentLoader;

    @PostMapping("/load-documents")
    public ResponseEntity<String> loadDocuments()
    {
        documentLoader.LoadDocument();
        return ResponseEntity.ok("Knowledge base loaded into vector store.");
    }
}
