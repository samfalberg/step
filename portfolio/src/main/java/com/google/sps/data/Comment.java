// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps.servlets;

/* Comment class for personal portfolio */
public class Comment {
    
    private long id;
    private long timestamp;
    private String name;
    private String email;
    private String message;
    private String mood;
    private String imageUrl;
    private boolean myComment;

    public Comment(long id, long timestamp, String name, String email, String message, 
    String mood, String imageUrl, boolean myComment) {
        this.id = id;
        this.timestamp = timestamp;
        this.name = name;
        this.email = email;
        this.message = message;
        this.mood = mood;
        this.imageUrl = imageUrl;
        this.myComment = myComment;
    }

    /* Builder class for the comment*/
    public static class Builder {

        private long id;
        private long timestamp;
        private String name;
        private String email;
        private String message;
        private String mood;
        private String imageUrl;
        private boolean myComment;

        public Builder(){}

        public Builder setId(long id) {
            this.id = id;
            return this;
        }

        public Builder setTimestamp(long timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setEmail(String email) {
            this.email = email;
            return this;
        }

        public Builder setMessage(String message) {
            this.message = message;
            return this;
        }

        public Builder setMood(String mood) {
            this.mood = mood;
            return this;
        }

        public Builder setImageUrl(String imageUrl) {
            this.imageUrl = imageUrl;
            return this;
        }

        public Builder setMyComment(boolean myComment) {
            this.myComment = myComment;
            return this;
        }

        public Comment build() {
            return new Comment(id, timestamp, name, email, message, mood, imageUrl, myComment);
        }
    }
}
