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
            var key = $(this).data("ld-key");
            var seq = $(this).data("ld-seq");

            var xhr = null;

            function poll(e) {
                xhr = $.get("/ui/poll", {k : key, s: seq}, "json");

                xhr.then(function(x) {
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
                })
                .then(function() {
                    poll(e);
                });
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

    function cubismCharts(scope) {
        var cubismDiv = $("#cubism");

        if (!cubismDiv.length) {
            return;
        }

        var context = cubism.context()
                        .step(2000)
                        .size(cubismDiv.width());

        // Maybe there's a neater way to do this with d3?
        $(document).bind("DOMNodeRemoved", function(e) {
            if (e.target == cubismDiv) {
                context.stop();
            }
        });

        // Should constrain D3 selection to scope, but how?
        d3.select("#cubism").selectAll(".axis")
            .data(["top", "bottom"])
            .enter().append("div")
            .attr("class", function(d) { return d + " axis"; })
            .each(function(d) {
                d3.select(this).call(context.axis().ticks(12).orient(d)); });

        d3.select("#cubism").append("div")
            .attr("class", "rule")
            .call(context.rule());

        var selected_statistic = 0;

        var new_data = function(data_fn) {
                // data_fn is passed an array of the existing data items and
                // returns the new data items.

                var selection =
                    d3.select("#cubism").selectAll(".horizon");

                // Bind tests to nodes.
                var binding = selection
                    .data(function() { return data_fn(selection.data()); },
                          function(metric) { return metric.key; });

                // Handle new nodes.
                binding.enter().insert("div", ".bottom")
                    .attr("class", "horizon")
                    .call(context.horizon()
                            .format(d3.format(",.3r"))
                            .colors(["#225EA8",
                                     "#41B6C4",
                                     "#A1DAB4",
                                     "#FFFFCC",
                                     "#FECC5C",
                                     "#FD8D3C",
                                     "#F03B20",
                                     "#BD0026"]));

                binding.exit().remove();

                binding.sort(function(a, b) {
                        return d3.ascending(a.test.test, b.test.test);
                    });

                context.on("focus", function(i) {
                    d3.selectAll(".value")
                    .style("right",
                            i == null ? null : context.size() - i + "px");
                });
            };

        $("#sample").on('livedata', function(_e, k, x) {
            if (k === "sample") {
                new_data(
                    function(existing) {
                        var by_test= {};

                        $(existing).each(function() {
                            by_test[this.test.test] = this;
                         });

                        var result = $.map(
                                x.data.tests,
                                function(t) {
                                    return cubismMetric(by_test[t.test],
                                                        x.data.timestamp,
                                                        t,
                                                        selected_statistic);
                                });

                        var totalTest = { test :"Total",
                                          description : null,
                                          statistics : x.data.totals };

                        result.push(cubismMetric(by_test[totalTest.test],
                                                 x.data.timestamp,
                                                 totalTest,
                                                 selected_statistic));

                        return result;
                      });
            }
        });

        function cubismMetric(existing, timestamp, test, statistic) {
            var metric;

            if (existing) {
                metric = existing;
            }
            else {
                // We may want to replace this with a binary tree.
                // For now we just have an array in timestamp order.
                // Each element is an array pair of timestamp and statistic.
                // We assume that we're called with increasing timestamps.
                var stats = [];

                var average = function(ss, s) {
                        if (ss.length == 0) {
                            return NaN;
                        }

                        var total =
                            ss.reduce(function(x, y) { return x + y[s]; }, 0);

                        return total / ss.length;
                    };

                var metric_fn = function(statistic) {
                        return function(start, stop, step, callback) {
                            var values = [];

                            start = +start; // Date -> timestamp.
                            var x, ss;

                            var previousBetween = function() {
                                    var d = stats.length - 1;

                                    return function(s, e) {
                                        x = stats[d];

                                        while (x && x[0] >= s) {
                                            d -= 1;
                                            if (x[0] < e) {
                                                return x[1];
                                            }

                                            x = stats[d];
                                        }
                                    };
                                }();

                            for (var i = +stop; i > start; i-= step) {
                                ss = [];

                                while (x = previousBetween(i - step, i)) {
                                    ss.push(x);
                                }

                                values.unshift(average(ss, statistic));
                            }

                            callback(null, values);
                        };
                    };

                var description;

                if (test.description) {
                    description = test.test + " [" + test.description + "]";
                }
                else {
                    description = test.test;
                }

                function create_metric(s) {
                    var metric = context.metric(metric_fn(s), description);
                    metric.key = test.test + "-" + s;
                    metric.test = test;
                    metric.stats = stats;
                    metric.with_statistic = create_metric;
                    return metric;
                };

                metric = create_metric(statistic);
            }

            // Trim old stats?
            metric.stats.push([timestamp, test.statistics]);

            return metric;
        }

        $("input[name=chart-statistic][value=" + selected_statistic + "]")
            .prop('checked', true);

        $("input[name=chart-statistic]").change(
                function(_e, k, x) {
                    selected_statistic = this.value;

                    new_data(function(existing) {
                        return $.map(existing, function(old) {
                            return old.with_statistic(selected_statistic);
                        });
                    });
                });
    }

    function addDataPanels(scope) {
        var old_state = null;

        $("#sample", scope).on('livedata', function(_e, k, x) {
            var s = x.data.status;

            for (var y in s) {
                $("#data-summary #" + y).html(s[y]);
            }

            if (s.state != old_state) {
                if (s.state === "Stopped") {
                    $("#data-summary").parent()
                        .stop().animate({opacity: 0}, "slow");
                }
                else {
                    $("#data-summary").parent()
                        .stop().animate({opacity: 0.9}, "fast");
                }

                old_state = s.state;
            }
        });
    }

    function addDynamicBehaviour(scope) {
        addButtons(scope);
        addChangeDetection(scope);
        pollLiveData(scope);
        cubismCharts(scope);
        addDataPanels(scope);
    }

    addDynamicBehaviour(document);
});
