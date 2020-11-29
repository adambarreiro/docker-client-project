let stompClient = null;

var app = new Vue({
    el: "#app",
    data: {
        stats: "",
        image: "ubuntu:latest",
        running: "",
        placeholder: "Loading..."
    },
    mounted: function(){
        this.start();
    },
    methods: {
        start: function() {
            let topResults = {};
            if (this.running === "") {
                if (this.image.split(":")[0] !== "" && this.image.split(":")[1] !== "") {
                    this.running = this.image;
                    this.placeholder = "Loading...";
                    stompClient = Stomp.over(new SockJS('/socket'));
                    stompClient.connect({}, function(frame) {
                        stompClient.subscribe('/docker/stats', function(value) {
                            app.placeholder = "$ docker run -it "+app.running+" top";
                            transform(topResults, value.body);
                        });
                        stompClient.send("/start", {}, app.running);
                    });
                } else {
                    alert("Please introduce a valid image: 'image:tag'");
                }
            } else {
                alert("Please stop current " + this.running + " container first");
            }
        },
        stop: function() {
            if (stompClient !== null) {
                stompClient.send("/stop");
                stompClient.disconnect();
                stompClient = null;
                app.placeholder = "$";
                app.stats = "";
                app.running = "";
            }
        }
    }
});

// Prints Top command on screen
let transform = function(topResults, input) {
    let sanitized = input.replaceAll(/�|\(B||\[m|\[H|\[39;49m|\[\?1h|\[2J|\[\?25l|\[K|\[7m|\[J|\[1m|=/g, " ").trim();
    if (sanitized.indexOf('OCI runtime exec failed') > -1) {
        app.placeholder = "Looks like " + app.running + " doesn't have top command!";
        app.stats = "";
        app.running = "";
    } else if (sanitized.indexOf("Exception") > -1) {
        app.placeholder = sanitized;
        app.stats = "";
        app.running = "";
    } else {
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
}
