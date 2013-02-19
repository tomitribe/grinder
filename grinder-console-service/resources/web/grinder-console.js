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
                    else if (ee.hasClass("live-data-display")) { // TODO Better name
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

    function addDynamicBehaviour(scope) {
        addButtons(scope);
        addChangeDetection(scope);
        pollLiveData(scope);

        $("#test").on('livedata', function() {console.log(arguments);});
    }

    addDynamicBehaviour(document);
});
