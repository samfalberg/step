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
        ['Can I offer you a nice egg in this tryin\' time?', 
        'Duct tape, zipties, and gloves; I have to have my tools!', 
        'I\'ve got boxes full of Pepe!',
        'I will come down on this hospital like the hammer of Thor. The thunder of my vengeance will echo through these corridors with the gust of a thousand winds!', 
        'Dee, I will slap you in the teeth.'];

    const quoteNum = Math.floor(Math.random() * quotes.length)

    // Pick a random quote and iamge.
    const quote = quotes[quoteNum];
    const imgUrl = 'images/sunny/IASIP-' + quoteNum + '.jpg';

    const imgElement = document.createElement('img');
    imgElement.src = imgUrl;

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
    var moodSelected = moods.options[moods.selectedIndex].value;

    if (moodSelected == "distressed") {
        var imgUrl = 'images/cats/distressed-0.jpg';
    }
    else if (moodSelected == "elated") {
        
    }
    else if (moodSelected == "distressed") {
        
    }
    else if (moodSelected == "melancholy") {
        
    }
    else if (moodSelected == "indolent") {
        
    }

    var imgElement = document.createElement('img');
    imgElement.src = imgUrl;

    //Add image to page
    var imgContainer = document.getElementById('cat-mood');
    imgContainer.appendChild(imgElement);
}
