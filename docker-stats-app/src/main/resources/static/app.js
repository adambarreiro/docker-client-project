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
                console.log(this.image);
                if (this.image.split(":")[0] !== "" &&
                    this.image.split(":")[1] !== undefined &&
                    this.image.split(":")[1] !== "") {

                    this.running = this.image;
                    this.placeholder = "Loading...";
                    stompClient = Stomp.over(new SockJS('/socket'));
                    stompClient.connect({}, function(frame) {
                        stompClient.subscribe('/docker/stats', function(value) {
                            app.placeholder = "$ docker run -it "+app.running+" top";
                            updateStats(topResults, value.body);
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

// Updates "stats" variable with the input retrieved from the websocket.
let updateStats = function(topResults, input) {
    let sanitizedTopLine = input.replaceAll(/�|\(B||\[m|\[H|\[39;49m|\[\?1h|\[2J|\[\?25l|\[K|\[7m|\[J|\[1m|=/g, " ").trim();
    if (sanitizedTopLine.indexOf('OCI runtime exec failed') > -1) {
        app.placeholder = "Looks like " + app.running + " doesn't have top command!";
        app.stats = "";
        app.running = "";
    } else if (sanitizedTopLine.indexOf("Exception") > -1) {
        app.placeholder = sanitizedTopLine;
        app.stats = "";
        app.running = "";
    } else {
        let firstWord = sanitizedTopLine.split(' ')[0];
        let secondWord = sanitizedTopLine.split(' ')[1];
        let key = firstWord.charAt(0) + (secondWord !== undefined ? secondWord.charAt(0) : "");
        topResults[key] = sanitizedTopLine;
        topStructuredStatsUpdate(topResults);
    }
}

// Structures the top command to print it correctly on screen.
let topStructuredStatsUpdate = function (topResults) {
    let unrecognizedLine = "<i>Unrecognized output</i>";
    let topLine = topResults["t-"] || unrecognizedLine;
    let tasksLine = topResults['T'] || topResults['La'] || unrecognizedLine;
    let cpuLine = topResults['%'] || topResults['C'] || unrecognizedLine;
    let memoryLine = topResults['MM'] || topResults['KM'] || unrecognizedLine;
    let swapLine = topResults['MS'] || topResults['KS'] || unrecognizedLine;
    let processHeader = topResults['PU'] || topResults['P'] || unrecognizedLine;
    app.stats = topLine + "<br/>" +
        tasksLine + "<br/>" +
        cpuLine + "<br/>"+
        memoryLine + "<br/>"+
        swapLine + "<br/><br/>"+
        processHeader+"<br/>";
    for (key in topResults) {
        if (/^\d/.test(key)) {
            app.stats += topResults[key]+ "<br/>";
        }
    }
}
