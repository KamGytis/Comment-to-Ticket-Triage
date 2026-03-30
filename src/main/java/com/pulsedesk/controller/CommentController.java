package com.pulsedesk.controller;

import com.pulsedesk.dto.CommentRequest;
import com.pulsedesk.model.Comment;
import com.pulsedesk.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:5174")
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<Comment> submitComment(@RequestBody CommentRequest request) {
        Comment comment = commentService.submitComment(request.getText(), request.getSource());
        return ResponseEntity.ok(comment);
    }

    @GetMapping
    public ResponseEntity<List<Comment>> getAllComments() {
        return ResponseEntity.ok(commentService.getAllComments());
    }
    
}