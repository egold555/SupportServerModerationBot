const cardTemplate = `<div class="cardRee">
<div class="card">
  <img class="card-img-top"
    src="%image-url%">
  <div class="card-body">
    <h5 class="card-title">%creator%</h5>
    <p class="card-text">Likes: %likes% <tab align=right>Dislikes: %dislikes%</p>
    <p class="card-text"></p>
    <p class="card-text">Total Votes: %total%</p>
    <a href="%jump-url%" target="_blank">Click to visit on Discord</p>
  </div>
</div>
</div>`;

const IP = "https://ess.golde.org/api/";

$.getJSON(IP + 'holiday', function(data) {
	if (data.success) {
		console.log('I got me data!');
		var messages = data.data.msgs;
		for (msgInt in messages) {
			var msg = messages[msgInt];
			var sender = msg.sender;
			var votes = msg.votes;
			var image = msg.image;

			var htmlString = cardTemplate;
			htmlString = htmlString.replace('%image-url%', image.url);
			htmlString = htmlString.replace('%creator%', sender.displayName);
			htmlString = htmlString.replace('%likes%', votes.likes);
			htmlString = htmlString.replace('%dislikes%', votes.dislikes);
			htmlString = htmlString.replace('%total%', votes.total);
			htmlString = htmlString.replace('%jump-url%', msg.url);
			$('.cardArea').append(htmlString);
		}
	}
});
