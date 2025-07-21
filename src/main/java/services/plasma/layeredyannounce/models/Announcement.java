package services.plasma.layeredyannounce.models;

import java.util.Objects;

public class Announcement {

    private boolean hasAnnouncement;
    private String id;
    private String content;
    private String theme;
    private String customColor;
    private String customTextColor;
    private double opacity;
    private String position;
    private boolean showOnce;
    private boolean allowDismissal;
    private String linkUrl;
    private String linkText;
    private String userPlan;

    public Announcement() {
        // Default constructor for JSON deserialization
    }

    public Announcement(String id, String content) {
        this.hasAnnouncement = true;
        this.id = id;
        this.content = content;
        this.theme = "default";
        this.customColor = "#3b82f6";
        this.customTextColor = "#ffffff";
        this.opacity = 1.0;
        this.position = "top";
        this.showOnce = false;
        this.allowDismissal = true;
        this.linkUrl = "";
        this.linkText = "";
        this.userPlan = "plus";
    }

    public boolean isHasAnnouncement() {
        return hasAnnouncement;
    }

    public void setHasAnnouncement(boolean hasAnnouncement) {
        this.hasAnnouncement = hasAnnouncement;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getCustomColor() {
        return customColor;
    }

    public void setCustomColor(String customColor) {
        this.customColor = customColor;
    }

    public String getCustomTextColor() {
        return customTextColor;
    }

    public void setCustomTextColor(String customTextColor) {
        this.customTextColor = customTextColor;
    }

    public double getOpacity() {
        return opacity;
    }

    public void setOpacity(double opacity) {
        this.opacity = opacity;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isShowOnce() {
        return showOnce;
    }

    public void setShowOnce(boolean showOnce) {
        this.showOnce = showOnce;
    }

    public boolean isAllowDismissal() {
        return allowDismissal;
    }

    public void setAllowDismissal(boolean allowDismissal) {
        this.allowDismissal = allowDismissal;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getLinkText() {
        return linkText;
    }

    public void setLinkText(String linkText) {
        this.linkText = linkText;
    }

    public String getUserPlan() {
        return userPlan;
    }

    public void setUserPlan(String userPlan) {
        this.userPlan = userPlan;
    }

    // Legacy compatibility methods
    public String getMessage() {
        return content;
    }

    public void setMessage(String message) {
        this.content = message;
    }

    public boolean isActive() {
        return hasAnnouncement;
    }

    public void setActive(boolean active) {
        this.hasAnnouncement = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Announcement that = (Announcement) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Announcement{" +
                "hasAnnouncement=" + hasAnnouncement +
                ", id='" + id + '\'' +
                ", content='" + content + '\'' +
                ", theme='" + theme + '\'' +
                ", customColor='" + customColor + '\'' +
                ", showOnce=" + showOnce +
                '}';
    }

    public boolean isValid() {
        return hasAnnouncement &&
                id != null && !id.trim().isEmpty() &&
                content != null && !content.trim().isEmpty();
    }
}