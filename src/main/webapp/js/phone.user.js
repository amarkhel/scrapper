// ==UserScript==
// @name         New Userscript
// @namespace    http://tampermonkey.net/
// @version      0.1
// @description  try to take over the world!
// @author       You
// @match        https://www.mafiaonline.ru/games/chat/chat.php
// @grant        none
// ==/UserScript==

$(function() {
   setInterval(function() {
  var privates = $("span.private");
    privates.each(function( index ) {
     var val = $(this).text();
     if (val.startsWith("phone")) {
         $(this).css("color", "#ff6600");
         $(this).children().each(function () {
    $(this).css("color", "#ff6600");
});
     }
});
}, 500);
});