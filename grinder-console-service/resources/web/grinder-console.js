jQuery(function($) {

    function addChangeDetection(scope) {
        var changeables = $(".changeable", scope);

        if (!changeables.length) {
            return;
        }

        $("label", scope).each(function() {
            var l = this;

            if (l.htmlFor != '') {
                var e = $("#" + l.htmlFor, scope)[0];

                if (e) {
                    e.label = this;
                } else {
                    $("[name='" + l.htmlFor + "']", scope).each(function() {
                        this.label = l;
                    });
                }
            }
        });

        jQuery.fn.visible = function(show) {
            return this.css("visibility", show ? "visible" : "hidden");
        };

        var submit = $("#submit", scope);
        submit.visible(false);

        changeables.each(function() {

            if (this.type === "checkbox") {
                this.modified = function() {
                    return this.checked != this.defaultChecked;
                };
            } else {
                this.original = this.value;
                this.modified = function() {
                    return this.original != this.value;
                };
            }

            $(this).change(function(e) {
                // This is wrong if multiple controls share the same label.
                if (e.target.modified()) {
                    $(e.target.label).addClass("changed");
                } else {
                    $(e.target.label).removeClass("changed");
                }

                submit.visible(changeables.filter(function(x) {
                    return this.modified();
                }).length);
            });
        });
    }

    function pollLiveData(scope) {

        $(".live-data", scope).each(function() {
            //console.log("Registering ", this);
            var key = $(this).data("ld-key");
            var seq = $(this).data("ld-seq");

            var xhr = null;

            function poll(e) {
                //console.log("Polling ", e);
                xhr = $.get("/ui/poll", {k : key, s: seq}, function(x) {
                    //console.log("Update ", x);

                    var ee = $(e);

                    ee.trigger("livedata", [key, x]);

                    if (ee.hasClass("live-data-animation")) {
                        ee
                        .stop()
                        .animate({opacity: 0.5},
                                "fast",
                                function() {
                            $(this)
                            .html(x.data)
                            .animate({opacity: 1}, "fast");
                        });
                    }
                    else if (ee.hasClass("live-data-display")) {
                        ee.html(x.data);
                    }

                    seq = x.next;

                    // Dispatch in timer - directly calling poll()
                    // causes FF to spin sometimes.
                    setTimeout(function() {poll(e);}, 1);
                },
                "json");
            }

            var thisElement = this;

            $(document).bind("DOMNodeRemoved", function(e) {
                if (e.target == thisElement) {
                    if (xhr != null)  {
                        xhr.abort();
                    }
                }
            });

            poll(this);
        });
    }

    function addButtons(scope) {
        content = $("div#content");

        $(".grinder-button", scope).each(function() {
            if (this.id) {

                var buttonOptions;

                if (this.classList.contains("grinder-button-icon")) {
                    buttonOptions = {
                            icons: { primary: this.id }
                    };
                }
                else {
                    buttonOptions = {};
                }

                $(this).button(buttonOptions);

                if (this.classList.contains("replace-content")) {
                    $(this).click(function() {
                        $.get("/ui/content/" + this.id,
                           function(x) {
                                content.animate(
                                    {opacity: 0},
                                    "fast",
                                    function() {
                                        content.html(x);
                                        addDynamicBehaviour(content);
                                        content.animate({opacity: 1}, "fast");
                                    });
                           });
                        });
                }
                else {
                    $(this).click(function() {
                        $.post("/ui/action/" + this.id);
                    });
                }
            } else {
                $(this).button();
            };
        });
    }

    function cubismDemo() {

        if (!$("#cubism").length) {
            return;
        }

        var w = $("#cubism").width();

        var context = cubism.context()
                        .step(2000)
                        .size(w);

        // Maybe there's a neater way to do this with d3?
        $("#cubism").each(function() {
            var thisElement = this;
            $(document).bind("DOMNodeRemoved", function(e) {
                if (e.target == thisElement) {
                    context.stop();
                }
            });
        });

        d3.select("#cubism").selectAll(".axis")
            .data(["top", "bottom"])
            .enter().append("div")
            .attr("class", function(d) { return d + " axis"; })
            .each(function(d) {
                d3.select(this).call(context.axis().ticks(12).orient(d)); });

        d3.select("#cubism").append("div")
            .attr("class", "rule")
            .call(context.rule());


        $("#test").on('livedata', function(_e, k, x) {
            if (k === "sample") {
                // Bind tests to nodes.
                var binding = d3.select("#cubism").selectAll(".horizon")
                .data(function() {
                        var existing = {};

                        $(this).each(function() {
                            existing[this.__data__.test.test] = this.__data__;
                            });

                        return $.map(
                                x.data.tests,
                                function(t) {
                                    return cubismMetric(existing[t.test],
                                                        x.data.timestamp,
                                                        t);
                                });
                      },
                      // Use the test number as the key for the d3 join.
                      function(metric) { return metric.test.test; });

                // Handle new nodes.
                // How do we alter the sort order?
                binding.enter().insert("div", ".bottom")
                    .attr("class", "horizon")
                    .call(context.horizon()
                            .format(d3.format(",.4g"))
                            .colors(["#225EA8",
                                     "#41B6C4",
                                     "#A1DAB4",
                                     "#FFFFCC",
                                     "#FECC5C",
                                     "#FD8D3C",
                                     "#F03B20",
                                     "#BD0026"]));

                binding.exit().remove();

                context.on("focus", function(i) {
                    d3.selectAll(".value")
                    .style("right",
                            i == null ? null : context.size() - i + "px");
                });

                //console.log(newTests, tests);
            }
        });

        function cubismMetric(existing, timestamp, test) {
            var metric;

            if (existing) {
                metric = existing;
            }
            else {
                // We may want to replace this with a binary tree.
                // For now we just have an array in timestamp order.
                // Each element is an array pair of timestamp and statistic.
                // We assume that we're called with increasing timestamps.
                var data = [];

                metric = context.metric(function(start, stop, step, callback) {
                        var values = [];

                        start = +start; // Date -> timestamp.
                        var x, stats;

                        var previousBetween = function() {
                            var d = data.length - 1;

                            return function(s, e) {
                                x = data[d];

                                while (x && x[0] >= s) {
                                    d -= 1;
                                    if (x[0] < e) {
                                        return x[1];
                                    }

                                    x = data[d];
                                }
                            };
                        }();

                        for (var i = +stop; i > start; i-= step) {
                            stats = [];

                            while (x = previousBetween(i - step, i)) {
                                stats.push(x);
                            }

                            values.unshift(
                                    averageStatistic(stats, 0 /* tests */));
                        }

                        console.log("->", values, +start, +stop, context);

                        callback(null, values);
                    },
                    test.test + " [" + test.description + "]");

                metric.test = test;
                metric.data = data;

                function averageStatistic(stats, slot) {
                    if (stats.length == 0) {
                        return NaN;
                    }

                    var total =
                        stats.reduce(function(x, y) { return x + y[slot]; }, 0);
                    return total / stats.length;
                }
            }

            // Trim old data?
            metric.data.push([timestamp, test.statistics]);

            return metric;
        }
    }

    function addDynamicBehaviour(scope) {
        addButtons(scope);
        addChangeDetection(scope);
        pollLiveData(scope);
        cubismDemo();
    }

    addDynamicBehaviour(document);
});
