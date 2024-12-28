    package com.example.courseonline.Domain;

    import java.util.Date;
    import java.util.List;
    import java.util.Objects;

    public class TopicClass {
        private String topic_id;
        private String topic_sender_id;
        private String topic_subject;
        private String topic_content;
        private List<String> topic_like;
        private List<String> topic_viewer;
        private List<String> topic_attach;
        private List<String> topic_seen;
        private Date topic_upload;
        private String discussion_id;


        public TopicClass(String topic_id, String topic_sender_id, String topic_subject, String topic_content, List<String> topic_like, List<String> topic_viewer, List<String> topic_attach, List<String> topic_seen, Date topic_upload, String discussion_id) {
            this.topic_id = topic_id;
            this.topic_sender_id = topic_sender_id;
            this.topic_subject = topic_subject;
            this.topic_content = topic_content;
            this.topic_like = topic_like;
            this.topic_viewer = topic_viewer;
            this.topic_attach = topic_attach;
            this.topic_seen = topic_seen;
            this.topic_upload = topic_upload;
            this.discussion_id = discussion_id;
        }

        public TopicClass() {
        }

        public String getTopic_id() {
            return topic_id;
        }

        public void setTopic_id(String topic_id) {
            this.topic_id = topic_id;
        }
        public String getTopic_sender_id() {
            return topic_sender_id;
        }

        public void setTopic_sender_id(String topic_sender_id) {
            this.topic_sender_id = topic_sender_id;
        }

        public String getTopic_subject() {
            return topic_subject;
        }

        public void setTopic_subject(String topic_subject) {
            this.topic_subject = topic_subject;
        }

        public String getTopic_content() {
            return topic_content;
        }

        public void setTopic_content(String topic_content) {
            this.topic_content = topic_content;
        }

        public List<String> getTopic_attach() {
            return topic_attach;
        }

        public void setTopic_attach(List<String> topic_attach) {
            this.topic_attach = topic_attach;
        }

        public Date getTopic_upload() {
            return topic_upload;
        }

        public List<String> getTopic_like() {
            return topic_like;
        }

        public void setTopic_like(List<String> topic_like) {
            this.topic_like = topic_like;
        }

        public List<String> getTopic_viewer() {
            return topic_viewer;
        }

        public void setTopic_viewer(List<String> topic_viewer) {
            this.topic_viewer = topic_viewer;
        }

        public String getDiscussion_id() {
            return discussion_id;
        }

        public void setDiscussion_id(String discussion_id) {
            this.discussion_id = discussion_id;
        }

        public List<String> getTopic_seen() {
            return topic_seen;
        }

        public void setTopic_seen(List<String> topic_seen) {
            this.topic_seen = topic_seen;
        }

        public void setTopic_upload(Date topic_upload) {
            this.topic_upload = topic_upload;
        }
        @Override
        public boolean equals(Object obj) {
            if (this == obj) return true;
            if (obj == null || getClass() != obj.getClass()) return false;
            TopicClass that = (TopicClass) obj;
            return Objects.equals(topic_id, that.topic_id);
        }

        @Override
        public int hashCode() {
            return topic_id  != null ? topic_id .hashCode() : 0;
        }
    }

