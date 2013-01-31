jQuery(function($) {
    function addChangeDetection() {
        var changeables = $(".changeable");

        if (!changeables.length) {
            return;
        }

        $("label").each(function() {
            var l = this;
            if (l.htmlFor != '') {
                var e = document.getElementById(l.htmlFor);

                if (e) {
                    e.label = this;
                } else {
                    $(document.getElementsByName(l.htmlFor)).each(function() {
                        this.label = l;
                    });
                }
            }
        });

        jQuery.fn.visible = function(show) {
            return this.css("visibility", show ? "visible" : "hidden");
        };

        var saveButton = $("#submit");
        saveButton.visible(false);

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

                saveButton.visible(changeables.filter(function(x) {
                    return this.modified();
                }).length);
            });
        });
    }

    addChangeDetection();
});
