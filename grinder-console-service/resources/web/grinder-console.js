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
                var original = this.value;
                this.modified = function() {
                    return original != this.value;
                };
            }

            $(this).change(function(e) {
                // This is wrong if multiple controls share the same label.
                $(e.target.label).toggleClass("changed", e.target.modified());

                submit.visible(changeables.filter(function(x) {
                    return this.modified();
                }).length);
            });
        });
    }

    function createPoller(e) {

        var tokens = {}; // key => token
        var listeners = {}; // key => {listeners}
        var xhr = null;

        var poller = {
            poll : function() {
                if (xhr != null) {
                    xhr.abort();
                    xhr = null;
                }

                if ($.isEmptyObject(tokens)) {
                    return;
                }

                var p = this;

                xhr = $.getJSON("/ui/poll", tokens);

                xhr.then(function(x) {
                    $.each(x, function(_k, v) {
                        if (tokens.hasOwnProperty(v.key)) {
                            $.each(listeners[v.key],
                                   function() { this(v.key, v.value); });

                            tokens[v.key] = v.next;
                        }
                        else {
                            console.warn("Ignoring value with unknown key", v);
                        }
                    });
                })
                .then(function() {
                    p.poll();
                });
            },

            subscribe : function(e, k, t, f) {
                var p = this;

                var unsubscribe = function() {
                    var l = listeners[k];

                    if (l) {
                        delete l[f];

                        if ($.isEmptyObject(l)) {
                            delete listeners[k];
                            delete tokens[k];
                            p.poll();
                        }
                    }
                };

                unsubscribe();

                $(document).bind("DOMNodeRemoved", function(r) {
                    if (r.target == e) {
                        unsubscribe();
                    }
                });

                var l = listeners[k] || { };
                l[f] = f;
                listeners[k] = l;

                if (!t || tokens[k] && tokens[k] != t) {
                    // No token supplied, or there is a listener registered
                    // for a different token. Request the current value.
                    tokens[k] = "-1";
                }
                else {
                    tokens[k] = t;
                }

                this.poll();
            },
        };

        return poller;
    };

    var poller = createPoller(document);

    function addLiveDataElements(scope) {

        $(".ld-display").each(function() {
            var t = $(this);

            poller.subscribe(this, t.data("ld-key"), t.data("ld-token"),
                function(k, v) {
                    t.html(v);
                });
        });

        $(".ld-animate").each(function() {
            var t = $(this);

            poller.subscribe(this, t.data("ld-key"), t.data("ld-token"),
                function(k, v) {
                    t
                    .stop()
                    .animate({opacity: 0.5},
                            "fast",
                            function() {
                                $(this)
                                    .html(v)
                                    .animate({opacity: 1}, "fast");
                            });
                });
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
                d3.select(this).call(context.axis().orient(d)); });

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

        $("#cubism").each(function() {
            poller.subscribe(this, "sample", undefined, function(k, v) {
                new_data(
                    function(existing) {
                        var by_test= {};

                        $(existing).each(function() {
                            by_test[this.test.test] = this;
                         });

                        var result = $.map(
                                v.tests,
                                function(t) {
                                    return cubismMetric(by_test[t.test],
                                                        v.timestamp,
                                                        t,
                                                        selected_statistic);
                                });

                        var totalTest = { test :"Total",
                                          description : null,
                                          statistics : v.totals };

                        result.push(cubismMetric(by_test[totalTest.test],
                                                 v.timestamp,
                                                 totalTest,
                                                 selected_statistic));

                        return result;
                    });
                });
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
                            ss.reduce(function(x, y) {
                                    var v = y[s];
                                    if (typeof(v) == "number") {
                                        return x + v;
                                    }

                                    return x;
                                }
                                , 0);

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
        var data_state = null;
        var process_threads = null;

        poller.subscribe(scope, "sample", undefined, function(k, v) {
            var s = v.status;
            var d = $("#data-summary");

            d.html(s.description);

            if (s.state != data_state) {
                if (s.state === "Stopped") {
                    d.parent().stop().animate({opacity: 0}, "slow");
                }
                else {
                    d.parent().stop().animate({opacity: 0.9}, "fast");
                }

                data_state = s.state;
            }
        });

        poller.subscribe(scope, "threads", undefined, function(k, v) {
            var p = $("#process-summary");

            p.html("<span>a:" + v.agents + " </span>" +
                   "<span>w:" + v.workers + " </span>" +
                   "<span>t:" + v.threads + " </span>");

            if (v.threads != process_threads) {
                if (v.threads === 0) {
                    p.parent().stop().animate({opacity: 0}, "slow");
                }
                else {
                    p.parent().stop().animate({opacity: 0.9}, "fast");
                }

                process_threads = v.threads;
            }
        });
    }

    function addDynamicBehaviour(scope) {
        addButtons(scope);
        addChangeDetection(scope);
        addLiveDataElements(scope);
        cubismCharts(scope);
    }

    addDataPanels(document);
    addDynamicBehaviour(document);
});
