package com.pulsedesk.controller;

import com.pulsedesk.dto.CommentRequest;
import com.pulsedesk.model.Comment;
import com.pulsedesk.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono; // THIS WAS MISSING

import java.util.List;

@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public Mono<ResponseEntity<Comment>> submitComment(@RequestBody CommentRequest request) {
        return commentService.submitComment(request.getText(), request.getSource())
                .map(ResponseEntity::ok);
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComments());
    }
}