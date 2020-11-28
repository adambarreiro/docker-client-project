let stompClient = null;

var app = new Vue({
    el: "#app",
    data: {
        stats: "",
        placeholder: "$"
    },
    mounted: function(){
        let that = this;
        let topResults = {};
        stompClient = Stomp.over(new SockJS('/socket'));
        stompClient.connect({}, function(frame) {
            stompClient.subscribe('/docker/stats', function(value) {
                transform(topResults, value.body);
            });
            that.start();
        });
    },
    methods: {
        start: function() {
            stompClient.send("/start");
            app.placeholder = "$ docker run -it ubuntu:latest top";
        },
        stop: function() {
            stompClient.send("/stop");
            app.placeholder = "$";
            app.stats = {};
        }
    }
});

// Prints Top command on screen
let transform = function(topResults, input) {
    let sanitized = input.replaceAll(/�|\(B||\[m|\[H|\[39;49m|\[\?1h|\[2J|\[\?25l|\[K|\[7m|\[J|\[1m|=/g, " ").trim();
    let firstWord = sanitized.split(' ')[0];
    let secondWord = sanitized.split(' ')[1];
    let key = firstWord.charAt(0) + (secondWord !== undefined ? secondWord.charAt(0) : "");
    topResults[key] = sanitized;
    app.stats = topResults["t-"] + "<br/>" + topResults['T'] + "<br/>" + topResults['%']+ "<br/>"+topResults['MM']+ "<br/>"+topResults['MS']+ "<br/><br/>"+topResults['PU']+"<br/>";
    for (key in topResults) {
         if (/^\d/.test(key)) {
             app.stats += topResults[key]+ "<br/>";
         }
    }
}
