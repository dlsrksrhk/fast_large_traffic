package com.onion.backend.service;

import com.onion.backend.dto.WriteCommentDto;
import com.onion.backend.entity.Article;
import com.onion.backend.entity.Board;
import com.onion.backend.entity.Comment;
import com.onion.backend.entity.User;
import com.onion.backend.repository.ArticleRepository;
import com.onion.backend.repository.BoardRepository;
import com.onion.backend.repository.UserRepository;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(username = "test", roles = {"USER"})
@Transactional
class CommentServiceTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BoardRepository boardRepository;

    @Autowired
    private ArticleRepository articleRepository;

    Long saveArticle() {
        Long boardId = 1L;
        Long userId = 2L;

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Board board = boardRepository.findById(boardId)
                .orElseThrow(() -> new RuntimeException("Board not found"));

        Article article = new Article();
        article.setBoard(board);
        article.setAuthor(author);
        article.setTitle("test title");
        article.setContent("test content");
        return articleRepository.save(article).getId();
    }


    @Test
    void writeComment() {
        Long boardId = 1L;
        Long articleId = saveArticle();
        String content = "test comment articleId : " + articleId;

        WriteCommentDto writeCommentDto = new WriteCommentDto();
        writeCommentDto.setContent(content);

        Comment comment = commentService.writeComment(boardId, articleId, writeCommentDto);
        Article foundArticle = articleRepository.findById(articleId)
                .orElseThrow(() -> new RuntimeException("Article not found"));

        assertNotNull(comment);
        assertEquals(content, comment.getContent());
        assertEquals(comment.getId(), foundArticle.getComments().getFirst().getId());
    }

    @Test
    void editComment() {
        Long boardId = 1L;
        Long articleId = saveArticle();
        String writeContent = "test comment articleId : " + articleId;
        String editContent = "test comment articleId : " + articleId + " edited";

        WriteCommentDto writeCommentDto = new WriteCommentDto();
        writeCommentDto.setContent(writeContent);
        WriteCommentDto editCommentDto = new WriteCommentDto();
        editCommentDto.setContent(editContent);

        Comment comment = commentService.writeComment(boardId, articleId, writeCommentDto);
        Comment editComment = commentService.editComment(boardId, articleId, comment.getId(), editCommentDto);

        assertEquals(comment.getId(), editComment.getId());
        assertEquals(editContent, editComment.getContent());
    }
}