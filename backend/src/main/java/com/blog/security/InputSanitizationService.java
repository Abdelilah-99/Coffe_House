package com.blog.security;

import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;

@Service
public class InputSanitizationService {
    private static final int MAX_TITLE_LENGTH = 200;
    private static final int MAX_CONTENT_LENGTH = 10000;
    private static final int MAX_COMMENT_LENGTH = 1000;
    private static final int MAX_USERNAME_LENGTH = 50;

    public String sanitizeTitle(String title) {
        if (title == null) {
            return "";
        }
        title = title.trim();
        if (title.length() > MAX_TITLE_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Title exceeds maximum length of %d characters", MAX_TITLE_LENGTH)
            );
        }
        // title = HtmlUtils.htmlEscape(title);
        return title;
    }

    public String sanitizeContent(String content) {
        if (content == null) {
            return "";
        }
        content = content.trim();
        if (content.length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Content exceeds maximum length of %d characters", MAX_CONTENT_LENGTH)
            );
        }
        // content = HtmlUtils.htmlEscape(content);
        return content;
    }

    public String sanitizeComment(String comment) {
        if (comment == null) {
            return "";
        }
        comment = comment.trim();
        if (comment.length() > MAX_COMMENT_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Comment exceeds maximum length of %d characters", MAX_COMMENT_LENGTH)
            );
        }
        // comment = HtmlUtils.htmlEscape(comment);

        return comment;
    }

    public String sanitizeUsername(String username) {
        if (username == null) {
            return "";
        }
        username = username.trim();
        if (username.length() > MAX_USERNAME_LENGTH) {
            throw new IllegalArgumentException(
                    String.format("Username exceeds maximum length of %d characters", MAX_USERNAME_LENGTH)
            );
        }
        if (!username.matches("^[a-zA-Z0-9._-]+$")) {
            throw new IllegalArgumentException(
                    "Username can only contain letters, numbers, dots, underscores, and hyphens"
            );
        }
        return username;
    }

    public String sanitizeText(String text) {
        return sanitizeText(text, MAX_CONTENT_LENGTH);
    }

    public String sanitizeText(String text, int maxLength) {
        if (text == null) {
            return "";
        }

        text = text.trim();

        if (text.length() > maxLength) {
            throw new IllegalArgumentException(
                    String.format("Text exceeds maximum length of %d characters", maxLength)
            );
        }

        // text = HtmlUtils.htmlEscape(text);

        return text;
    }

}
