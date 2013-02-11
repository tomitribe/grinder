jQuery(function($) {
    var ld_xhrs = [];

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
            // console.log("Registering " + this);
            var seq = -1;

            function poll(e) {
                // console.log("Polling " + e);
                xhr = $.get("/ui/poll", {k : e.id, s: seq}, function(x) {
                    // console.log("Update " + x);

                    $(e)
                    .stop()
                    .animate({opacity: 0.5},
                            "fast",
                            function() {
                                $(this).html(x.html);
                                $(this).animate({opacity: 1}, "fast");
                            });

                    seq = x.sequence;

                    // Dispatch in timer - directly calling poll()
                    // causes FF to spin sometimes.
                    setTimeout(function() {poll(e);}, 1);
                },
                "json");

                ld_xhrs.push(xhr);
            }

            poll(this);
        });
    }

    function stopLiveDataPolls() {
        // Maybe scope ld_xhrs so we don't stop them all.
        $(ld_xhrs).each(function() { this.abort(); });
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
        stopLiveDataPolls();
        addButtons(scope);
        addChangeDetection(scope);
        pollLiveData(scope);
    }

    addDynamicBehaviour(document);
});
