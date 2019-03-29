package com.paradox.eigonews;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

class NewsCard {
    private String title;
    private String description;
    private String imgUrl;
    private String sourceId;
    private String sourceName;
    private String author;
    private String url;
    private String content;
    private Date publishedAt;

    NewsCard(JSONObject article) throws JSONException {
        this.sourceName = article.getJSONObject("source").getString("name");
        this.sourceId = article.getJSONObject("source").getString("id");
        this.author = article.getString("author");
        this.title = article.getString("title");
        // TODO: Validate the news encoding and convert to proper if needed
        this.description = article.getString("description");
        this.url = article.getString("url");
        this.imgUrl = article.getString("urlToImage");
        // this.publishedAt= new Date(article.getString("publishedAt")); TODO: uncomment
        this.content = article.getString("content");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getSourceName() {
        return sourceName;
    }

    public void setSourceName(String sourceName) {
        this.sourceName = sourceName;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getPublishedAt() {
        return publishedAt;
    }

    public void setPublishedAt(Date publishedAt) {
        this.publishedAt = publishedAt;
    }
}
