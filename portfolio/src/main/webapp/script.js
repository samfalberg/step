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

/**
 * Adds a random greeting to the page.
 */
function addRandomQuote() {
    const quotes =
        ['\"Can I offer you a nice egg in this tryin\' time?\"', 
        '\"Duct tape, zipties, and gloves; I have to have my tools!\"', 
        '\"I\'ve got boxes full of Pepe!\"',
        '\"I will come down on this hospital like the hammer of Thor. The thunder of my vengeance will echo through these corridors with the gust of a thousand winds!\"', 
        '\"Dee, I will slap you in the teeth.\"'];
    
    var quotesNoRepeat = quotes;

    const quoteNum = Math.floor(Math.random() * quotesNoRepeat.length)

    // Pick a random quote and iamge.
    const quote = quotesNoRepeat[quoteNum];
    quotesNoRepeat.splice(quoteNum, 1);
    const imgUrl = 'images/sunny/IASIP-' + quoteNum + '.jpg';

    const imgElement = document.createElement('img');
    imgElement.src = imgUrl;
    imgElement.height = 504;
    imgElement.width = 884;

    // Add them to the page.
    const quoteContainer = document.getElementById('quote-container');
    quoteContainer.innerText = quote + "\n";
    quoteContainer.appendChild(imgElement);
}

/*
 * Displays an image relevant to the chosen mood
 */
function catMood() {
    //Get value of select dropdown
    var moods = document.getElementById("moods");
    var moodSelected = moods.options[moods.selectedIndex].value
    
    //Choose one of two images matching certain mood
    var imgNum = Math.floor(Math.random() * 2);
    var imgUrl = 'images/cats/' + moodSelected + '-' + imgNum + '.jpg';
    var img = document.createElement('img');
    img.src = imgUrl;
    img.height = 500;
    img.width = 350;

    //Add image to page, remove previous one
    const imgContainer = document.getElementById('cat-moods-container');
    imgContainer.innerHTML = '\n';
    imgContainer.appendChild(img);
}

/*
 * Blows a "hole" in the screen
 */
function blowUp() {
    //Play explosion on click
    var explosion = document.createElement('audio');
    explosion.src = 'sounds/explosion.mp3';
    explosion.play();

    var hole = document.createElement('img');
    hole.src = 'images/hole/hole.png';
    hole.width = 419;
    hole.height = 319;
    
    //Add hole in wall, remove crack
    const imgContainer = document.getElementById('site-crack');
    imgContainer.innerHTML = '';
    imgContainer.appendChild(hole);

    //Add text
    const textContainer = document.getElementById('text-container');
    textContainer.innerText = "\nWhat the heck? You just blew up my website! Not cool...";    
}

/**
 * Fetches comment from the server
 */
function showComments() {
    fetch('/data').then(response => response.json()).then((comments) => {
        comments.forEach((comment) => {
            document.getElementById('comment-container').appendChild(createComment(comment));
        })
        console.log(comments);
  });
}

/**
 * Displays comment message, timestamp, and id
 */
function createComment(comment) {
     //Box for each comment
     const commentBox = document.createElement('div');
     
     //Add id
     const commentID = document.createElement('b');
     commentID.innerHTML = comment.id;
     
     //Add timestamp
     const commentTimestamp = document.createElement('small');
     commentTimestamp.innerHTML = comment.timestamp

     //Add message
     const commentMessage = document.createElement('p');
     commentMessage.innerHTML = comment.message;

     const deleteButton = document.createElement('button');
     deleteButton.innerText = 'Delete';
     deleteButton.addEventListener('click', () => {
         deleteComment(comment);

         commentBox.remove();
     })

     commentBox.appendChild(commentID);
     commentBox.appendChild(commentTimestamp);
     commentBox.appendChild(commentMessage);
     commentBox.appendChild(deleteButton);

     return commentBox;
 }

 /**
  * Delete comments from the server
  */
function deleteComment(comment) {
    const params = new URLSearchParams();
    params.append('id', comment.id);
    fetch('/delete-data', {method: 'POST', body: params});
}
