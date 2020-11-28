let stompClient = null;
let app = new Vue({
    el: "#app",
    data: {
        stats: [],
        placeholder: "$"
    },
    mounted: function(){
        let that = this;
        stompClient = Stomp.over(new SockJS('/socket'));
        stompClient.connect({}, function(frame) {
            stompClient.subscribe('/docker/stats', function(value) {
                app.stats.push(value.body);
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
            app.stats = [];
        }
    }
});