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
    private String message;
    private String mood;
    private String imageUrl;

    public Comment(long id, long timestamp, String name, String message, String mood, String imageUrl) {
        this.id = id;
        this.timestamp = timestamp;
        this.name = name;
        this.message = message;
        this.mood = mood;
        this.imageUrl = imageUrl;
    }
}
